package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.properties.Constants
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import cn.luoym.bookreader.skylarkreader.properties.TextReaderUIEnum
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.DataContext
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.ListPopup
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.wm.StatusBarWidget
import com.intellij.openapi.wm.impl.status.EditorBasedStatusBarPopup
import org.jetbrains.annotations.NonNls
import javax.naming.OperationNotSupportedException

class StatusBarWidgetReaderUI(project: Project) : EditorBasedStatusBarPopup(project, false), ReaderUI {

    lateinit var book: TextBook

    private var activated = false

    private var widgetContent: String = ""

    override fun createInstance(project: Project): StatusBarWidget {
        var readerUI = StatusBarWidgetReaderUI(project)
        Context.instance.statusBarWidget = readerUI
        return readerUI
    }

    override fun createPopup(context: DataContext): ListPopup? {
        var group = ActionManager.getInstance()
            .getAction(Constants.ACTION_GROUP_ID) as ActionGroup
        return JBPopupFactory.getInstance()
            .createActionGroupPopup(
                Constants.PLUGIN_NAME,
                group,
                context,
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                true
            )
    }

    override fun getWidgetState(file: VirtualFile?): WidgetState {
        var instance = SettingsProperties.instance
        if (instance.textReaderUI == TextReaderUIEnum.CONSOLE || !isActive() || !::book.isInitialized) {
            return WidgetState.HIDDEN
        }
        return WidgetState(book.bookName, widgetContent, true)

    }

    private fun readCurrentPageContent(): String {
        val currentPageContent = book.readCurrentPageContent(TextReaderUIEnum.STATUS_BAR_WIDGET)
        val progress = book.readingProgress()
        return "$progress| $currentPageContent"
    }

    override fun ID(): @NonNls String = Constants.STATUS_BAR_WEDGET_ID

    override fun showReadContent() {
        activated = true
        widgetContent = readCurrentPageContent()
        update {
            myStatusBar?.updateWidget(Constants.STATUS_BAR_WEDGET_ID)
        }
    }

    override fun clearReadContent() {
        widgetContent = ""
        update {
            myStatusBar?.updateWidget(Constants.STATUS_BAR_WEDGET_ID)
        }
    }

    override fun updateFontStyle() {
        throw OperationNotSupportedException()
    }

    override fun isActive(): Boolean  = activated

    override fun exit() {
        widgetContent = ""
        activated = false
        Context.instance.currentReaderUI = null
    }

    override fun nextPage() {
        book.indexToNextPage(TextReaderUIEnum.STATUS_BAR_WIDGET)
        showReadContent()
    }

    override fun prevPage() {
        book.indexToPrevPage(TextReaderUIEnum.STATUS_BAR_WIDGET)
        showReadContent()
    }

    override fun dispose() {
        exit()
        if (Context.instance.currentReaderUI == this) {
            Context.instance.currentReaderUI = null
        }
        Context.instance.statusBarWidget = null
        super.dispose()
    }
}