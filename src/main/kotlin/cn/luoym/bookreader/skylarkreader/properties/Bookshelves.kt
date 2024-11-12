package cn.luoym.bookreader.skylarkreader.properties

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
import java.io.Serializable


@Service
@State(name = "Bookshelves", storages = [Storage(value = "SkylarkReaderBookshelves.xml")])
class Bookshelves : PersistentStateComponent<Bookshelves.State> {

    private val log = logger<ConsoleViewImpl>()

    var bookshelves: LinkedHashMap<Long, BookState> = LinkedHashMap()

    fun addBook(path: String) {
        val file = File(path)
        addBook(file)
    }

    fun addBook(file: File) {
        log.info("add book ${file.absolutePath} to bookshelves")
        val properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
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
        val bookState = BookState(currentTimeMillis, file.name, 1, properties.fontSize, file.absolutePath)
        bookshelves[currentTimeMillis] = bookState
    }

    fun removeBook(bookState: BookState) {
        bookshelves.remove(bookState.id)
    }

    override fun getState(): State? {
        val bookshelvesState = State()
        bookshelvesState.bookshelves = bookshelves
        return bookshelvesState
    }

    override fun loadState(p0: State) {
        this.bookshelves = state!!.bookshelves
    }

    class State {
        var bookshelves: LinkedHashMap<Long, BookState> = LinkedHashMap()
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

    constructor()

    constructor(id: Long, bookName: String, index: Int, fontSize: Int, path: String) {
        this.id = id
        this.bookName = bookName
        this.fontSize = fontSize
        this.index = index
        this.path = path
    }
}

