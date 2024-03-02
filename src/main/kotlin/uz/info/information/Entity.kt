package uz.info.information

import jakarta.persistence.*
import java.sql.Time
import java.time.LocalDate
import java.util.UUID


@Entity(name = "users")
class User(
    @Column(nullable = false)
    val firstName: String,
    @Column(nullable = false)
    val lastName: String,
    @Column(nullable = false)
    val password: String,
    @Column(nullable = false, unique = true)
    val username: String,
    @Column(nullable = false)
    val roles: List<String> = listOf("ADMIN"),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

@Entity
class GraphicTime(
    @Column(nullable = false, unique = true)
    var time: Time,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

@Entity
class FileAttachment(
    @Column(nullable = false)
    val originalFileName: String,
    @Column(nullable = false)
    val path: String,
    @Column(nullable = false)
    val size: Long,
    @Column(nullable = false)
    var contentType: String,
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null
)

@Entity
class FileGraphic(
    @Column(nullable = false)
    var title: String,
    @Column(nullable = false)
    var status: FileStatus,
    @Column(nullable = false)
    var localDate: LocalDate,
    @OneToOne var fileAttachment: FileAttachment,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

@Entity
@Table(
    uniqueConstraints = [UniqueConstraint(columnNames = ["localDate", "graphicTime_id"])]
)

class FileGraphicTime(
    @ManyToOne var fileGraphic: FileGraphic,
    @Temporal(TemporalType.DATE) var localDate: LocalDate,
    @ManyToOne var graphicTime: GraphicTime,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)



