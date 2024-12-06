package cn.luoym.bookreader.skylarkreader.book

import cn.luoym.bookreader.skylarkreader.BookTypeEnum
import cn.luoym.bookreader.skylarkreader.properties.BookState
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import com.intellij.openapi.application.ApplicationManager
import org.apache.fontbox.ttf.BufferedRandomAccessFile
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat

class TextBook(path: String) : AbstractBook() {
    val randomAccessFile: RandomAccessFile
    val properties: SettingProperties
    var index = 0

    init {
        this.path = path
        this.bookType = BookTypeEnum.TEXT_BOOK
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

    fun resetPageIndex(){
        pageIndex = index / properties.pageSize + 1
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
        val position = getBookPosition()
        randomAccessFile.seek(position)
        randomAccessFile.read(bytes, 0, properties.pageSize)
        var byteIndex = bytes.size - 1
        var cnByteSize = 0
        while (byteIndex > 0) {
            val byte = bytes[byteIndex]
            if (byte >= 0) {
                break
            }
            cnByteSize++
            byteIndex--
        }
        val i = cnByteSize % 3
        var copyOfRange = bytes.copyOfRange(0, bytes.size - i)
        var preByteIndex = 0
        var preCnByteSize = 0
        while (preByteIndex < copyOfRange.size) {
            val byte = copyOfRange[preByteIndex]
            if (byte >= 0) {
                break
            }
            preByteIndex++
            preCnByteSize++
        }
        val cnCount = preCnByteSize % 3
        if (cnCount > 0) {
            val addCount = 3 - cnCount
            val currentPosition = position - addCount
            randomAccessFile.seek(currentPosition)
            val preAddByte = ByteArray(addCount)
            randomAccessFile.read(preAddByte, 0, addCount)
            copyOfRange = preAddByte + copyOfRange
        }
        return String(copyOfRange, StandardCharsets.UTF_8)
    }

    override fun readingProgress(): String{
        val decimalFormat = DecimalFormat("0.00%")
        val lengthDecimal = BigDecimal(randomAccessFile.length())
        val indexDecimal = BigDecimal(index)
        val divide = indexDecimal.divide(lengthDecimal, 4, RoundingMode.HALF_UP)
        return decimalFormat.format(divide)
    }
}

