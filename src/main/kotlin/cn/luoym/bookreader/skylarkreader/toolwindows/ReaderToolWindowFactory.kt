package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import cn.luoym.bookreader.skylarkreader.ui.ReaderConsoleUI
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory


class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {

    private var readerConsole: ReaderConsoleUI? = null

    private lateinit var readerContent: Content

    private var bookshelvesUI: BookshelvesUI? = null

    private lateinit var bookshelvesContent: Content

    private lateinit var project: Project

    private lateinit var toolWindow: ToolWindow

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
        if (readerConsole == null) {
            readerConsole = ReaderConsoleUI(project, toolWindow, book)
            readerContent = ContentFactory.getInstance().createContent(readerConsole, "", false)
        } else {
            readerConsole!!.book = book
        }
        val manager = toolWindow.contentManager
        manager.removeAllContents(false)
        manager.addContent(readerContent)
        readerConsole?.pageChange(false)
    }

    fun showBookshelvesUI() {
        if (bookshelvesUI == null) {
            bookshelvesUI = BookshelvesUI()
            bookshelvesContent = ContentFactory.getInstance().createContent(bookshelvesUI!!.bookshelves, "", false)
        }
        val manager = toolWindow.contentManager
        manager.removeAllContents(false)
        manager.addContent(bookshelvesContent)
    }




}