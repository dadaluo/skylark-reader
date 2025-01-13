package cn.luoym.bookreader.skylarkreader.properties

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.book.BookTypeEnum
import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.utils.sendNotify
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.notification.NotificationType
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.logger
import com.intellij.util.xmlb.annotations.Attribute
import java.io.File
import java.io.FileNotFoundException
import java.io.Serializable
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicInteger


@Service
@State(name = "Bookshelves", storages = [Storage(value = "SkylarkReaderBookshelves.xml")])
class Bookshelves : PersistentStateComponent<Bookshelves.State> {

    companion object {
        val instance: Bookshelves
            get() = service()
    }

    private val log = logger<ConsoleViewImpl>()

    private var incr: AtomicInteger = AtomicInteger(0)

    var bookshelves: LinkedHashMap<Long, AbstractBook> = LinkedHashMap()

    fun findBookById(id: Long):AbstractBook? {
        return bookshelves[id]
    }

    fun addBook(path: String) {
        val file = File(path)
        addBook(file)
    }

    fun addBook(file: File) {
        try {
            log.info("add book ${file.absolutePath} to bookshelves")
            if (!file.exists() || !file.isFile) {
                sendNotify("文件${file.path}不存在", NotificationType.ERROR)
                return
            }
            if (bookshelves.any { it.value.path == file.absolutePath }) {
                sendNotify("《${file.name}》已经在书架中", NotificationType.INFORMATION)
                return
            }
            val generalId = generalId()
            val typeEnum = BookTypeEnum.bookType(file.name)
            when (typeEnum) {
                BookTypeEnum.TEXT_BOOK -> bookshelves[generalId] = TextBook(file.path).apply { this.id = generalId }
                BookTypeEnum.EPUB_BOOK -> bookshelves[generalId] = EpubBook(file.path).apply { this.id = generalId }
                BookTypeEnum.NOT_SUPPORTED -> sendNotify("《${file.name}》不支持该类型文件", NotificationType.WARNING)
            }
        } catch (e: Exception) {
            log.error("add book error", e)
            sendNotify(e.localizedMessage, NotificationType.ERROR)
        }
    }

    private fun generalId(): Long = System.currentTimeMillis() - Constants.BASE_TIMESTAMP + incr.incrementAndGet()

    fun removeBook(book: AbstractBook) {
        bookshelves.remove(book.id)
        log.info("移除书籍 ${book.bookName} (${book.id})")
    }

    fun resetBookPageIndex() {
        bookshelves.values.filterIsInstance<TextBook>().forEach { it.resetPageIndex() }
        log.info("重置所有书籍的页码索引")
    }

    override fun getState(): State {
        val bookshelvesState = State()
        bookshelves.values.forEach {
            if (it is TextBook){
                bookshelvesState.bookStateMap[it.id] = BookState(
                    it.id, it.bookName, it.position, it.path,
                    BookTypeEnum.TEXT_BOOK
                )
            } else if (it is EpubBook) {
                bookshelvesState.bookStateMap[it.id] = BookState(
                    it.id, it.bookName, it.pageIndex,  it.path,
                    BookTypeEnum.EPUB_BOOK
                )
            }
        }
        return bookshelvesState
    }

    override fun loadState(state: State) {
        log.info("---- Bookshelves load state ----")
        Executors.newVirtualThreadPerTaskExecutor().use {
            state.bookStateMap.forEach { (id, bookState) ->
                it.execute {
                    try {
                        val book = when (bookState.bookType) {
                            BookTypeEnum.TEXT_BOOK -> TextBook(bookState)
                            BookTypeEnum.EPUB_BOOK -> EpubBook(bookState)
                            else -> null
                        }
                        book?.let {
                            bookshelves[id] = it
                            log.info("成功加载书籍 ${it.bookName} (${id})")
                        }
                    } catch (e: FileNotFoundException) {
                        log.error("加载书籍失败: ${e.localizedMessage}", e)
                        sendNotify("加载书籍失败，出现错误：${e.localizedMessage}", NotificationType.ERROR)
                    }
                }
            }
        }
        log.info("---- Bookshelves load state success ----")
    }

    class State : Serializable{
        var bookStateMap: LinkedHashMap<Long, BookState> = LinkedHashMap()
    }


}

class BookState() : Serializable {
    @Attribute
    var id: Long? = null
    @Attribute
    var bookName: String? = null
    @Attribute
    var index: Int? = null
    @Attribute
    var path: String? = null

    @Attribute
    var bookType: BookTypeEnum? = null

    constructor(id: Long, bookName: String, index: Int,  path: String, bookType: BookTypeEnum): this() {
        this.id = id
        this.bookName = bookName
        this.index = index
        this.path = path
        this.bookType = bookType
    }
}

