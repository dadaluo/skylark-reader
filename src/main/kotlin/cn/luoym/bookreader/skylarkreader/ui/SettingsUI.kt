package cn.luoym.bookreader.skylarkreader.ui

import cn.luoym.bookreader.skylarkreader.properties.SettingProperties
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.JBIntSpinner
import com.intellij.ui.components.JBLabel
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.uiDesigner.core.Spacer
import com.intellij.util.ui.JBUI
import java.awt.GraphicsEnvironment
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class SettingsUI {
    val settings: JPanel = JPanel(GridLayoutManager(4, 6, JBUI.emptyInsets(), -1, -1, false, false))

    lateinit var fontFamilyLabel: JBLabel

    lateinit var fontSelect: ComboBox<String>

    lateinit var fontSizeLabel: JBLabel

    lateinit var fontSizeSpinner: JBIntSpinner

    lateinit var pageSizeLabel: JBLabel

    lateinit var pageSizeSpinner: JBIntSpinner

    lateinit var autoTurnPageBox: JCheckBox

    lateinit var fontFamily: String

    var fontSize: Int = 13

    var pageSize: Int = 3000

    var autoTurnPage = false

    init {
        settingProperties()
        createUIComponents()
        addListener()
    }


    fun createUIComponents() {
        fontFamilyLabel = JBLabel("字体选择")
        settings.add(fontFamilyLabel, GridConstraints().apply {
            row = 0
            column = 0
            rowSpan = 1
            colSpan = 1
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })


        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        val fontFamilyNames = ge.availableFontFamilyNames
        fontSelect = ComboBox<String>(fontFamilyNames, -1)
        fontSelect.selectedItem = fontFamily
        settings.add(fontSelect, GridConstraints().apply {
            row = 0
            column = 1
            rowSpan = 1
            colSpan = 1
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        fontSizeLabel = JBLabel("默认字体大小")
        settings.add(fontSizeLabel, GridConstraints().apply {
            row = 0
            column = 2
            rowSpan = 1
            colSpan = 1
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        fontSizeSpinner = JBIntSpinner(fontSize, 6, 50)
        settings.add(fontSizeSpinner, GridConstraints().apply {
            row = 0
            column = 3
            rowSpan = 1
            colSpan = 1
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 2
            vSizePolicy = 0
            fill = 1
        })

        pageSizeLabel = JBLabel("分页大小")
        settings.add(pageSizeLabel, GridConstraints().apply {
            row = 1
            column = 0
            rowSpan = 1
            colSpan = 1
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        pageSizeSpinner = JBIntSpinner(pageSize, 1000, 50000, 500)
        settings.add(pageSizeSpinner, GridConstraints().apply {
            row = 1
            column = 1
            rowSpan = 1
            colSpan = 1
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 2
            vSizePolicy = 0
        })

        autoTurnPageBox = JCheckBox("阅读到页底时是否自动翻页")
        settings.add(autoTurnPageBox, GridConstraints().apply {
            row = 2
            column = 0
            rowSpan = 1
            colSpan = 4
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        settings.add(Spacer(), GridConstraints().apply {
            row = 3
            column = 2
            rowSpan = 1
            colSpan = 2
            anchor = GridConstraints.ANCHOR_CENTER
            hSizePolicy = 1
            vSizePolicy = 6
        })
    }

    fun addListener() {
        fontSelect.addItemListener(ItemListener { e: ItemEvent? ->
            if (e!!.getStateChange() == ItemEvent.SELECTED && fontSelect.selectedItem != null) {
                fontFamily = fontSelect.selectedItem.toString()
            }
        })

        fontSizeSpinner.addChangeListener(ChangeListener { e: ChangeEvent? ->
            val source = e!!.getSource() as JSpinner
            fontSize = (source.value as Int)
        })

        pageSizeSpinner.addChangeListener(ChangeListener { e: ChangeEvent? ->
            val source = e!!.getSource() as JSpinner
            pageSize = (source.value as Int)
        })

        autoTurnPageBox.addChangeListener(ChangeListener { e: ChangeEvent? ->
            autoTurnPage = autoTurnPageBox.isSelected
        })
    }

    fun settingProperties() {
        val properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        pageSize = properties.pageSize
        fontSize = properties.fontSize
        autoTurnPage = properties.autoTurnPage
        fontFamily = properties.fontFamily
    }


}