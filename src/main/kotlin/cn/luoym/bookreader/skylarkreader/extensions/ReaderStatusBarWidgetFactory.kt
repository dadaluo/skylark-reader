package cn.luoym.bookreader.skylarkreader.extensions

import cn.luoym.bookreader.skylarkreader.properties.Constants
import cn.luoym.bookreader.skylarkreader.ui.StatusBarWidgetReaderUI
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.openapi.wm.StatusBar
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.StatusBarWidgetFactory
import org.jetbrains.annotations.NonNls

class ReaderStatusBarWidgetFactory : StatusBarWidgetFactory {

    override fun getId(): @NonNls String = Constants.STATUS_BAR_ID

    override fun getDisplayName(): @NlsContexts.ConfigurableName String = Constants.PLUGIN_NAME

    override fun createWidget(project: Project): StatusBarWidget {
        var readerUI = StatusBarWidgetReaderUI(project)
        Context.instance.statusBarWidget = readerUI
        return readerUI
    }

    override fun isAvailable(project: Project): Boolean = true

    override fun canBeEnabledOn(statusBar: StatusBar): Boolean = true
}