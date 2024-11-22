package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

class ReaderUIExitAction(name: String): AnAction(name, "", Actions.Exit) {

    override fun actionPerformed(p0: AnActionEvent) {
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        context.readerToolWindowFactory.showBookshelvesUI()
    }
}