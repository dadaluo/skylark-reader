package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.BookTypeEnum
import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import cn.luoym.bookreader.skylarkreader.ui.HtmlReaderUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderConsoleUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.externalSystem.service.execution.NotSupportedException
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {


    private lateinit var project: Project

    private lateinit var toolWindow: ToolWindow

    val context: Context = ApplicationManager.getApplication().getService<Context>(Context::class.java)

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        this.project = project
        this.toolWindow = toolWindow
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        context.readerToolWindowFactory = this
        showBookshelvesUI()
    }

    fun showReaderConsole(book: AbstractBook) {
        val readerUI = findReaderUI(book)
        val manager = toolWindow.contentManager
        manager.removeAllContents(false)
        manager.addContent(readerUI.uiContent())
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
        if (book.bookType == BookTypeEnum.TEXT_BOOK) {
            if (context.textReadConsole == null) {
                context.textReadConsole = ReaderConsoleUI(project, toolWindow, book)
            }
            context.textReadConsole!!.book = book
            return context.textReadConsole!!
        } else if (book.bookType == BookTypeEnum.EPUB_BOOK) {
            if (context.htmlReaderUI == null) {
                context.htmlReaderUI = HtmlReaderUI(project, toolWindow, book)
            }
            context.htmlReaderUI!!.book = book
            return context.htmlReaderUI!!
        }
        throw NotSupportedException("不支持的类型")
    }

}