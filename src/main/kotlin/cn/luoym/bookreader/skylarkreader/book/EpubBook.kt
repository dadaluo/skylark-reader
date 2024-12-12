package cn.luoym.bookreader.skylarkreader.book

import cn.luoym.bookreader.skylarkreader.properties.BookState
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import com.intellij.openapi.application.ApplicationManager
import io.documentnode.epub4j.domain.Book
import io.documentnode.epub4j.domain.Resource
import io.documentnode.epub4j.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException

class EpubBook(path: String) : AbstractBook() {
    val properties: SettingProperties
    val book: Book
    val pagePathIndexMap:Map<String, Int>

    init {
        this.path = path
        this.bookType = BookTypeEnum.EPUB_BOOK
        properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            throw FileNotFoundException("文件${path}不存在！")
        }
        bookName = file.name
        FileInputStream(file).use {
            val reader = EpubReader()
            book = reader.readEpub(it)
            val references = book.spine.spineReferences
            maxPageIndex = references.size
            pagePathIndexMap = HashMap(maxPageIndex)
            for (i in 0..maxPageIndex - 1 ) {
                val reference = references[i]
                pagePathIndexMap[reference.resource.href] = i
            }
        }
    }

    constructor(bookState: BookState) : this(bookState.path!!) {
        id = bookState.id!!
        bookName = bookState.bookName!!
        pageIndex = bookState.index!!
        path = bookState.path!!
    }

    override fun doRead(): String {
        val spine = book.spine
        val spineReferences = spine.spineReferences
        val spineReference = spineReferences[pageIndex]
        return id.toString() + "/" +spineReference.resource.href
    }



    fun staticResource(path: String): Resource?{
        pagePathIndexMap[path]?.let {
            pageIndex = it
        }
         return book.resources.resourceMap[path]
    }
}