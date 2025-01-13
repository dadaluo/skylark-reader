package cn.luoym.bookreader.skylarkreader.extensions

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.properties.Constants
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import cn.luoym.bookreader.skylarkreader.ui.HtmlReaderUI
import cn.luoym.bookreader.skylarkreader.ui.PluginUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderConsoleUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import cn.luoym.bookreader.skylarkreader.ui.StatusBarWidgetReaderUI
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service
import com.intellij.openapi.externalSystem.service.execution.NotSupportedException
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.impl.status.widget.StatusBarWidgetsManager
import org.jetbrains.ide.BuiltInServerManager

@Service(Service.Level.PROJECT)
class Context(private val project: Project) {

    companion object {
        fun instance(project: Project): Context = project.service()
    }

    lateinit var toolWindow: ToolWindow

    var textReadConsole: ReaderConsoleUI? = null

    var htmlReaderUI: HtmlReaderUI? = null

    var bookshelvesUI: BookshelvesUI? = null

    var statusBarWidget: StatusBarWidgetReaderUI? = null

    var currentReaderUI: ReaderUI? = null

    var currentBook:AbstractBook? = null

    val serverUrl: String =  "http://localhost:${BuiltInServerManager.getInstance().port}/${Constants.PLUGIN_NAME}"


    fun showBookshelvesUI() {
        if (bookshelvesUI == null) {
            bookshelvesUI = BookshelvesUI(project)
        }

        toolWindow.contentManager.let {
            it.removeAllContents(false)
            it.addContent(bookshelvesUI!!.uiContent())
        }
    }


    fun showReaderConsole(book: AbstractBook) {
        val readerUI = findReaderUI(book)
        currentReaderUI = readerUI
        if (readerUI is PluginUI) {
            val manager = toolWindow.contentManager
            manager.removeAllContents(false)
            manager.addContent(readerUI.uiContent())
        }
        readerUI.showReadContent()
    }

    private fun findReaderUI(book: AbstractBook): ReaderUI {
        if (book is TextBook) {
            val instance = SettingsProperties.instance
            if (instance.textReaderUI == TextReaderUIEnum.STATUS_BAR_WIDGET) {
                val widgetsManager = ApplicationManager.getApplication().getService(StatusBarWidgetsManager::class.java)
                widgetsManager.updateWidget(ReaderStatusBarWidgetFactory::class.java)
                statusBarWidget?.let {
                    it.book = book
                    return it
                }
            }
            if (textReadConsole == null) {
                textReadConsole = ReaderConsoleUI(project, toolWindow, book)
            }
            textReadConsole!!.book = book
            return textReadConsole!!
        } else if (book is EpubBook) {
            if (htmlReaderUI == null) {
                htmlReaderUI = HtmlReaderUI(project, toolWindow, book)
            }
            htmlReaderUI!!.book = book
            return htmlReaderUI!!
        }
        throw NotSupportedException("不支持的类型")
    }

}