package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.action.InputPageAction
import cn.luoym.bookreader.skylarkreader.action.ReaderUIExitAction
import cn.luoym.bookreader.skylarkreader.book.TextBook
import cn.luoym.bookreader.skylarkreader.extensions.Context
import cn.luoym.bookreader.skylarkreader.properties.SettingsProperties
import com.intellij.codeWithMe.ClientId.Companion.currentOrNull
import com.intellij.execution.actions.ClearConsoleAction
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons.General
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.diagnostic.logger
import com.intellij.openapi.editor.ClientEditorManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.editor.impl.softwrap.SoftWrapAppliancePlaces
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.Icon

class ReaderConsoleUI(
    project: Project,
    toolWindow: ToolWindow,
    book: TextBook,
) : ConsoleViewImpl(project,  GlobalSearchScope.projectScope(project), false, false), ReaderUI, PluginUI {

    private val log = logger<ConsoleViewImpl>()

    var book: TextBook = book
        set(value){
            field = value
            field.spinner = jBIntSpinner
            field.resetPageIndex()
        }

    private lateinit var jBIntSpinner:JBIntSpinner

    private var readerContent: Content

    private var activated = false

    init {
        this.component
        val handler = NopProcessHandler()
        handler.startNotify()
        this.attachToProcess(handler)
        editorInit()
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, createActionGroup(), true)
        toolbar.targetComponent = this
        this.add(toolbar.component, BorderLayout.NORTH)
        readerContent =  ContentFactory.getInstance().createContent(this, "", false)
        showReadContent()
    }

    private fun editorInit() {
        val properties =
            SettingsProperties.instance
        updateFontStyle()
        val editorx = this.editor
        if (editorx is EditorEx) {
            editorx.scrollPane.verticalScrollBar
                .addAdjustmentListener {
                    if (properties.autoTurnPage && isStickingToEnd(editorx)) {
                        if (editorx.document.text.isNotBlank()){
                            pageChange(1, true)
                        }
                    }
                }
        }

    }

    override fun uiContent(): Content {
        return readerContent
    }

    override fun showReadContent() {
        activated = true
        pageChange(0, false)
    }

    override fun clearReadContent() {
        clear()
    }

    override fun isActive(): Boolean {
        return activated
    }

    override fun exit() {
        activated = false
        book.spinner = null
        clear()
    }

    override fun updateFontStyle() {
        val properties =
            SettingsProperties.instance
        val scheme = this.editor?.colorsScheme
        scheme?.editorFontName = properties.fontFamily
        scheme?.editorFontSize = properties.fontSize
    }

    override fun nextPage() {
        pageChange(1, true)
    }

    override fun prevPage() {
        pageChange(-1, false)
    }

    override fun dispose() {
        exit()
        val instance = Context.instance(project)
        instance.textReadConsole = null
        if (instance.currentReaderUI == this){
            instance.currentReaderUI = null
        }
        super.dispose()
    }

    private fun createActionGroup(): DefaultActionGroup {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(ReaderUIExitAction("Exit", this))
        actionGroup.add(PrePageAction("PrePage"))
        actionGroup.add(NextPageAction("NextPage"))
        val switchSoftWrapsAction: AnAction =
            object : ToggleUseSoftWrapsToolbarAction(SoftWrapAppliancePlaces.CONSOLE) {
                override fun getEditor(e: AnActionEvent): Editor? {
                    val editor = this@ReaderConsoleUI.editor
                    return if (editor == null) null else ClientEditorManager.getClientEditor(editor, currentOrNull)
                }
            }
        val autoScrollToTheEndAction: AnAction = ScrollToTheEndToolbarAction(
            editor
        )
        actionGroup.add(switchSoftWrapsAction)
        actionGroup.add(autoScrollToTheEndAction)
        actionGroup.add(ClearThisConsoleAction(this))
        jBIntSpinner = JBIntSpinner(book.pageIndex, 1, book.maxPageIndex, 1)
        val icon: Icon = IconLoader.getIcon("icon/JumpTo.svg", this::class.java)
        actionGroup.add(JumpPageAction("JumpTo", icon, jBIntSpinner))
        actionGroup.add(InputPageAction(jBIntSpinner))
        return actionGroup
    }

    /**
     * @param indexChange 分页的变更量
     * @param append 是否在console中追加文本。true: 在console的现有文本信息后追加文本信息，false: 清空console中信息后展示文本信息
     */
    fun pageChange(indexChange: Int, append: Boolean) {
        val add = book.addPageIndex(indexChange)
        if (add) {
            val read = this.book.doRead()
            if (!append) {
                clear()
            }
            myCancelStickToEnd = true
            print(read, ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    class ClearThisConsoleAction(private val myConsoleView: ConsoleView) : ClearConsoleAction() {

        override fun update(e: AnActionEvent) {
            val enabled = this.myConsoleView.contentSize > 0
            e.presentation.isEnabled = enabled
        }

        override fun actionPerformed(e: AnActionEvent) {
            this.myConsoleView.clear()
        }
    }

    inner class PrePageAction(name: String) : AnAction(name, "", General.ChevronLeft) {
        override fun actionPerformed(event: AnActionEvent) {
            pageChange(-1, false)
        }
    }

    inner class NextPageAction(name: String) : AnAction(name, "", General.ChevronRight) {

        override fun actionPerformed(p0: AnActionEvent) {
            pageChange(1, true)
        }
    }

    inner class JumpPageAction(name: String, icon: Icon, private val intSpinner: JBIntSpinner) : AnAction(name, "", icon) {

        override fun actionPerformed(p0: AnActionEvent) {
            val value = intSpinner.value as Int
            log.info("page index is $value")
            pageChange(value - book.pageIndex, false)
        }
    }

}