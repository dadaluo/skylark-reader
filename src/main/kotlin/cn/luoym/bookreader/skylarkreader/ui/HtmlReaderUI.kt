package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.action.InputPageAction
import cn.luoym.bookreader.skylarkreader.action.ReaderUIExitAction
import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.extensions.Context
import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.General
import com.intellij.openapi.Disposable
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JCEFHtmlPanel
import java.awt.BorderLayout
import javax.swing.Icon

class HtmlReaderUI(val project: Project, val toolWindow: ToolWindow, book: AbstractBook) : ReaderUI, Disposable, PluginUI{

    private val htmlPanel: JCEFHtmlPanel

    private lateinit var jBIntSpinner: JBIntSpinner

    private val readerContent: Content

    private var activated: Boolean = false

    var book: AbstractBook = book
        set(value) {
            field = value
            field.spinner = jBIntSpinner
        }

    init {
        val context = Context.instance(project)
        htmlPanel = JCEFHtmlPanel(context.serverUrl)
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, createActionGroup(), true)
        toolbar.targetComponent = htmlPanel.component
        htmlPanel.component.add(toolbar.component, BorderLayout.NORTH)
        readerContent = ContentFactory.getInstance().createContent(htmlPanel.component, "", false)
        showReadContent()
    }

    override fun uiContent(): Content {
        return readerContent
    }

    override fun showReadContent() {
        activated = true
        val content = book.doRead()
        val context = Context.instance(project)
        htmlPanel.loadURL(context.serverUrl + "/$content")
    }

    override fun clearReadContent() {
        htmlPanel.setHtml("")
    }

    fun reload() {
        htmlPanel.cefBrowser.reloadIgnoreCache()
    }

    override fun updateFontStyle() {
        reload()
    }

    override fun isActive(): Boolean {
        return activated
    }

    override fun exit() {
        activated = false
        clearReadContent()
    }

    override fun nextPage() {
        pageChange(1)
    }

    override fun prevPage() {
        pageChange(-1)
    }

    private fun createActionGroup(): DefaultActionGroup {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(ReaderUIExitAction("Exit", this))
        actionGroup.add(PrePageAction("PrePage"))
        actionGroup.add(NextPageAction("NextPage"))
        actionGroup.add(ClearThisConsoleAction("Clear"))
        jBIntSpinner = JBIntSpinner(book.pageIndex, 1, book.maxPageIndex, 1)
        val icon: Icon = IconLoader.getIcon("icon/JumpTo.svg", this::class.java)
        actionGroup.add(JumpPageAction("JumpTo", icon, jBIntSpinner))
        actionGroup.add(InputPageAction(jBIntSpinner))
        return actionGroup
    }

    fun pageChange(indexChange: Int) {
        this.book.addPageIndex(indexChange)
        showReadContent()
    }


    inner class PrePageAction(name: String) : AnAction(name, "", General.ChevronLeft) {
        override fun actionPerformed(event: AnActionEvent) {
            pageChange(-1)
        }
    }

    inner class NextPageAction(name: String) : AnAction(name, "", General.ChevronRight) {
        override fun actionPerformed(event: AnActionEvent) {
            pageChange(1)
        }
    }

    inner class ClearThisConsoleAction(name: String) : AnAction(name, "", AllIcons.Actions.GC) {
        override fun actionPerformed(p0: AnActionEvent) {
            clearReadContent()
        }
    }

    inner class JumpPageAction(name: String, icon: Icon, val intSpinner: JBIntSpinner) : AnAction(name, "", icon) {
        override fun actionPerformed(p0: AnActionEvent) {
            val value = intSpinner.value as Int
            pageChange(value - book.pageIndex)
        }
    }

    override fun dispose() {
        exit()
        val instance = Context.instance(project)
        instance.htmlReaderUI = null
        if (instance.currentReaderUI == this) {
            instance.currentReaderUI = null
        }
        book.spinner = null
        htmlPanel.dispose()
    }
}