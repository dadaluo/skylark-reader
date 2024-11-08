package cn.luoym.bookreader.skylarkreader.toolwindows

import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import cn.luoym.bookreader.skylarkreader.ui.SettingUI
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.openapi.util.NlsContexts
import org.jetbrains.annotations.NonNls
import javax.swing.JComponent

class SettingWindowsFactory: SearchableConfigurable {

    private var settingUI: SettingUI? = null

    override fun getId(): @NonNls String {
        return "skylark-reader-settings"
    }

    override fun getDisplayName(): @NlsContexts.ConfigurableName String? {
        return "Skylark Reader"
    }

    override fun isModified(): Boolean {
        val properties = ApplicationManager.getApplication().getService(SettingProperties::class.java);
        return properties.fontSize != settingUI?.fontSize ||
                properties.pageSize != settingUI?.pageSize ||
                properties.fontFamily != settingUI?.fontFamily
    }

    override fun createComponent(): JComponent? {
        if (settingUI == null) {
            settingUI = SettingUI()
        }
        return settingUI?.setting
    }

    override fun apply() {
        val properties = ApplicationManager.getApplication().getService(SettingProperties::class.java);
        properties.fontSize = settingUI?.fontSize ?: properties.fontSize
        properties.fontFamily = settingUI?.fontFamily ?: properties.fontFamily
        properties.pageSize = settingUI?.pageSize ?: properties.pageSize
        //ReaderToolWindowFactory.Companion.readerUI?.fontChange()
    }
}