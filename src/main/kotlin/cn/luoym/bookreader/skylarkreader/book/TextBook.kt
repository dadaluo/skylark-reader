package cn.luoym.bookreader.skylarkreader.book

import com.intellij.openapi.application.ApplicationManager
import org.apache.fontbox.ttf.BufferedRandomAccessFile
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.nio.charset.StandardCharsets

class TextBook(
    bookName: String,
    index: Int,
    maxIndex: Int,
    fontSize: Int,
    val path: String,
    val randomAccessFile: RandomAccessFile
) :
    AbstractBook(bookName, index, maxIndex, fontSize) {

    companion object {

        fun create(path: String, index: Int): TextBook {
            val properties = ApplicationManager.getApplication().getService<BookProperties>(BookProperties::class.java)
            val file = File(path)
            if (!file.exists() || !file.isFile) {
                throw FileNotFoundException("文件不存在")
            }
            val length = file.length()
            val maxIndex = length.div(properties.pageSize).toInt()
            val randomAccessFile = BufferedRandomAccessFile(file, "r", properties.pageSize * 10)
            val book = TextBook(file.name, index, maxIndex, properties.fontSize, path, randomAccessFile)
            return book
        }
    }

    override fun doRead(): String {
        val properties = ApplicationManager.getApplication().getService<BookProperties>(BookProperties::class.java)
        val bytes = ByteArray(properties.pageSize)
        if (index > maxIndex) {
            return ""
        }
        randomAccessFile.seek((index - 1) * properties.pageSize.toLong())
        randomAccessFile.read(bytes, 0, properties.pageSize)
        return String(bytes, StandardCharsets.UTF_8)
    }

    override fun isFinished():Boolean{
        return index >= maxIndex
    }

}

