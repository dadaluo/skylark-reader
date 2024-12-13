package cn.luoym.bookreader.skylarkreader.properties

import com.intellij.openapi.util.IconLoader

class Constants {
    companion object {
        const val PLUGIN_NAME = "SkylarkReader"


        const val LOCAL_RESOURCE = "sr-local/"

        const val SCROLLBAR_CSS = "<link href=\"/SkylarkReader/sr-local/css/scrollbar.css\" rel=\"stylesheet\" type=\"text/css\"/>"

        const val FONT_SIZE_NAME = "font-size"

        const val FONT_FAMILY_NAME = "font-family"

        const val FONT_SIZE_SUFFIX = "px"

        const val SETTINGS_CHANGED_TOPIC = "skylarkReaderSettingsChangedTopic"

        const val BUNDLE_NAME = "messages.MessageBundle"

        @JvmStatic
        val ICON = IconLoader.getIcon("icon/pluginIcon.svg", this::class.java)

    }
}
