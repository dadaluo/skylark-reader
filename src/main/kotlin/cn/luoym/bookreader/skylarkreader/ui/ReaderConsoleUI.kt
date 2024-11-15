package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import com.intellij.codeWithMe.ClientId.Companion.currentOrNull
import com.intellij.execution.actions.ClearConsoleAction
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.process.NopProcessHandler
import com.intellij.execution.ui.ConsoleView
import com.intellij.execution.ui.ConsoleViewContentType
import com.intellij.icons.AllIcons.Actions
import com.intellij.icons.AllIcons.General
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.openapi.application.ApplicationManager
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
import java.awt.BorderLayout
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel

class ReaderConsoleUI(
    project: Project,
    searchScope: GlobalSearchScope,
    viewer: Boolean,
    usePredefinedMessageFilter: Boolean,
) : ConsoleViewImpl(project, searchScope, viewer, usePredefinedMessageFilter) {

    private val log = logger<ConsoleViewImpl>()


    lateinit var book: AbstractBook

    private lateinit var jBIntSpinner:JBIntSpinner


    constructor(project: Project, toolWindow: ToolWindow, book: AbstractBook) : this(
        project,
        GlobalSearchScope.projectScope(project),
        false,
        false
    ) {
        this.book = book
        this.component
        val handler = NopProcessHandler()
        handler.startNotify()
        this.attachToProcess(handler)
        editorInit()
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, createActionGroup(), true)
        toolbar.targetComponent = this
        this.add(toolbar.component, BorderLayout.NORTH)
        pageChange(0, true)
    }

    fun editorInit() {
        val properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        val scheme = this.editor.colorsScheme
        scheme.editorFontName = properties.fontFamily
        scheme.editorFontSize = book.fontSize
        val editorx = this.editor
        if (editorx is EditorEx) {
            editorx.scrollPane.verticalScrollBar
                .addAdjustmentListener {
                    if (properties.autoTurnPage && isStickingToEnd(editorx)) {
                        pageChange(1, true)
                    }
                }
        }

    }


    fun createActionGroup(): DefaultActionGroup {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(ExitAction("Exit"))
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
        actionGroup.add(LargeFontSizeAction("LargerFont"))
        actionGroup.add(SmallerFontSizeAction("SmallerFont"))
        jBIntSpinner = JBIntSpinner(book.pageIndex, 1, book.maxPageIndex, 1)
        val icon: Icon = IconLoader.getIcon("icon/JumpTo.svg", this::class.java)
        actionGroup.add(JumpPageAction("JumpTo", icon, jBIntSpinner))
        actionGroup.add(InputPageAction(jBIntSpinner))
        return actionGroup
    }

    fun pageChange(int: Int, append: Boolean) {
        val add = book.addPageIndex(int)
        if (add) {
            val read = this.book.doRead()
            if (!append) {
                clear()
            }
            myCancelStickToEnd = true
            jBIntSpinner.value = book.pageIndex
            print(read, ConsoleViewContentType.NORMAL_OUTPUT)
        }
    }

    fun fontChange() {
        if (editor is EditorEx) {
            val ex = editor as EditorEx
            ex.setFontSize(book.fontSize)
        }
    }

    class ClearThisConsoleAction(val myConsoleView: ConsoleView) : ClearConsoleAction() {

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

    inner class LargeFontSizeAction(name: String) : AnAction(name, "", General.ZoomIn) {

        override fun actionPerformed(p0: AnActionEvent) {
            if (book.fontSize >= 50) {
                return
            }
            book.fontSize += 1
            fontChange()
        }
    }

    inner class SmallerFontSizeAction(name: String) : AnAction(name, "", General.ZoomOut) {
        override fun actionPerformed(p0: AnActionEvent) {
            val book = book
            if (book.fontSize <= 8) {
                return
            }
            book.fontSize -= 1
            fontChange()
        }
    }

    inner class JumpPageAction(name: String, icon: Icon, val intSpinner: JBIntSpinner) : AnAction(name, "", icon) {

        override fun actionPerformed(p0: AnActionEvent) {
            val value = intSpinner.value as Int
            log.info("page index is $value")
            pageChange(value - book.pageIndex, false)
        }
    }


    inner class InputPageAction(val intSpinner: JBIntSpinner) : AnAction(""), CustomComponentAction {

        override fun actionPerformed(p0: AnActionEvent) {
        }

        override fun createCustomComponent(
            presentation: Presentation, place: String
        ): JComponent {
            val jPanel = JPanel(BorderLayout())
            val panel = JPanel()
            panel.add(intSpinner)
            jPanel.add(panel, BorderLayout.CENTER)
            return jPanel
        }
    }

    inner class ExitAction(name: String): AnAction(name, "", Actions.Exit) {
        override fun actionPerformed(p0: AnActionEvent) {
            val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
            context.readerToolWindowFactory.showBookshelvesUI()
        }
    }
}