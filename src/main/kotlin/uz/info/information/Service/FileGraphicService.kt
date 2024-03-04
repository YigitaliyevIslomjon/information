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

    fun getOne(id: Long): FileGraphicOneDtoResponse
    fun getAll(
        startDate: LocalDate?,
        endDate: LocalDate?,
        status: FileStatus?,
        pageable: Pageable
    ): Page<FileGraphicDtoResponse>

    fun changeStatus(id: Long, dto: FileGraphicStatusDto): Result
    /*
        fun delete(id: Long): Result
    */
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
        val existFileGraphicByAttachment = fileGraphicRepository.findByFileAttachment(fileAttachment)

        if (existFileGraphicByAttachment != null) {
            throw FileAlreadyConnectedGraphicException("file id ${fileAttachment.id} connected to graphic already, choose another file")
        }
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
                throw LocalDateAndGraphicTimeMustBeUniqueException("this kind of localDate $localDate and graphicTime id ${graphicTime.id} is present")
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

        val existFileGraphicByAttachment = fileGraphicRepository.findByFileAttachment(fileAttachment)

        if (existFileGraphicByAttachment != null && existFileGraphicByAttachment.id != fileGraphic.id) {
            throw FileAlreadyConnectedGraphicException("file id ${fileAttachment.id} connected to graphic already, choose another file")
        }

        val updatedFileGraphic = fileGraphic.also {
            it.title = title
            it.localDate = localDate
            it.fileAttachment = fileAttachment
        }

        fileGraphicRepository.save(updatedFileGraphic)

        graphicTimes.forEach {
            val graphicTime =
                graphicTimeRepository.findByIdOrNull(it)
                    ?: throw GraphicTimeNotFoundException("graphicTime $it id not found")

            if (graphicTime.delete) {
                throw GraphicTimeDeletedException("this graphicTime id $id is deleted, remove this id from list")
            }

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
                    throw LocalDateAndGraphicTimeMustBeUniqueException("choose another localDate, $localDate and GraphicTime id ${graphicTime.id}  is present")
                }
            } else if (connectedFileGraphicTime.localDate != localDate) {
                val isExistFileGraphicTime =
                    fileGraphicTimeRepository.existsByLocalDateAndAndGraphicTime(localDate, graphicTime)
                if (!isExistFileGraphicTime) {
                    fileGraphicTimeRepository.save(
                        connectedFileGraphicTime.also { item ->
                            item.fileGraphic = updatedFileGraphic
                            item.localDate = localDate
                            item.graphicTime = graphicTime
                        }
                    )
                } else {
                    throw LocalDateAndGraphicTimeMustBeUniqueException("choose another localDate, $localDate and GraphicTime id ${graphicTime.id}  is present")
                }
            }

            val graphicTimes =
                fileGraphicTimeRepository.findAllByFileGraphicAndGraphicTime_IdNotIn(fileGraphic.id!!, dto.graphicTimes)
            fileGraphicTimeRepository.deleteAll(graphicTimes)
        }

        return Result("data are edited successfully")
    }

    /*    override fun delete(id: Long): Result {
            fileGraphicRepository.findByIdOrNull(id) ?: throw FileGraphicNotException("fileGraphic id $id is not exist")
            fileGraphicRepository.deleteById(id)
            return Result("fileGraphic are deleted")
        }*/

    override fun getOne(id: Long): FileGraphicOneDtoResponse {
        val fileGraphic = fileGraphicRepository.findByIdOrNull(id)
            ?: throw FileGraphicNotException("fileGraphic id $id is not exist")
        val fileGraphicTimeIds = fileGraphicTimeRepository.findAllGraphicTimesByFileGraphic(fileGraphic.id!!)
        val fileGraphicTimes = graphicTimeRepository.findAllById(fileGraphicTimeIds)

        return FileGraphicOneDtoResponse.toResponse(fileGraphic, fileGraphicTimes)
    }

    override fun getAll(
        startDate: LocalDate?,
        endDate: LocalDate?,
        status: FileStatus?,
        pageable: Pageable
    ): Page<FileGraphicDtoResponse> {


        val response =
            if (startDate != null && endDate != null && status != null) {
                fileGraphicRepository.findAllByLocalDateBetweenAndStatus(startDate, endDate, status, pageable)
            } else if (status == null && startDate != null && endDate != null) {
                fileGraphicRepository.findAllByLocalDateBetween(startDate, endDate, pageable)
            } else {
                fileGraphicRepository.findAll(pageable)
            }
        return response.map(FileGraphicDtoResponse.Companion::toResponse)
    }

    override fun changeStatus(id: Long, dto: FileGraphicStatusDto): Result {
        val existingFileGraphic = fileGraphicRepository.findByIdOrNull(id)
            ?: throw FileGraphicNotException("fileGraphic id $id is not exist")

        existingFileGraphic.status = dto.status
        fileGraphicRepository.save(existingFileGraphic)
        return Result("status are saved successfully")
    }
}