package cn.luoym.bookreader.skylarkreader.ui;

import cn.luoym.bookreader.skylarkreader.book.AbstractBook;
import cn.luoym.bookreader.skylarkreader.book.BookProperties;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class ReaderUI {
    @Nullable
    private JPanel reader;
    @Nullable
    private JPanel controlPanel;
    @Nullable
    private JButton btnFirst;
    @Nullable
    private JButton btnPre;
    @Nullable
    private JButton btnNext;
    @Nullable
    private JButton btnLast;
    @Nullable
    private JButton biggerFontSize;
    @Nullable
    private JButton smallerFontSize;

    private JScrollPane scrollPane;

    private JTextPane textPane;

    @NotNull
    private AbstractBook book;

    @Nullable
    public final JPanel getReader() {
        return this.reader;
    }

    private final void createUIComponents() {
    }

    public ReaderUI(@NotNull Project project, @NotNull ToolWindow toolWindow, @NotNull AbstractBook book) {
        this.book = book;
        BookProperties properties = ApplicationManager.getApplication().getService(BookProperties.class);
        btnFirst.addActionListener(e -> {
            book.setIndex(1);
            pageChage(false);
        });
        btnPre.addActionListener(e -> {
            if (book.getIndex() == 1) {
                book.setIndex(1);
            } else {
                book.setIndex(book.getIndex() - 1);
            }
            pageChage(false);
        });

        btnNext.addActionListener(e -> {
            book.setIndex(book.getIndex() + 1);
            pageChage(true);
        });
        btnLast.addActionListener(e -> {
            book.setIndex(book.getMaxIndex());
            pageChage(false);
        });

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> {
            JViewport viewport = scrollPane.getViewport();
            if (viewport.getView().getHeight() <= viewport.getHeight() + viewport.getViewPosition().y) {
                book.setIndex(book.getIndex() + 1);
                pageChage(true);
            }
        });
        biggerFontSize.addActionListener(e -> {
            int fontSize = book.getFontSize();
            fontSize += 1;
            book.setFontSize(fontSize);
            fontChange();
        });
        smallerFontSize.addActionListener(e -> {
            int fontSize = book.getFontSize();
            fontSize -= 1;
            book.setFontSize(fontSize);
            fontChange();
        });
    }

    public void pageChage(Boolean append) {
        String read = this.book.doRead();
        Document document = textPane.getStyledDocument();
        try {
            if (!append) {
                document.remove(0, document.getLength());
            }
            AttributeSet attributeSet = textPane.getCharacterAttributes();
            document.insertString(document.getLength(), read, attributeSet);
            if (!append){
                fontChange();
            }
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        textPane.setDocument(document);
    }

    public void fontChange() {
        BookProperties properties = ApplicationManager.getApplication().getService(BookProperties.class);
        SimpleAttributeSet arrSet = new SimpleAttributeSet();
        MutableAttributeSet attributes = textPane.getInputAttributes();
        AttributeSet attributeSet = textPane.getCharacterAttributes();
        StyledDocument document = textPane.getStyledDocument();
        StyleConstants.setFontFamily(arrSet, properties.getFontFamily());
        StyleConstants.setFontSize(arrSet, book.getFontSize());
        document.setCharacterAttributes(0, document.getLength(), arrSet, false);
        //设置将输入的属性
        textPane.setCharacterAttributes(arrSet, false);

    }
}
