package cn.luoym.bookreader.skylarkreader.action

import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.utils.ReaderBundle
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class NextPageAction: AnAction(ReaderBundle.message("skylark.reader.action.text.next.page")) {

    override fun actionPerformed(p0: AnActionEvent) {
        val context = Context.instance
        context.currentReaderUI?.nextPage()
    }
}