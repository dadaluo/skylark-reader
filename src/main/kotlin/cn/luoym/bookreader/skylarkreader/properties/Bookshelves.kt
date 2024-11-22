package cn.luoym.bookreader.skylarkreader.properties

import cn.luoym.bookreader.skylarkreader.BookTypeEnum
import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.utils.sendNotify
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.notification.NotificationType
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.xmlb.annotations.Attribute
import java.io.File
import java.io.FileNotFoundException
import java.io.Serializable


@Service
@State(name = "Bookshelves", storages = [Storage(value = "SkylarkReaderBookshelves.xml")])
class Bookshelves : PersistentStateComponent<Bookshelves.State> {

    private val log = logger<ConsoleViewImpl>()

    var bookshelves: LinkedHashMap<Long, AbstractBook> = LinkedHashMap()

    fun addBook(path: String) {
        val file = File(path)
        addBook(file)
    }

    fun addBook(file: File) {
        log.info("add book ${file.absolutePath} to bookshelves")
        if (!file.exists() || !file.isFile) {
            sendNotify("文件${file.path}不存在", NotificationType.ERROR)
            return
        }
        val any = bookshelves.values.any { it.path == file.absolutePath }
        if (any) {
            sendNotify("《${file.name}》已经在书架中", NotificationType.INFORMATION)
            return
        }
        val currentTimeMillis = System.currentTimeMillis()
        val typeEnum = BookTypeEnum.bookType(file.name)
        when (typeEnum) {
            BookTypeEnum.TEXT_BOOK -> {
                val book = TextBook(file.path)
                bookshelves[currentTimeMillis] = book
            }
            BookTypeEnum.EPUB_BOOK -> {
                val book = EpubBook(file.path)
                bookshelves[currentTimeMillis] = book
            }
            BookTypeEnum.WEB_SITE -> {

            }
            BookTypeEnum.NOT_SUPPORTED -> {
                sendNotify("《${file.name}》不支持该类型文件", NotificationType.WARNING)
            }
        }


    }

    fun removeBook(bookState: AbstractBook) {
        bookshelves.remove(bookState.id)
    }

    fun resetBookPageIndex(){
        bookshelves.values.forEach {
            if (it is TextBook) {
                it.resetPageIndex()
            }
        }
    }

    override fun getState(): State? {
        val bookshelvesState = State()
        bookshelves.values.forEach {
            if (it is TextBook){
                bookshelvesState.bookStateMap[it.id] = BookState(
                    it.id, it.bookName, it.index, it.fontSize, it.path,
                    BookTypeEnum.TEXT_BOOK
                )
            } else if (it is EpubBook) {
                bookshelvesState.bookStateMap[it.id] = BookState(
                    it.id, it.bookName, it.pageIndex, it.fontSize, it.path,
                    BookTypeEnum.EPUB_BOOK
                )
            }
        }
        return bookshelvesState
    }

    override fun loadState(p0: State) {
        p0.bookStateMap.forEach {
            try {
                val value = it.value
                if (value.bookType == null){
                    value.bookType = BookTypeEnum.bookType(value.bookName!!)
                }
                when (value.bookType!!) {
                    BookTypeEnum.TEXT_BOOK -> {
                        val textBook = TextBook(it.value)
                        bookshelves[textBook.id] = textBook
                    }
                    BookTypeEnum.EPUB_BOOK -> {
                        val epubBook = EpubBook(it.value)
                        bookshelves[epubBook.id] = epubBook
                    }
                    BookTypeEnum.WEB_SITE -> {

                    }
                    BookTypeEnum.NOT_SUPPORTED -> {

                    }
                }

            } catch (e: FileNotFoundException) {
                sendNotify(e.localizedMessage, NotificationType.ERROR)
            }
        }
    }

    class State : Serializable{
        var bookStateMap: LinkedHashMap<Long, BookState> = LinkedHashMap()
    }


}

class BookState : Serializable {
    @Attribute
    var id: Long? = null
    @Attribute
    var bookName: String? = null
    @Attribute
    var index: Int? = null
    @Attribute
    var fontSize: Int? = null
    @Attribute
    var path: String? = null

    @Attribute
    var bookType: BookTypeEnum? = null

    constructor()

    constructor(id: Long, bookName: String, index: Int, fontSize: Int, path: String, bookType: BookTypeEnum) {
        this.id = id
        this.bookName = bookName
        this.fontSize = fontSize
        this.index = index
        this.path = path
        this.bookType = bookType
    }
}

