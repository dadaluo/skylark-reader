package cn.luoym.bookreader.skylarkreader.book

abstract class AbstractBook(val id: Long, val bookName: String, var index: Int, var maxIndex: Int, var fontSize: Int, val path: String,) {

    abstract fun doRead():String

    abstract fun isFinished():Boolean
}