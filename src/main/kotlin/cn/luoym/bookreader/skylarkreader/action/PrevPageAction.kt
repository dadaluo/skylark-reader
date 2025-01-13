package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.utils.ReaderBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PrevPageAction: AnAction(ReaderBundle.message("skylark.reader.action.text.previous.page")) {
    override fun actionPerformed(p0: AnActionEvent) {
        p0.project?.let {
            val context = Context.instance(it)
            context.currentReaderUI?.prevPage()
        }

    }
}