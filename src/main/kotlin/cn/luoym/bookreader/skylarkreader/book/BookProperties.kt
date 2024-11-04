package cn.luoym.bookreader.skylarkreader.book

import com.intellij.openapi.components.Service


@Service
class BookProperties() {

    var font: String? = null

    var fontSize: Float = 13.0F

    var pageSize: Int = 1000
}