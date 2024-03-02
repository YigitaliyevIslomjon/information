package uz.info.information

import jakarta.persistence.Column
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.multipart.MultipartHttpServletRequest
import java.sql.Time
import java.time.LocalDate
import java.util.Date
import java.util.UUID


data class SignInDTO(
    val username : String,
    val password : String
)

data class UserDto (
    val firstName: String,
    val lastName: String,
    var username: String,
    var password: String,
    val roles: List<String>
)

data class FileAttachmentDtoResponse(

    val id: UUID,
    val path: String,
    val size: Long,
    val contentType: String,
    val originalFileName: String,
    ){
    companion object {
        fun toResponse(fileAttachment: FileAttachment): FileAttachmentDtoResponse {
            return FileAttachmentDtoResponse(
                fileAttachment.id!!,
                fileAttachment.path,
                fileAttachment.size,
                fileAttachment.contentType,
                fileAttachment.originalFileName
            )
        }
    }
}

data class FileGraphicDto(
    val fileId: UUID,
    val title: String,
    val graphicTimes: List<Long>,
    val localDate: LocalDate
)

data class FileGraphicDtoResponse(
    val id: Long,
    val title: String,
    val status: FileStatus,
    val localDate: LocalDate,
    val fileAttachmentId: UUID,

){
    companion object {
        fun toResponse(fileGraphic: FileGraphic): FileGraphicDtoResponse {
            return FileGraphicDtoResponse(
                fileGraphic.id!!,
                fileGraphic.title,
                fileGraphic.status,
                fileGraphic.localDate,
                fileGraphic.fileAttachment.id!!
            )
        }
    }
}


data class GraphicTimeDto(
    val time: Time
)

data class GraphicTimeDtoResponse(
    val id: Long,
    val time: Time
) {
    companion object {
        fun toResponse(graphicTime: GraphicTime): GraphicTimeDtoResponse {
            return GraphicTimeDtoResponse(
                graphicTime.id!!,
                graphicTime.time
            )
        }
    }
}

data class Result(
    val message: String
)