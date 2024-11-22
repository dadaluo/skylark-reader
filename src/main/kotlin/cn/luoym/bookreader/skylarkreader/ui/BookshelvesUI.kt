package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.book.AbstractBook
import cn.luoym.bookreader.skylarkreader.listener.MouseEventHandler
import cn.luoym.bookreader.skylarkreader.properties.Bookshelves
import cn.luoym.bookreader.skylarkreader.toolwindows.Context
import cn.luoym.bookreader.skylarkreader.utils.sendNotify
import com.intellij.icons.AllIcons
import com.intellij.notification.NotificationType
import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileChooser.FileChooser
import com.intellij.openapi.fileChooser.FileChooserDescriptor
import com.intellij.openapi.project.ProjectManager
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import com.jgoodies.common.collect.ArrayListModel
import java.awt.BorderLayout
import java.awt.event.MouseListener
import java.text.DecimalFormat
import java.util.function.Consumer
import javax.swing.JPanel
import javax.swing.ListModel

class BookshelvesUI : PluginUI {

    val bookshelves: JPanel = JPanel(BorderLayout())

    val listMode: ArrayListModel<AbstractBook> = ArrayListModel<AbstractBook>()

    val jbList: JBList<AbstractBook> = JBList<AbstractBook>()

    private val uiContent: Content


    constructor(){
        val toolbar = ActionManager.getInstance().createActionToolbar(ActionPlaces.TOOLBAR, createActionGroup(), true)
        toolbar.targetComponent = bookshelves
        this.bookshelves.add(toolbar.component, BorderLayout.NORTH)
        val scrollPane = JBScrollPane(jbList)
        bookshelves.add(scrollPane, BorderLayout.CENTER)
        jbList.installCellRenderer { BookProgress(it) }
        jbList.addMouseListener(doubleClickListener())
        uiContent = ContentFactory.getInstance().createContent(bookshelves, "", false)
        refreshShelves()
    }

    fun doubleClickListener(): MouseListener {
        val handler = MouseEventHandler()
        handler.mouseClicked = Consumer {
            if (it.clickCount >= 2) {
                val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
                val currentSelect = jbList.selectedValue
                context.currentBook= currentSelect
                context.readerToolWindowFactory.showReaderConsole(currentSelect)
            }
        }
        return handler
    }

    fun createActionGroup(): ActionGroup {
        val actionGroup = DefaultActionGroup()
        actionGroup.add(AddBookAction())
        actionGroup.add(RemoveBookAction())
        actionGroup.add(ReadBookAction())
        return actionGroup
    }

    fun refreshShelves() {
        val bookshelves = ApplicationManager.getApplication().getService<Bookshelves>(Bookshelves::class.java)
        listMode.clear()
        jbList.model = listMode as ListModel<AbstractBook>
        bookshelves.bookshelves.forEach {
            listMode.add(it.value)
        }
    }

    override fun uiContent(): Content {
        return uiContent
    }

    inner class AddBookAction : AnAction("Add Book", "", AllIcons.General.Add) {
        override fun actionPerformed(p0: AnActionEvent) {
            val descriptor = FileChooserDescriptor(true, false, false, false, false, true)
            val project = ProjectManager.getInstance().defaultProject
            val bookshelves = ApplicationManager.getApplication().getService<Bookshelves>(Bookshelves::class.java)
            FileChooser.chooseFiles(descriptor, project, project.workspaceFile) {
                it.forEach {
                    bookshelves.addBook(it.path)
                }
            }
            refreshShelves()
        }
    }


    inner class RemoveBookAction : AnAction("Remove Book", "", AllIcons.General.Remove) {
        override fun actionPerformed(p0: AnActionEvent) {
            val currentSelect = jbList.selectedValue
            if (currentSelect == null) {
                sendNotify("请选择要删除的选项", NotificationType.WARNING)
                return
            }
            val bookshelves = ApplicationManager.getApplication().getService<Bookshelves>(Bookshelves::class.java)
            bookshelves.removeBook(currentSelect)
            refreshShelves()
        }
    }

    inner class ReadBookAction : AnAction("Read Book", "", AllIcons.Toolwindows.ToolWindowRun) {
        override fun actionPerformed(p0: AnActionEvent) {
            val currentSelect = jbList.selectedValue
            if (currentSelect == null) {
                sendNotify("请选择要读的书", NotificationType.WARNING)
                return
            }
            val context = ApplicationManager.getApplication().getService<Context>(Context::class.java)
            context.currentBook= currentSelect
            context.readerToolWindowFactory.showReaderConsole(currentSelect)
        }
    }

    inner class BookProgress(val book: AbstractBook) : JPanel() {
        init {
            val fileLabel = JBLabel()
            val percentLabel = JBLabel()
            this.setLayout(BorderLayout())
            val percent = book.readingProgress()
            val labelText = "  " + book.path
            fileLabel.horizontalAlignment = JBLabel.LEFT
            fileLabel.text = labelText
            fileLabel.icon = AllIcons.FileTypes.Text

            percentLabel.text = percent
            percentLabel.horizontalAlignment = JBLabel.RIGHT
            this.add(fileLabel, BorderLayout.WEST)
            this.add(percentLabel, BorderLayout.EAST)
        }
    }


}