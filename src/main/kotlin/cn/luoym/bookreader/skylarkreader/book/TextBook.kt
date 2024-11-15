package cn.luoym.bookreader.skylarkreader.book

import cn.luoym.bookreader.skylarkreader.properties.BookState
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import com.intellij.openapi.application.ApplicationManager
import org.apache.fontbox.ttf.BufferedRandomAccessFile
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets

class TextBook(path: String) : AbstractBook() {
    val randomAccessFile: RandomAccessFile
    val properties: SettingProperties
    var index = 0

    init {
        this.path = path
        properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            throw FileNotFoundException("文件${path}不存在！")
        }
        randomAccessFile = BufferedRandomAccessFile(file, "r", properties.pageSize * 10)
        maxPageIndex = randomAccessFile.length().div(properties.pageSize).toInt() + 1
        bookName = file.name
        fontSize = properties.fontSize
    }

    constructor(bookState: BookState) : this(bookState.path!!) {
        id = bookState.id!!
        bookName = bookState.bookName!!
        index = bookState.index!!
        path = bookState.path!!
        fontSize = bookState.fontSize!!
        pageIndex = bookState.index!!.div(properties.pageSize).toInt() + 1
    }

    fun getBookPosition(): Long {
        if (pageIndex > maxPageIndex) {
            return index.toLong()
        }
        index = (pageIndex - 1) * properties.pageSize
        return index.toLong()
    }

    override fun doRead(): String {
        val bytes = ByteArray(properties.pageSize)
        randomAccessFile.seek(getBookPosition())
        randomAccessFile.read(bytes, 0, properties.pageSize)
        return String(bytes, StandardCharsets.UTF_8)
    }

    override fun isFinished():Boolean{
        return pageIndex >= maxPageIndex
    }

}
