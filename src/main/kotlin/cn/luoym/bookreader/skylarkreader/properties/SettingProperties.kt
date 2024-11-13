package cn.luoym.bookreader.skylarkreader.properties

import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import java.io.Serializable


@Service
@State(name = "SettingProperties", storages = [Storage(value = "SkylarkReaderSettings.xml")])
class SettingProperties : PersistentStateComponent<SettingsState> {

    var fontFamily: String = "YouYuan"

    var fontSize: Int = 20

    var pageSize: Int = 5000


    override fun getState(): SettingsState? {
        val settingPropertiesState = SettingsState()
        settingPropertiesState.fontFamily = fontFamily
        settingPropertiesState.fontSize = fontSize
        settingPropertiesState.pageSize = pageSize
        return settingPropertiesState
    }

    override fun loadState(p0: SettingsState) {
        fontFamily = p0.fontFamily
        fontSize = p0.fontSize
        pageSize = p0.pageSize
    }
}

class SettingsState : Serializable {

    var fontFamily: String = "YouYuan"

    var fontSize: Int = 14

    var pageSize: Int = 3000
}