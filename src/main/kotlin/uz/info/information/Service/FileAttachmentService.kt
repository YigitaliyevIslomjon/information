package uz.info.information.Service

import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.util.FileCopyUtils
import org.springframework.web.multipart.MultipartHttpServletRequest
import uz.info.information.FileAttachment
import uz.info.information.FileAttachmentRepository
import uz.info.information.FileNotFoundException
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

interface FileAttachmentService {
    fun add(request: MultipartHttpServletRequest): UUID
    fun getOne(id: UUID, response: HttpServletResponse)
}

@Service
class FileAttachmentServiceImpl(
    private val fileAttachmentRepository: FileAttachmentRepository,
    @Value("\${saved-file-folder}") var savedFileFolder: String,
) : FileAttachmentService {

    override fun add(request: MultipartHttpServletRequest): UUID {
        val fileNames = request.fileNames
        val file = request.getFile(fileNames.next())
        val fileAttachment: FileAttachment
        if (file != null) {
            try {
                val fileOriginalName = file.originalFilename
                val size = file.size
                val contentType = file.contentType
                val split = fileOriginalName!!.split("\\.")
                val name = UUID.randomUUID().toString() + split[split.size - 1]
                val newAttachment = FileAttachment(
                    originalFileName = fileOriginalName,
                    size = size,
                    contentType = contentType!!,
                    path = name
                )
                fileAttachment = fileAttachmentRepository.save(newAttachment)
                val path = Paths.get("${savedFileFolder}/$name")
                Files.copy(file.inputStream, path)
                return fileAttachment.id!!
            } finally {
                file.inputStream.close()
            }
        } else {
            throw FileNotFoundException("file is null")
        }

    }

    override fun getOne(id: UUID, response: HttpServletResponse) {
        val existFileAttachment =
            fileAttachmentRepository.findByIdOrNull(id) ?: throw FileNotFoundException("File id $id is not found")
        response.setHeader("Content-Disposition", "attachemnt; filename=\"${existFileAttachment.originalFileName}\" ")
        response.contentType = existFileAttachment.contentType
        val fileInputStream = FileInputStream("$savedFileFolder/" + existFileAttachment.path)
        FileCopyUtils.copy(fileInputStream, response.outputStream)
    }
}