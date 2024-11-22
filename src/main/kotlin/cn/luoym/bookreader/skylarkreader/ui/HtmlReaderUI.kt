package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.action.InputPageAction
import cn.luoym.bookreader.skylarkreader.action.ReaderUIExitAction
import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import com.intellij.icons.AllIcons
import com.intellij.icons.AllIcons.General
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.jcef.JCEFHtmlPanel
import java.awt.BorderLayout
import javax.swing.Icon

class HtmlReaderUI(val project: Project, val toolWindow: ToolWindow, var book: AbstractBook) : ReaderUI {

    val htmlPanel: JCEFHtmlPanel

    private lateinit var jBIntSpinner: JBIntSpinner

    private val readerContent: Content

    private val scrollBarStyle = """
        <style>
        html,
        body {
          &::-webkit-scrollbar {
            width: 6px;
          }
          &::-webkit-scrollbar-thumb {
            background-color: rgba(144, 147, 153, 0.5);
            border-radius: 6px;
          }
          &::-webkit-scrollbar-track {
            background-color: transparent;
          }
        }
        </style>
    """.trimIndent()

    init {
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        htmlPanel = JCEFHtmlPanel(context.serverUrl).apply{
            setOpenLinksInExternalBrowser(true)
        }
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
        val content = book.doRead()
        val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
        htmlPanel.loadURL(context.serverUrl + "/$content")
    }

    override fun clearReadContent() {
        htmlPanel.setHtml("")
    }

    fun createActionGroup(): DefaultActionGroup {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(ReaderUIExitAction("Exit"))
        actionGroup.add(PrePageAction("PrePage"))
        actionGroup.add(NextPageAction("NextPage"))
        actionGroup.add(ClearThisConsoleAction("Clear"))
        actionGroup.add(LargeFontSizeAction("LargerFont"))
        actionGroup.add(SmallerFontSizeAction("SmallerFont"))
        jBIntSpinner = JBIntSpinner(book.pageIndex, 1, book.maxPageIndex, 1)
        val icon: Icon = IconLoader.getIcon("icon/JumpTo.svg", this::class.java)
        actionGroup.add(JumpPageAction("JumpTo", icon, jBIntSpinner))
        actionGroup.add(InputPageAction(jBIntSpinner))
        return actionGroup
    }


    inner class PrePageAction(name: String) : AnAction(name, "", General.ChevronLeft) {
        override fun actionPerformed(event: AnActionEvent) {
            book.addPageIndex(-1)
            showReadContent()
        }
    }

    inner class NextPageAction(name: String) : AnAction(name, "", General.ChevronRight) {
        override fun actionPerformed(event: AnActionEvent) {
            book.addPageIndex(1)
            showReadContent()
        }
    }

    inner class ClearThisConsoleAction(name: String) : AnAction(name, "", AllIcons.Actions.GC) {
        override fun actionPerformed(p0: AnActionEvent) {
            clearReadContent()
        }
    }

    inner class LargeFontSizeAction(name: String) : AnAction(name, "", General.ZoomIn) {

        override fun actionPerformed(p0: AnActionEvent) {

        }
    }

    inner class SmallerFontSizeAction(name: String) : AnAction(name, "", General.ZoomOut) {
        override fun actionPerformed(p0: AnActionEvent) {

        }
    }

    inner class JumpPageAction(name: String, icon: Icon, val intSpinner: JBIntSpinner) : AnAction(name, "", icon) {
        override fun actionPerformed(p0: AnActionEvent) {
            val value = intSpinner.value as Int
            book.addPageIndex(value - book.pageIndex)
            showReadContent()
        }
    }
}