package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.message.SettingsChangedNotifier
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import cn.luoym.bookreader.skylarkreader.ui.SettingsForm
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class ReaderSettingConfigurable: SearchableConfigurable {

    private var settingsForm: SettingsForm? = null

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
                properties.autoTurnPage != settingsForm?.autoTurnPage?.get() ||
                epubBookFontStyleIsModified(properties)
    }

    override fun createComponent(): JComponent? {
        if (settingsForm == null) {
            settingsForm = SettingsForm()
        }
        return settingsForm
    }

    override fun apply() {
        val properties = ApplicationManager.getApplication().getService(SettingProperties::class.java);
        properties.autoTurnPage = settingsForm?.autoTurnPage?.get() ?: properties.autoTurnPage

        val pageSizeIsModified = pageSizeIsModified(properties)
        if (pageSizeIsModified) {
            properties.pageSize = settingsForm?.pageSize?.get() ?: properties.pageSize
        }

        val fontStyleIsModified = fontStyleIsModified(properties)
        if (fontStyleIsModified) {
            properties.fontSize = settingsForm?.fontSize?.get() ?: properties.fontSize
            properties.fontFamily = settingsForm?.fontFamily?.get() ?: properties.fontFamily
        }

        val epubBookFontStyleIsModified = epubBookFontStyleIsModified(properties)
        if (epubBookFontStyleIsModified) {
            properties.overrideEpubFontFamily = settingsForm?.overrideEpubFontFamily?.get() ?: properties.overrideEpubFontFamily
            properties.overrideEpubFontSize = settingsForm?.overrideEpubFontSize?.get() ?: properties.overrideEpubFontSize
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
        return properties.pageSize != settingsForm?.pageSize?.get()
    }

    fun fontStyleIsModified(properties: SettingProperties): Boolean {
        return properties.fontSize != settingsForm?.fontSize?.get() ||
                properties.fontFamily != settingsForm?.fontFamily?.get()
    }

    fun epubBookFontStyleIsModified(properties: SettingProperties): Boolean {
        return properties.overrideEpubFontFamily != settingsForm?.overrideEpubFontFamily?.get() ||
                properties.overrideEpubFontSize != settingsForm?.overrideEpubFontSize?.get()
    }

    override fun reset() {
        settingsForm?.reset()
    }

    override fun disposeUIResources() {
        settingsForm = null
    }
}