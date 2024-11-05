package cn.luoym.bookreader.skylarkreader.ui;

import cn.luoym.bookreader.skylarkreader.book.BookProperties;
import com.intellij.openapi.application.ApplicationManager;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

public class SettingUI {
    private JPanel setting;
    private JComboBox<String> fontSelect;
    private JLabel fontFamily;
    private JLabel fontSizeLabel;
    private JSpinner fontSize;

    public SettingUI() {

        BookProperties properties = ApplicationManager.getApplication().getService(BookProperties.class);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilyNames = ge.getAvailableFontFamilyNames();
        Arrays.stream(fontFamilyNames).forEach(fontFamilyName -> {
            fontSelect.addItem(fontFamilyName);
        });
        fontSelect.setSelectedItem(properties.getFontFamily());
        fontSelect.addItemListener( e ->{
            if (e.getStateChange() == ItemEvent.SELECTED && fontSelect.getSelectedItem() != null){
                properties.setFontFamily((String) fontSelect.getSelectedItem());
            }
        });
        SpinnerNumberModel  nModel = new SpinnerNumberModel(13, 8,   50,  1);

        fontSize.addChangeListener(e -> {
            SpinnerModel source = (SpinnerModel)e.getSource();
            Integer value = (Integer)source.getValue();
            properties.setFontSize(value);
        });
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
