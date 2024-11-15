package cn.luoym.bookreader.skylarkreader.ui;

import cn.luoym.bookreader.skylarkreader.properties.SettingProperties;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

public class SettingUI {
    private JPanel setting;
    private JComboBox<String> fontSelect;
    private JLabel fontFamilyLabel;
    private JLabel fontSizeLabel;
    private JSpinner fontSizeSpinner;
    private JLabel pageSizeLabel;
    private JSpinner pageSizeSpinner;
    private JCheckBox autoTurnPageBox;


    private String fontFamily;

    private int fontSize;

    private int pageSize;

    private boolean autoTurnPage;

    public SettingUI() {

        SettingProperties properties = ApplicationManager.getApplication().getService(SettingProperties.class);
        fontFamily = properties.getFontFamily();
        fontSize = properties.getFontSize();
        pageSize = properties.getPageSize();
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilyNames = ge.getAvailableFontFamilyNames();
        Arrays.stream(fontFamilyNames).forEach(fontFamilyName -> {
            fontSelect.addItem(fontFamilyName);
        });
        fontSelect.setSelectedItem(properties.getFontFamily());
        fontSelect.addItemListener( e ->{
            if (e.getStateChange() == ItemEvent.SELECTED && fontSelect.getSelectedItem() != null){
                fontFamily = fontSelect.getSelectedItem().toString();
            }
        });
        SpinnerNumberModel  nModel = new SpinnerNumberModel(properties.getFontSize(), 8,   50,  1);
        fontSizeSpinner.setModel(nModel);
        fontSizeSpinner.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            fontSize = (Integer)source.getValue();
        });
        SpinnerNumberModel  pageSizeModel = new SpinnerNumberModel(properties.getPageSize(), 1000,   30000,  500);
        pageSizeSpinner.setModel(pageSizeModel);
        pageSizeSpinner.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            pageSize = (Integer)source.getValue();
        });
        autoTurnPageBox.setSelected(properties.getAutoTurnPage());
        autoTurnPageBox.addChangeListener(e -> autoTurnPage = autoTurnPageBox.isSelected());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public JPanel getSetting() {
        return setting;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public int getFontSize() {
        return fontSize;
    }

    public int getPageSize() {
        return pageSize;
    }

    public boolean isAutoTurnPage() {
        return autoTurnPage;
    }

}
