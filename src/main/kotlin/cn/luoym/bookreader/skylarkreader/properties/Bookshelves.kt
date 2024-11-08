package cn.luoym.bookreader.skylarkreader.properties

import com.intellij.execution.impl.ConsoleViewImpl
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
@State(name = "SettingProperties", storages = [Storage(value = "SkylarkReaderBookshelves.xml")])
class Bookshelves : PersistentStateComponent<Bookshelves.State> {

    private val log = logger<ConsoleViewImpl>()

    var bookshelves: LinkedHashMap<Long, BookState> = LinkedHashMap()

    fun addBook(bookState: BookState) {
        bookshelves[bookState.id] = bookState
    }

    fun addBook(path: String) {
        val properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        val any = bookshelves.values.any { it.path == path }
        if (any) {
            log.info("该文件已经在书架中")
            return
        }
        val file = File(path)
        if (!file.exists() || !file.isFile) {
            throw FileNotFoundException("文件不存在")
        }
        val currentTimeMillis = System.currentTimeMillis()
        val bookState = BookState(currentTimeMillis, file.name, 1, properties.fontSize, path)
        bookshelves[currentTimeMillis] = bookState
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

class BookState(
    @Attribute
    val id: Long,
    @Attribute
    val bookName: String,
    @Attribute
    var index: Int,
    @Attribute
    var fontSize: Int,
    @Attribute
    val path: String
) : Serializable {

}

