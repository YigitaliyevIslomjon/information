package uz.info.information

enum class Role {
    ADMIN,
    USER
}

enum class FileStatus{
    ACTIVE,
    INACTIVE,
    DONE, // kuni o'tib ketda done bo'ladi
}

enum class ErrorCode(val code: Int) {
    USERNAME_EXIST(100),
    FILE_NOT_FOUND (101),
    GRAPHIC_TIME_NOT_FOUND (102),
    LOCAL_DATE_AND_GRAPHIC_TIME_MUST_BE_UNIQUE(103),
    FILE_GRAPHIC_NOT_FOUND(104)
}
