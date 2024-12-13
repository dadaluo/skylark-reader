package cn.luoym.bookreader.skylarkreader.properties

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import com.intellij.util.xmlb.annotations.Attribute
import java.io.Serializable


@Service
@State(name = "SettingProperties", storages = [Storage(value = "SkylarkReaderSettings.xml")])
class SettingProperties : PersistentStateComponent<SettingsState> {

    var fontFamily: String = "黑体"

    var fontSize: Int = 14

    var pageSize: Int = 3000

    var autoTurnPage: Boolean = true

    var overrideEpubFontFamily: Boolean = false

    var overrideEpubFontSize: Boolean = false

    companion object {
        val instance: SettingProperties
            get() = service()
    }


    override fun getState(): SettingsState? {
        val settings = this
        val settingPropertiesState = SettingsState().apply {
            fontFamily = settings.fontFamily
            fontSize = settings.fontSize
            pageSize = settings.pageSize
            autoTurnPage = settings.autoTurnPage
            overrideEpubFontFamily = settings.overrideEpubFontFamily
            overrideEpubFontSize = settings.overrideEpubFontSize
        }
        return settingPropertiesState
    }

    override fun loadState(p0: SettingsState) {
        fontFamily = p0.fontFamily ?: fontFamily
        fontSize = p0.fontSize ?: fontSize
        pageSize = p0.pageSize ?: pageSize
        autoTurnPage = p0.autoTurnPage?: autoTurnPage
        overrideEpubFontFamily = p0.overrideEpubFontFamily ?: overrideEpubFontFamily
        overrideEpubFontSize = p0.overrideEpubFontSize ?: overrideEpubFontSize
    }
}

class SettingsState : Serializable {

    var fontFamily: String? = null

    var fontSize: Int? = null

    var pageSize: Int? = null

    var autoTurnPage: Boolean? = null

    var overrideEpubFontFamily:Boolean? = null

    var overrideEpubFontSize: Boolean? = null

}