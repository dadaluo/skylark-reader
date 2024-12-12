package cn.luoym.bookreader.skylarkreader.book

enum class BookTypeEnum {

    TEXT_BOOK,

    EPUB_BOOK,

    WEB_SITE,

    NOT_SUPPORTED,
    ;

    companion object {

        fun bookType(fileName: String): BookTypeEnum {
            val split = fileName.split(".")
            val name = split[split.size - 1]
            when (name) {
                "epub" -> return EPUB_BOOK
                "text", "txt", "log" -> return TEXT_BOOK
            }
            return NOT_SUPPORTED
        }
    }
}