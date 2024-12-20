package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PrevPageAction: AnAction() {
    override fun actionPerformed(p0: AnActionEvent) {
        var instance = Context.instance
        instance.currentReaderUI?.prevPage()
    }
}