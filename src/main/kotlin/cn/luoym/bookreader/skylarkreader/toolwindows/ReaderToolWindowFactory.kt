package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.console.ReaderConsoleView
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {

    private lateinit var readerConsoleView: ReaderConsoleView

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val book = TextBook.create("D:/data/weblog/ddm-traffic-common/service.2024-10-31-1.log", 1)
//       kReaderUI = KReaderUI(project, toolWindow, book)
        readerConsoleView =  ReaderConsoleView(project, toolWindow, book)

        val content = ContentFactory.getInstance().createContent(readerConsoleView, "", false)
        toolWindow.contentManager.addContent(content)
    }


}