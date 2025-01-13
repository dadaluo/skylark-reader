package cn.luoym.bookreader.skylarkreader.listener

import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.message.SettingsChangedNotifier
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import com.intellij.openapi.project.ProjectManager

class SettingChangedHandler : SettingsChangedNotifier {

    override fun onFontStyleChanged() {
        ProjectManager.getInstance().openProjects.forEach {
            val context = Context.instance(it)
            context.textReadConsole?.updateFontStyle()
        }
    }

    override fun onPageSizeChanged() {
        val bookshelves = Bookshelves.instance
        bookshelves.resetBookPageIndex()
    }

    override fun onEpubBookFontChanged() {
        ProjectManager.getInstance().openProjects.forEach {
            val context = Context.instance(it)
            context.htmlReaderUI?.updateFontStyle()
        }
    }

    override fun onTextReaderUIChanged() {
        val instance = SettingsProperties.instance
        ProjectManager.getInstance().openProjects.forEach {
            val context = Context.instance(it)
            if (instance.textReaderUI == TextReaderUIEnum.CONSOLE && context.currentReaderUI == context.statusBarWidget) {
                context.currentReaderUI?.exit()
            }
        }
    }
}