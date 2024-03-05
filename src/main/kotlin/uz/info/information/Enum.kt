package uz.info.information

enum class Role {
    ADMIN,
    USER
}

enum class FileStatus{
    ACTIVE,
    INACTIVE,
    DONE,
}

enum class ErrorCode(val code: Int) {
    USERNAME_EXIST(100),
    FILE_NOT_FOUND (101),
    GRAPHIC_TIME_NOT_FOUND (102),
    GRAPHIC_TIME_DELETED (104),
    GRAPHIC_TIME_ALREADY_EXIST (105),
    LOCAL_DATE_AND_GRAPHIC_TIME_MUST_BE_UNIQUE(106),
    FILE_GRAPHIC_NOT_FOUND(107),
    FILE_ALREADY_CONNECTED_GRAPHIC(108),

}
