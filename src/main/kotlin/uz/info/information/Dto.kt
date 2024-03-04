package uz.info.information


import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size

import java.sql.Time
import java.time.LocalDate
import java.util.UUID


data class SignInDTO(
    @field:NotBlank
    @field:Size(max = 50)
    val username: String,
    @field:NotBlank
    val password: String
)

data class UserDto(
    @field:NotBlank
    @field:Size(max = 50)
    val firstName: String,
    @field:NotBlank
    @field:Size(max = 50)
    val lastName: String,
    @field:NotBlank
    @field:Size(max = 50)
    var username: String,
    @field:NotBlank
    var password: String,
    @field:NotEmpty
    val roles: List<String>
)

data class FileAttachmentDtoResponse(
    val id: UUID,
) {
    companion object {
        fun toResponse(fileAttachment: FileAttachment): FileAttachmentDtoResponse {
            return FileAttachmentDtoResponse(
                fileAttachment.id!!,
            )
        }
    }
}

data class FileGraphicDto(
    @field: NotNull
    val fileId: UUID,
    @field:NotBlank
    val title: String,
    @field:NotEmpty
    val graphicTimes: List<Long>,
    @field:NotNull
    val localDate: LocalDate
)

data class FileGraphicStatusDto(
    @field: NotNull
    val status: FileStatus,
)

data class FileGraphicDtoResponse(
    val id: Long,
    val title: String,
    val status: FileStatus,
    val localDate: LocalDate,
    val fileAttachmentId: UUID,
) {
    companion object {
        fun toResponse(fileGraphic: FileGraphic): FileGraphicDtoResponse {
            return FileGraphicDtoResponse(
                fileGraphic.id!!,
                fileGraphic.title,
                fileGraphic.status,
                fileGraphic.localDate,
                fileGraphic.fileAttachment.id!!,
            )
        }
    }
}

data class FileGraphicOneDtoResponse(
    val id: Long,
    val title: String,
    val status: FileStatus,
    val localDate: LocalDate,
    val fileAttachmentId: UUID,
    val graphicItems: List<GraphicTime>
) {
    companion object {
        fun toResponse(fileGraphic: FileGraphic, graphicItems: List<GraphicTime>): FileGraphicOneDtoResponse {
            return FileGraphicOneDtoResponse(
                fileGraphic.id!!,
                fileGraphic.title,
                fileGraphic.status,
                fileGraphic.localDate,
                fileGraphic.fileAttachment.id!!,
                graphicItems
            )
        }
    }
}


data class GraphicTimeDto(
    @field:NotNull
    val time: Time
)

data class GraphicTimeDtoResponse(
    val id: Long,
    val time: Time,
    val delete: Boolean
) {
    companion object {
        fun toResponse(graphicTime: GraphicTime): GraphicTimeDtoResponse {
            return GraphicTimeDtoResponse(
                graphicTime.id!!,
                graphicTime.time,
                graphicTime.delete
            )
        }
    }
}

data class Result(
    val message: String
)