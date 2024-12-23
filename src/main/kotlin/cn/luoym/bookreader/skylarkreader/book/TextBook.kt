package cn.luoym.bookreader.skylarkreader.book

import cn.luoym.bookreader.skylarkreader.properties.BookState
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import org.apache.fontbox.ttf.BufferedRandomAccessFile
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.nio.charset.StandardCharsets
import java.text.DecimalFormat
import kotlin.math.max
import kotlin.math.min

class TextBook(path: String) : AbstractBook() {

    private val randomAccessFile: BufferedRandomAccessFile

    private val properties: SettingsProperties

    var position = 0

    var nexPageOffset = 0


    init {
        this.path = path
        this.bookType = BookTypeEnum.TEXT_BOOK
        properties = SettingsProperties.instance
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            throw FileNotFoundException("文件 $path 不存在！")
        }
        randomAccessFile = BufferedRandomAccessFile(file, "r", properties.pageSize * 10)
        maxPageIndex = calculateMaxPageIndex()
        bookName = file.name
    }

    constructor(bookState: BookState) : this(bookState.path!!) {
        id = bookState.id!!
        bookName = bookState.bookName!!
        position = bookState.index!!
        path = bookState.path!!
        resetPageIndex()
    }

    private fun calculateMaxPageIndex(): Int {
        return randomAccessFile.length().div(properties.pageSize).toInt() + 1
    }

    fun resetPageIndex() {
        pageIndex = position / properties.pageSize + 1
    }

    fun getBookPosition(): Int {
        if (pageIndex > maxPageIndex) {
            return position
        }
        position = (pageIndex - 1) * properties.pageSize
        return position
    }

    override fun doRead(): String {
        val bookPosition = getBookPosition()
        return readString(bookPosition, properties.pageSize)
    }

    fun readPageSize(textReaderUI: TextReaderUIEnum): Int {
        return when (textReaderUI) {
            TextReaderUIEnum.CONSOLE -> properties.pageSize
            TextReaderUIEnum.STATUS_BAR_WIDGET -> properties.widgetPageSize
        }
    }

    fun readCurrentPageContent(textReaderUI: TextReaderUIEnum): String {
        val pageSize = readPageSize(textReaderUI)
        return readString(position, pageSize)
    }

    fun indexToNextPage(textReaderUI: TextReaderUIEnum) {
        val pageSize = readPageSize(textReaderUI)
        position = min((position + pageSize + nexPageOffset), randomAccessFile.length().toInt())
    }

    fun indexToPrevPage(textReaderUI: TextReaderUIEnum) {
        val pageSize = readPageSize(textReaderUI)
        position = max(0, position - pageSize)
    }

    fun readString(position: Int, length: Int): String {
        return try {
            val bytes = ByteArray(length)
            randomAccessFile.seek(position.toLong())
            randomAccessFile.read(bytes, 0, length)
            trimIncompleteCharacters(bytes)
        } catch (e: IOException) {
            throw IOException("读取文件时发生错误", e)
        }
    }

    private fun trimIncompleteCharacters(bytes: ByteArray): String {
        var byteIndex = bytes.size - 1
        var cnByteSize = 0
        while (byteIndex >= 0 && bytes[byteIndex] < 0) {
            cnByteSize++
            byteIndex--
        }
        val i = cnByteSize % 3
        nexPageOffset = -i
        var copyOfRange = bytes.copyOfRange(0, bytes.size - i)
        var preByteIndex = 0
        var preCnByteSize = 0
        while (preByteIndex < copyOfRange.size && copyOfRange[preByteIndex] < 0) {
            preByteIndex++
            preCnByteSize++
        }
        val cnCount = preCnByteSize % 3
        if (cnCount > 0) {
            val addCount = 3 - cnCount
            position -= addCount
            randomAccessFile.seek(position.toLong())
            val preAddByte = ByteArray(addCount)
            randomAccessFile.read(preAddByte, 0, addCount)
            copyOfRange = preAddByte + copyOfRange
        }
        return String(copyOfRange, StandardCharsets.UTF_8)
    }

    override fun readingProgress(): String {
        val decimalFormat = DecimalFormat("0.00%")
        val lengthDecimal = BigDecimal(randomAccessFile.length())
        val indexDecimal = BigDecimal(position)
        val divide = indexDecimal.divide(lengthDecimal, 4, RoundingMode.HALF_UP)
        return decimalFormat.format(divide)
    }
}
