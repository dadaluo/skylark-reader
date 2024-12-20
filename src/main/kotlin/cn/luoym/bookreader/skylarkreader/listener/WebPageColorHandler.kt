package cn.luoym.bookreader.skylarkreader.listener

import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.extensions.Context
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme


class WebPageColorHandler: EditorColorsListener {

    override fun globalSchemeChange(schema: EditorColorsScheme?) {
        val context = Context.instance
        if (context.currentBook is EpubBook){
            context.htmlReaderUI?.reload()
        }
    }

}