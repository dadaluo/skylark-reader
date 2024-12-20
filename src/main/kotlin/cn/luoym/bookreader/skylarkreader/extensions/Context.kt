package cn.luoym.bookreader.skylarkreader.extensions

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.properties.Constants
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import cn.luoym.bookreader.skylarkreader.ui.HtmlReaderUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderConsoleUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import cn.luoym.bookreader.skylarkreader.ui.StatusBarWidgetReaderUI
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import org.jetbrains.ide.BuiltInServerManager

@Service
class Context {

    companion object {
        val instance: Context
            get() = service()
    }

    lateinit var readerToolWindowFactory: ReaderToolWindowFactory

    var textReadConsole: ReaderConsoleUI? = null

    var htmlReaderUI: HtmlReaderUI? = null

    var bookshelvesUI: BookshelvesUI? = null

    var statusBarWidget: StatusBarWidgetReaderUI? = null

    var currentReaderUI: ReaderUI? = null

    var currentBook:AbstractBook? = null

    val serverUrl: String =  "http://localhost:${BuiltInServerManager.getInstance().port}/${Constants.PLUGIN_NAME}"

}