package uz.info.information

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate
import java.util.Date
import java.util.UUID

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}

interface GraphicTimeRepository : JpaRepository<GraphicTime, Long> {
}

interface FileGraphicRepository : JpaRepository<FileGraphic, Long> {
//    @Query(value = "select f.graphic_time_id from file_graphic_time f where f.file_graphic_id = :fileGraphicId", nativeQuery = true)
//    fun findGraphicTimesByFileGraphicId(fileGraphicId: UUID): List<graphicTimes>

    fun findAllByLocalDateBetween(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<FileGraphic>
    fun findAllByLocalDateBetweenAndStatus(
        startDate: LocalDate, endDate: LocalDate,
        status: FileStatus,
        pageable: Pageable
    ): Page<FileGraphic>
}

interface FileAttachmentRepository : JpaRepository<FileAttachment, UUID> {

}

interface FileGraphicTimeRepository : JpaRepository<FileGraphicTime, Long> {
    fun existsByLocalDateAndAndGraphicTime(localDate: LocalDate, graphicTime: GraphicTime): Boolean

    fun findByFileGraphic(fileGraphic: FileGraphic): FileGraphicTime?

    fun findByFileGraphicAndGraphicTime(fileGraphic: FileGraphic, graphicTime: GraphicTime): FileGraphicTime?

    fun findAllByFileGraphicAndGraphicTime_IdNotIn(
        fileGraphic: FileGraphic,
        graphicTimeIds: Collection<Long>
    ): List<FileGraphicTime>
}

