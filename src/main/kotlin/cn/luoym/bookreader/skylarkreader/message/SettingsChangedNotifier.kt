package cn.luoym.bookreader.skylarkreader.message

import cn.luoym.bookreader.skylarkreader.properties.Constants
import com.intellij.util.messages.Topic

interface SettingsChangedNotifier {

    companion object {
        @Topic.AppLevel
        val SETTINGS_CHANGED: Topic<SettingsChangedNotifier> =
            Topic.create(Constants.SETTINGS_CHANGED_TOPIC, SettingsChangedNotifier::class.java)
    }

    fun onFontStyleChanged()

    fun onPageSizeChanged()

    fun onEpubBookFontChanged()

    fun onTextReaderUIChanged()
}