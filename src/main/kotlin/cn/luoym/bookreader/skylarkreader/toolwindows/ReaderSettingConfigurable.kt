package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import cn.luoym.bookreader.skylarkreader.ui.SettingsUI
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class ReaderSettingConfigurable: SearchableConfigurable {

    private var settingsUI: SettingsUI? = null

    override fun getId(): @NonNls String {
        return "skylark-reader-settings"
    }

    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        return "Skylark Reader"
    }

    override fun isModified(): Boolean {
        val properties = ApplicationManager.getApplication().getService(SettingProperties::class.java);
        return properties.fontSize != settingsUI?.fontSize ||
                properties.pageSize != settingsUI?.pageSize ||
                properties.fontFamily != settingsUI?.fontFamily ||
                properties.autoTurnPage != settingsUI?.autoTurnPage ||
                properties.overrideEpubFont != settingsUI?.overrideEpubFont
    }

    override fun createComponent(): JComponent? {
        if (settingsUI == null) {
            settingsUI = SettingsUI()
        }
        return settingsUI?.settings
    }

    override fun apply() {
        val properties = ApplicationManager.getApplication().getService(SettingProperties::class.java);
        properties.fontSize = settingsUI?.fontSize ?: properties.fontSize
        properties.fontFamily = settingsUI?.fontFamily ?: properties.fontFamily
        properties.autoTurnPage = settingsUI?.autoTurnPage ?: properties.autoTurnPage
        properties.overrideEpubFont = settingsUI?.overrideEpubFont ?: properties.overrideEpubFont
        if (settingsUI?.pageSize != properties.pageSize) {
            properties.pageSize = settingsUI?.pageSize ?: properties.pageSize
            val bookshelves = ApplicationManager.getApplication().getService(Bookshelves::class.java)
            bookshelves.resetBookPageIndex()
        }
    }
}