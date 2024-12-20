package cn.luoym.bookreader.skylarkreader.extensions

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import cn.luoym.bookreader.skylarkreader.ui.HtmlReaderUI
import cn.luoym.bookreader.skylarkreader.ui.PluginUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderConsoleUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import com.intellij.openapi.externalSystem.service.execution.NotSupportedException
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {


    private lateinit var project: Project

    private lateinit var toolWindow: ToolWindow

    val context: Context = Context.instance

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        this.project = project
        this.toolWindow = toolWindow
        val context = Context.instance
        context.readerToolWindowFactory = this
        showBookshelvesUI()
    }

    fun showReaderConsole(book: AbstractBook) {
        val readerUI = findReaderUI(book)
        context.currentReaderUI = readerUI
        if (readerUI is PluginUI) {
            val manager = toolWindow.contentManager
            manager.removeAllContents(false)
            manager.addContent(readerUI.uiContent())
        }
        readerUI.showReadContent()
    }

    fun showBookshelvesUI() {
        if (context.bookshelvesUI == null) {
            context.bookshelvesUI = BookshelvesUI()
        }
        val manager = toolWindow.contentManager
        manager.removeAllContents(false)
        manager.addContent(context.bookshelvesUI!!.uiContent())
    }

    fun findReaderUI(book: AbstractBook): ReaderUI {
        if (book is TextBook) {
            var instance = SettingsProperties.instance
            if (instance.textReaderUI == TextReaderUIEnum.STATUS_BAR_WIDGET && context.statusBarWidget != null) {
                context.statusBarWidget?.book = book
                return context.statusBarWidget!!
            }
            if (context.textReadConsole == null) {
                context.textReadConsole = ReaderConsoleUI(project, toolWindow, book)
            }
            context.textReadConsole!!.book = book
            return context.textReadConsole!!
        } else if (book is EpubBook) {
            if (context.htmlReaderUI == null) {
                context.htmlReaderUI = HtmlReaderUI(project, toolWindow, book)
            }
            context.htmlReaderUI!!.book = book
            return context.htmlReaderUI!!
        }
        throw NotSupportedException("不支持的类型")
    }

}