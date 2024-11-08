package cn.luoym.bookreader.skylarkreader.book

import com.intellij.openapi.components.Service


@Service
class BookProperties() {

    var fontFamily: String = "YouYuan"

    var fontSize: Int = 20

    var pageSize: Int = 5000
}