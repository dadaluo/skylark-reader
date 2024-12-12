package cn.luoym.bookreader.skylarkreader.listener

import cn.luoym.bookreader.skylarkreader.message.SettingsChangedNotifier
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import com.intellij.openapi.application.ApplicationManager

class SettingChangedHandler : SettingsChangedNotifier {

    override fun onFontStyleChanged() {
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        context.textReadConsole?.updateFontStyle()

    }

    override fun onPageSizeChanged() {
        val bookshelves = ApplicationManager.getApplication().getService(Bookshelves::class.java)
        bookshelves.resetBookPageIndex()
    }

    override fun onEpubBookFontChanged() {
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        context.htmlReaderUI?.updateFontStyle()
    }
}