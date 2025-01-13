package cn.luoym.bookreader.skylarkreader.listener

import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.extensions.Context
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.openapi.project.ProjectManager


class WebPageColorHandler: EditorColorsListener {

    override fun globalSchemeChange(schema: EditorColorsScheme?) {
        ProjectManager.getInstance().openProjects.forEach {
            val context = Context.instance(it)
            if (context.currentBook is EpubBook) {
                context.htmlReaderUI?.reload()
            }
        }
    }

}