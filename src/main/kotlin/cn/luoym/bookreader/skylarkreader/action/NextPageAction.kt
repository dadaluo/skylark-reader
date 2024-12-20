package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class NextPageAction: AnAction() {

    override fun actionPerformed(p0: AnActionEvent) {
        val context = Context.instance
        context.currentReaderUI?.nextPage()
    }
}