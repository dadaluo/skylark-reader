package cn.luoym.bookreader.skylarkreader.extensions

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory

class ReaderToolWindowFactory: ToolWindowFactory, DumbAware {

    override fun createToolWindowContent(
        project: Project,
        toolWindow: ToolWindow
    ) {
        val instance = Context.instance(project)
        instance.toolWindow = toolWindow
        instance.showBookshelvesUI()
    }

}