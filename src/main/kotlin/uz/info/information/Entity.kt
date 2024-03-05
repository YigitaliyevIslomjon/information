package uz.info.information

import jakarta.persistence.*
import java.sql.Time
import java.time.LocalDate
import java.util.UUID

@Entity(name = "users")
class User(
    @Column(nullable = false, length = 50)
    val firstName: String,
    @Column(nullable = false, length = 50)
    val lastName: String,
    @Column(nullable = false)
    val password: String,
    @Column(nullable = false, unique = true, length = 50)
    val username: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val roles: List<Role> = listOf(Role.USER),
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
    @Enumerated(EnumType.STRING)
    var status: FileStatus,
    @Column(nullable = false)
    var localDate: LocalDate,
    @OneToOne var fileAttachment: FileAttachment,
    @OneToMany(mappedBy = "fileGraphic", cascade = [CascadeType.ALL])
    var fileGraphicTimes: List<FileGraphicTime> = mutableListOf(),
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
)

@Entity
class GraphicTime(
    @Column(nullable = false, unique = true)
    var time: Time,
    var delete: Boolean = false,
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



