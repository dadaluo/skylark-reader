package cn.luoym.bookreader.skylarkreader.book

abstract class AbstractBook {

    var id: Long = 0

    lateinit var bookName: String

    var fontSize: Int = 13

    lateinit var path: String

    var pageIndex: Int = 1

    var maxPageIndex: Int = 1

    fun addPageIndex(int: Int):Boolean{
        val i = pageIndex + int
        if(i > maxPageIndex || i < 1){
            return false
        }
        pageIndex = i
        return true
    }

    abstract fun doRead():String

    abstract fun isFinished():Boolean
}