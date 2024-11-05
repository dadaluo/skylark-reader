package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {

    companion object {
        private var readerUI: ReaderUI? = null
    }

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val book = TextBook.create("D:/data/weblog/ddm-traffic-common/service.2024-10-31-1.log", 1)
        readerUI = ReaderUI(project, toolWindow, book)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(readerUI!!.reader, "", false)
        toolWindow.contentManager.addContent(content)
        readerUI!!.pageChage(true)
        readerUI!!.setFont()
    }
}