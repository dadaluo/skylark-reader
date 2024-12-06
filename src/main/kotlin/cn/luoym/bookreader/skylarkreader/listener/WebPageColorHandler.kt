package cn.luoym.bookreader.skylarkreader.listener

import cn.luoym.bookreader.skylarkreader.book.EpubBook
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.colors.EditorColorsListener
import com.intellij.openapi.editor.colors.EditorColorsScheme
import com.intellij.ui.ColorUtil
import com.intellij.util.ui.UIUtil


class WebPageColorHandler: EditorColorsListener {

    override fun globalSchemeChange(schema: EditorColorsScheme?) {
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        if (context.currentBook is EpubBook){
            context.htmlReaderUI?.reload()
        }
    }

}