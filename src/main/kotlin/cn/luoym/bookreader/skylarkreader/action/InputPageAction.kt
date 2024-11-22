package cn.luoym.bookreader.skylarkreader.action

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.actionSystem.ex.CustomComponentAction
import com.intellij.ui.JBIntSpinner
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JPanel

class InputPageAction(val intSpinner: JBIntSpinner) : AnAction(""), CustomComponentAction {

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