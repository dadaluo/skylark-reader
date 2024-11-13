package cn.luoym.bookreader.skylarkreader.book

abstract class AbstractBook {

    var id: Long = 0

    lateinit var bookName: String

    var fontSize: Int = 13

    lateinit var path: String

    var pageIndex: Int = 1

    var maxPageIndex: Int = 1

    abstract fun doRead():String

    abstract fun isFinished():Boolean
}