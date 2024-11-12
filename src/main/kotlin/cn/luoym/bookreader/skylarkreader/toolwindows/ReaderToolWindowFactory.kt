package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.console.ReaderConsoleView
import cn.luoym.bookreader.skylarkreader.ui.BookshelvesUI
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory


class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {

    private lateinit var readerConsoleView: ReaderConsoleView

    private lateinit var bookshelvesUI: BookshelvesUI

    private lateinit var project: Project

    private lateinit var toolWindow: ToolWindow

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        this.project = project
        this.toolWindow = toolWindow
        val book = TextBook.create("D:/data/weblog/ddm-traffic-common/service.2024-10-31-1.log", 1)
        readerConsoleView =  ReaderConsoleView(project, toolWindow, book)
        bookshelvesUI = BookshelvesUI()
        showBookshelvesUI()
    }

    fun showReaderConsole(){
        val content = ContentFactory.getInstance().createContent(readerConsoleView, "", false)
        toolWindow.contentManager.addContent(content)
    }

    fun showBookshelvesUI() {
        val content = ContentFactory.getInstance().createContent(bookshelvesUI.bookshelves, "", false)
        toolWindow.contentManager.addContent(content)
    }




}