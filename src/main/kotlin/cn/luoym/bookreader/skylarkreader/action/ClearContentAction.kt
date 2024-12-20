package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import com.intellij.openapi.actionSystem.AnAction

class ClearContentAction: AnAction() {
    override fun actionPerformed(p0: com.intellij.openapi.actionSystem.AnActionEvent) {
        val context = Context.instance
        context.currentReaderUI?.clearReadContent()
    }
}