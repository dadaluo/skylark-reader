package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.utils.ReaderBundle
import com.intellij.openapi.actionSystem.AnAction

class ClearContentAction: AnAction(ReaderBundle.message("skylark.reader.action.text.next.clear")) {
    override fun actionPerformed(p0: com.intellij.openapi.actionSystem.AnActionEvent) {
        p0.project?.let {
            val context = Context.instance(it)
            context.currentReaderUI?.clearReadContent()
        }

    }
}