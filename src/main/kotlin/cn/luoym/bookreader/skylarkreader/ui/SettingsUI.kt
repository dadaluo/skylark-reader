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
import java.awt.Dimension
import java.awt.GraphicsEnvironment
import java.awt.event.ItemEvent
import java.awt.event.ItemListener
import javax.swing.JCheckBox
import javax.swing.JPanel
import javax.swing.JSpinner
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener

class SettingsUI {
    val settings: JPanel = JPanel(GridLayoutManager(6, 6, JBUI.emptyInsets(), -1, -1, false, false))

    lateinit var fontFamilyLabel: JBLabel

    lateinit var fontFamily: String


    lateinit var fontSelect: ComboBox<String>

    lateinit var fontSizeLabel: JBLabel

    lateinit var fontSizeSpinner: JBIntSpinner

    lateinit var pageSizeLabel: JBLabel

    lateinit var pageSizeSpinner: JBIntSpinner

    lateinit var autoTurnPageBox: JCheckBox

    lateinit var overrideEpubFontFamilyBox: JCheckBox

    lateinit var overrideEpubFontSizeBox: JCheckBox


    var fontSize: Int = 13

    var pageSize: Int = 3000

    var autoTurnPage = false

    var overrideEpubFontFamily = false

    var overrideEpubFontSize = false

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
        settings.add(
            fontSelect, GridConstraints(
                0,
                1,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                0,
                0,
                Dimension(200, -1),
                Dimension(200, -1),
                Dimension(200, -1)
            )
        )

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
        settings.add(
            fontSizeSpinner, GridConstraints(
                0,
                3,
                1,
                1,
                GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_NONE,
                0,
                0,
                Dimension(200, -1),
                Dimension(200, -1),
                Dimension(200, -1)
            )
        )

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
        settings.add(pageSizeSpinner, GridConstraints(1,
            1,
            1,
            1,
            GridConstraints.ANCHOR_WEST,
            GridConstraints.FILL_NONE,
            0,
            0,
            Dimension(200, -1),
            Dimension(200, -1),
            Dimension(200, -1)))

        autoTurnPageBox = JCheckBox("阅读到页底时是否自动翻页")
        autoTurnPageBox.isSelected = autoTurnPage
        settings.add(autoTurnPageBox, GridConstraints().apply {
            row = 2
            column = 0
            rowSpan = 1
            colSpan = 4
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        overrideEpubFontFamilyBox = JCheckBox("覆盖epub电子书内的字体样式")
        overrideEpubFontFamilyBox.isSelected = overrideEpubFontFamily
        settings.add(overrideEpubFontFamilyBox, GridConstraints().apply {
            row = 3
            column = 0
            rowSpan = 1
            colSpan = 4
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        overrideEpubFontSizeBox = JCheckBox("覆盖epub电子书内的字体大小（可能会导致电子书页面排版混乱！）")
        overrideEpubFontSizeBox.isSelected = overrideEpubFontSize
        settings.add(overrideEpubFontSizeBox, GridConstraints().apply {
            row = 4
            column = 0
            rowSpan = 1
            colSpan = 4
            anchor = GridConstraints.ANCHOR_WEST
            hSizePolicy = 0
            vSizePolicy = 0
        })

        settings.add(Spacer(), GridConstraints().apply {
            row = 5
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

        overrideEpubFontFamilyBox.addChangeListener(ChangeListener { e: ChangeEvent? ->
            overrideEpubFontFamily = overrideEpubFontFamilyBox.isSelected
        })

        overrideEpubFontSizeBox.addChangeListener(ChangeListener { e: ChangeEvent? ->
            overrideEpubFontSize = overrideEpubFontSizeBox.isSelected
        })
    }

    fun settingProperties() {
        val properties =
            ApplicationManager.getApplication().getService<SettingProperties>(SettingProperties::class.java)
        pageSize = properties.pageSize
        fontSize = properties.fontSize
        autoTurnPage = properties.autoTurnPage
        fontFamily = properties.fontFamily
        overrideEpubFontFamily = properties.overrideEpubFontFamily
        overrideEpubFontSize = properties.overrideEpubFontSize
    }
}