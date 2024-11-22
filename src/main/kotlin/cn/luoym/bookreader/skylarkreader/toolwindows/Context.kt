package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import cn.luoym.bookreader.skylarkreader.ui.HtmlReaderUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderConsoleUI
import com.intellij.openapi.components.Service
import org.jetbrains.ide.BuiltInServerManager
import java.util.UUID

@Service
class Context {

    lateinit var readerToolWindowFactory: ReaderToolWindowFactory

    var textReadConsole: ReaderConsoleUI? = null

    var htmlReaderUI: HtmlReaderUI? = null

    var bookshelvesUI: BookshelvesUI? = null

    var currentBook:AbstractBook? = null

    val serverUrl: String

    val uuid = UUID.randomUUID().toString()

    init {
        serverUrl = "http://localhost:${BuiltInServerManager.getInstance().port}/$uuid"
    }
}