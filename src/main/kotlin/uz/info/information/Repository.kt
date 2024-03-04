package uz.info.information

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.sql.Time
import java.time.LocalDate
import java.util.Date
import java.util.UUID

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}

interface GraphicTimeRepository : JpaRepository<GraphicTime, Long> {
    fun existsByTime(time: Time): Boolean

    fun findByTime(time: Time): GraphicTime?

    @Query(value = "select * from graphic_time g where g.delete = false", nativeQuery = true)
    fun getAllTime(): List<GraphicTime>
}

interface FileGraphicRepository : JpaRepository<FileGraphic, Long> {
    fun findAllByLocalDateBetween(startDate: LocalDate, endDate: LocalDate, pageable: Pageable): Page<FileGraphic>
    fun findAllByLocalDateBetweenAndStatus(
        startDate: LocalDate, endDate: LocalDate,
        status: FileStatus,
        pageable: Pageable
    ): Page<FileGraphic>

    fun findByFileAttachment(fileAttachment: FileAttachment): FileGraphic?
}

interface FileAttachmentRepository : JpaRepository<FileAttachment, UUID> {

}

interface FileGraphicTimeRepository : JpaRepository<FileGraphicTime, Long> {
    fun existsByLocalDateAndAndGraphicTime(localDate: LocalDate, graphicTime: GraphicTime): Boolean

    fun findByFileGraphic(fileGraphic: FileGraphic): FileGraphicTime?

    fun findByFileGraphicAndGraphicTime(fileGraphic: FileGraphic, graphicTime: GraphicTime): FileGraphicTime?

    @Query(value = "SELECT * FROM file_graphic_time  WHERE file_graphic_id = :fileGraphicId AND graphic_time_id NOT IN (:graphicTimeIds)", nativeQuery = true)
    fun findAllByFileGraphicAndGraphicTime_IdNotIn(fileGraphicId: Long, graphicTimeIds: List<Long>):  List<FileGraphicTime>
//    fun findAllByFileGraphicAndGraphicTime_IdNotIn(
//        fileGraphic: FileGraphic,
//        graphicTimeIds: Collection<Long>
//    ): List<FileGraphicTime>

    @Query(value = "select graphic_time_id from file_graphic_time where file_graphic_id = :fileGraphicId", nativeQuery = true)
    fun findAllGraphicTimesByFileGraphic(fileGraphicId: Long): List<Long>
}

