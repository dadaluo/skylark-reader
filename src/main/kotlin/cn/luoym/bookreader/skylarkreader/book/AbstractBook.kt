package cn.luoym.bookreader.skylarkreader.book

import com.intellij.openapi.options.FontSize

abstract class AbstractBook (val bookName: String, var index: Int,var maxIndex: Int, var fontSize: Float) {

    abstract fun doRead():String
}