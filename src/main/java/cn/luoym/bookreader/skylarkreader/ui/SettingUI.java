package cn.luoym.bookreader.skylarkreader.ui;

import cn.luoym.bookreader.skylarkreader.book.BookProperties;
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

    private String fontFamily;

    private int fontSize;

    private int pageSize;

    public SettingUI() {

        BookProperties properties = ApplicationManager.getApplication().getService(BookProperties.class);
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
        SpinnerNumberModel  nModel = new SpinnerNumberModel(13, 8,   50,  1);
        fontSizeSpinner.setModel(nModel);
        fontSizeSpinner.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            fontSize = (Integer)source.getValue();
        });
        SpinnerNumberModel  pageSizeModel = new SpinnerNumberModel(3000, 1000,   30000,  500);
        pageSizeSpinner.setModel(pageSizeModel);
        pageSizeSpinner.addChangeListener(e -> {
            JSpinner source = (JSpinner) e.getSource();
            pageSize = (Integer)source.getValue();
        });
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
}
