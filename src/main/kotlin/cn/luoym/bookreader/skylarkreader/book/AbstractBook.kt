package cn.luoym.bookreader.skylarkreader.book

abstract class AbstractBook (val bookName: String, var index: Int,var maxIndex: Int, var fontSize: Int) {

    abstract fun doRead():String

    abstract fun isFinished():Boolean
}