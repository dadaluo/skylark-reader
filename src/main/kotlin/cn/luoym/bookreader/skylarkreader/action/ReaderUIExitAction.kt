package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.ui.ReaderUI
import com.intellij.icons.AllIcons.Actions
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager

class ReaderUIExitAction(name: String, val readerUI: ReaderUI): AnAction(name, "", Actions.Exit) {

    override fun actionPerformed(p0: AnActionEvent) {
        val context = Context.instance
        context.readerToolWindowFactory.showBookshelvesUI()
        readerUI.exit()
        context.currentReaderUI = null
    }
}