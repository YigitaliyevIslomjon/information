package uz.info.information.Service

import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import uz.info.information.*
import java.time.LocalDate

interface FileGraphicService {
    fun add(dto: FileGraphicDto): Result
    fun edit(
        id: Long, dto: FileGraphicDto
    ): Result

    fun delete(id: Long): Result
    fun getOne(id: Long): FileGraphicDtoResponse
    fun getAll(
        startDate: LocalDate,
        endDate: LocalDate,
        status: FileStatus?,
        pageable: Pageable
    ): Page<FileGraphicDtoResponse>

    fun changeStatus(id: Long, status: FileStatus): Result
}

@Service
class FileGraphicServiceImpl(
    private val fileAttachmentRepository: FileAttachmentRepository,
    private val graphicTimeRepository: GraphicTimeRepository,
    private val fileGraphicRepository: FileGraphicRepository,
    private val fileGraphicTimeRepository: FileGraphicTimeRepository,
) : FileGraphicService {

    @Transactional
    override fun add(
        dto: FileGraphicDto
    ): Result = dto.run {

        val fileAttachment = fileAttachmentRepository.findByIdOrNull(fileId)
            ?: throw FileNotFoundException("fileId $fileId is not present")

        val fileGraphic = FileGraphic(
            title,
            FileStatus.ACTIVE,
            localDate,
            fileAttachment
        )

        fileGraphicRepository.save(fileGraphic)

        graphicTimes.forEach {
            val graphicTime =
                graphicTimeRepository.findByIdOrNull(it)
                    ?: throw GraphicTimeNotFoundException("graphicTime $it id not found")

            val existFileGraphicTime =
                fileGraphicTimeRepository.existsByLocalDateAndAndGraphicTime(localDate, graphicTime)
            if (existFileGraphicTime) {
                throw LocalDateAndGraphicTimeMustBeUniqueException("this kind of localDate and graphicTime is present")
            } else {
                val fileGraphicTime = FileGraphicTime(
                    fileGraphic,
                    localDate,
                    graphicTime
                )
                fileGraphicTimeRepository.save(fileGraphicTime)
            }
        }

        Result("data are saved successfully")
    }

    @Transactional
    override fun edit(
        id: Long,
        dto: FileGraphicDto
    ): Result = dto.run {
        val fileGraphic = fileGraphicRepository.findByIdOrNull(id)
            ?: throw FileGraphicNotException("fileGraphic id $id is not found")

        val fileAttachment = fileAttachmentRepository.findByIdOrNull(fileId)
            ?: throw FileNotFoundException("fileId $fileId is not present")

        val updatedFileGraphic = fileGraphic.also {
            it.title = title
            it.localDate = localDate
            it.fileAttachment = fileAttachment
        }

        fileGraphicRepository.save(updatedFileGraphic)


        val fileGraphicTime = fileGraphicTimeRepository.findByFileGraphic(updatedFileGraphic)
            ?: throw FileGraphicNotException("File graphic id ${updatedFileGraphic.id} not found connected to file graphic time")

        graphicTimes.forEach {
            val graphicTime =
                graphicTimeRepository.findByIdOrNull(it)
                    ?: throw GraphicTimeNotFoundException("graphicTime $it id not found")


            val connectedFileGraphicTime =
                fileGraphicTimeRepository.findByFileGraphicAndGraphicTime(updatedFileGraphic, graphicTime)

            if (connectedFileGraphicTime == null) {
                val isExistFileGraphicTime =
                    fileGraphicTimeRepository.existsByLocalDateAndAndGraphicTime(localDate, graphicTime)
                if (!isExistFileGraphicTime) {
                    val newFileGraphicTime = FileGraphicTime(
                        updatedFileGraphic,
                        localDate,
                        graphicTime
                    )
                    fileGraphicTimeRepository.save(newFileGraphicTime)
                } else {
                    throw LocalDateAndGraphicTimeMustBeUniqueException("choose another localdate, localdate and GraphicTime is present")
                }
            }
            else if (fileGraphicTime.localDate != localDate) {
                val isExistFileGraphicTime =
                    fileGraphicTimeRepository.existsByLocalDateAndAndGraphicTime(localDate, graphicTime)
                if (!isExistFileGraphicTime) {
                    // update
                    fileGraphicTimeRepository.save(
                        fileGraphicTime.also {item->
                            item.fileGraphic = updatedFileGraphic
                            item.localDate = localDate
                            item.graphicTime = graphicTime
                        }
                    )
                } else {
                    throw LocalDateAndGraphicTimeMustBeUniqueException("choose another localDate, localdate and GraphicTime is present")
                }
            }

            fileGraphicTimeRepository.save(
                fileGraphicTime.apply {
                    this.fileGraphic = updatedFileGraphic
                }
            )

            val graphicTimes = fileGraphicTimeRepository
                .findAllByFileGraphicAndGraphicTime_IdNotIn(fileGraphic, dto.graphicTimes)

            fileGraphicTimeRepository.deleteAll(graphicTimes)
        }

        return Result("data are edited successfully")
    }

    override fun delete(id: Long): Result {
        fileGraphicRepository.findByIdOrNull(id) ?: throw FileGraphicNotException("fileGraphic id ${id} is not exist")
        fileGraphicRepository.deleteById(id)
        return Result("fileGraphic are deleted")
    }

    override fun getOne(id: Long): FileGraphicDtoResponse {
        val existingFileGraphic = fileGraphicRepository.findByIdOrNull(id)
            ?: throw FileGraphicNotException("fileGraphic id $id is not exist")
        return FileGraphicDtoResponse.toResponse(existingFileGraphic)
    }

    override fun getAll(
        startDate: LocalDate,
        endDate: LocalDate,
        status: FileStatus?,
        pageable: Pageable
    ): Page<FileGraphicDtoResponse> {
        val response = if (status == null) {
            fileGraphicRepository.findAllByLocalDateBetween(startDate, endDate, pageable)
        } else {
            fileGraphicRepository.findAllByLocalDateBetweenAndStatus(startDate, endDate, status, pageable)
        }
      return  response.map(FileGraphicDtoResponse.Companion::toResponse)
    }

    override fun changeStatus(id: Long, status: FileStatus): Result {
        val existingFileGraphic = fileGraphicRepository.findByIdOrNull(id)
            ?: throw FileGraphicNotException("fileGraphic id $id is not exist")
        
        existingFileGraphic.status = status
        fileGraphicRepository.save(existingFileGraphic)
        return Result("status are saved successfully")
    }
}