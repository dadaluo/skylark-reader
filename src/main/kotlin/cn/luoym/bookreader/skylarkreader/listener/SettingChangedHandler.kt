package cn.luoym.bookreader.skylarkreader.listener

import cn.luoym.bookreader.skylarkreader.message.SettingsChangedNotifier
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import com.intellij.openapi.application.ApplicationManager

class SettingChangedHandler : SettingsChangedNotifier {

    override fun onFontStyleChanged() {
        val context = Context.instance
        context.textReadConsole?.updateFontStyle()

    }

    override fun onPageSizeChanged() {
        val bookshelves = Bookshelves.instance
        bookshelves.resetBookPageIndex()
    }

    override fun onEpubBookFontChanged() {
        val context = Context.instance
        context.htmlReaderUI?.updateFontStyle()
    }

    override fun onTextReaderUIChanged() {
        var instance = SettingsProperties.instance
        var context = Context.instance
        if (instance.textReaderUI == TextReaderUIEnum.CONSOLE && context.currentReaderUI == context.statusBarWidget){
            context.currentReaderUI?.exit()
        }
    }
}