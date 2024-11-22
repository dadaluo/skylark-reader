package cn.luoym.bookreader.skylarkreader.book

import cn.luoym.bookreader.skylarkreader.BookTypeEnum
import cn.luoym.bookreader.skylarkreader.properties.BookState
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.vfs.VirtualFile
import io.documentnode.epub4j.domain.Book
import io.documentnode.epub4j.domain.Resource
import io.documentnode.epub4j.epub.EpubReader
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.nio.charset.Charset

class EpubBook(path: String) : AbstractBook() {
    val properties: SettingProperties
    val book: Book

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
            maxPageIndex = book.tableOfContents.tocReferences.size
        }
    }

    constructor(bookState: BookState) : this(bookState.path!!) {
        id = bookState.id!!
        bookName = bookState.bookName!!
        pageIndex = bookState.index!!
        path = bookState.path!!
        fontSize = bookState.fontSize!!
    }

    override fun doRead(): String {
        val spine = book.spine
        val spineReferences = spine.spineReferences
        val spineReference = spineReferences[pageIndex]
        return spineReference.resource.href
    }

    private fun pageHref(): String {
        val references = book.tableOfContents.tocReferences

        val reference = references[pageIndex]
        val resource = reference.resource
        return resource.href
    }

    fun staticResource(path: String): Resource?{
         return book.resources.resourceMap[path]
    }
}