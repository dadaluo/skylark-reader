package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.message.SettingsChangedNotifier
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
        return fontStyleIsModified(properties) ||
                pageSizeIsModified(properties) ||
                properties.autoTurnPage != settingsUI?.autoTurnPage ||
                epubBookFontStyleIsModified(properties)
    }

    override fun createComponent(): JComponent? {
        if (settingsUI == null) {
            settingsUI = SettingsUI()
        }
        return settingsUI?.settings
    }

    override fun apply() {
        val properties = ApplicationManager.getApplication().getService(SettingProperties::class.java);
        properties.autoTurnPage = settingsUI?.autoTurnPage ?: properties.autoTurnPage

        val pageSizeIsModified = pageSizeIsModified(properties)
        if (pageSizeIsModified) {
            properties.pageSize = settingsUI?.pageSize ?: properties.pageSize
        }

        val fontStyleIsModified = fontStyleIsModified(properties)
        if (fontStyleIsModified) {
            properties.fontSize = settingsUI?.fontSize ?: properties.fontSize
            properties.fontFamily = settingsUI?.fontFamily ?: properties.fontFamily
        }

        val epubBookFontStyleIsModified = epubBookFontStyleIsModified(properties)
        if (epubBookFontStyleIsModified) {
            properties.overrideEpubFontFamily = settingsUI?.overrideEpubFontFamily ?: properties.overrideEpubFontFamily
            properties.overrideEpubFontSize = settingsUI?.overrideEpubFontSize ?: properties.overrideEpubFontSize
        }

        sendSettingsChangedMessage(properties, pageSizeIsModified, fontStyleIsModified, epubBookFontStyleIsModified)
    }

    fun sendSettingsChangedMessage(
        properties: SettingProperties,
        pageSizeIsModified: Boolean,
        fontStyleIsModified: Boolean,
        epubBookFontStyleIsModified: Boolean,
    ) {
        if (!pageSizeIsModified && !fontStyleIsModified && !epubBookFontStyleIsModified) {
            return
        }
        val publisher =
            ApplicationManager.getApplication().messageBus.syncPublisher(SettingsChangedNotifier.SETTINGS_CHANGED)
        if (pageSizeIsModified) {
            publisher.onPageSizeChanged()
        }
        if (fontStyleIsModified) {
            publisher.onFontStyleChanged()
        }
        if (epubBookFontStyleIsModified || (fontStyleIsModified && (properties.overrideEpubFontSize || properties.overrideEpubFontFamily))) {
            publisher.onEpubBookFontChanged()
        }
    }

    fun pageSizeIsModified(properties: SettingProperties): Boolean {
        return properties.pageSize != settingsUI?.pageSize
    }

    fun fontStyleIsModified(properties: SettingProperties): Boolean {
        return properties.fontSize != settingsUI?.fontSize ||
                properties.fontFamily != settingsUI?.fontFamily
    }

    fun epubBookFontStyleIsModified(properties: SettingProperties): Boolean {
        return properties.overrideEpubFontFamily != settingsUI?.overrideEpubFontFamily ||
                properties.overrideEpubFontSize != settingsUI?.overrideEpubFontSize
    }

    override fun reset() {
        settingsUI?.settingProperties()
    }
}