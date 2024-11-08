package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.console.ReaderConsoleView
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {


    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val book = TextBook.create("D:/data/weblog/ddm-traffic-common/service.2024-10-31-1.log", 1)
//       kReaderUI = KReaderUI(project, toolWindow, book)
        ReaderConsoleView(project, toolWindow, book)
    }


}