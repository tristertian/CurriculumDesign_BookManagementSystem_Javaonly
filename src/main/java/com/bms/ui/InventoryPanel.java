package com.bms.ui;

import com.bms.entity.Book;
import com.bms.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * 库存管理面板：添加、修改、删除、多条件搜索定位、书籍列表展示。
 */
public class InventoryPanel extends JPanel {

    private final BookService bookService;

    private final BookTableModel tableModel = new BookTableModel();
    private final JTable bookTable = new JTable(tableModel);

    private final JComboBox<String> searchFieldCombo = new JComboBox<>(new String[]{"书名", "ISBN", "作者", "出版社"});
    private final JTextField searchInput = new JTextField(18);

    private final JTextField isbnField = new JTextField();
    private final JTextField titleField = new JTextField();
    private final JTextField publisherField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField priceField = new JTextField();

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);

    public InventoryPanel(BookService bookService) {
        this.bookService = bookService;
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("库存管理");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));

        // 搜索区
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "搜索定位"));
        searchPanel.add(new JLabel("搜索字段:"));
        searchPanel.add(searchFieldCombo);
        searchPanel.add(new JLabel("关键字:"));
        searchPanel.add(searchInput);

        JButton searchButton = new JButton("查询");
        stylePrimaryButton(searchButton);
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);

        JButton resetButton = new JButton("重置");
        styleSecondaryButton(resetButton);
        resetButton.addActionListener(e -> {
            searchInput.setText("");
            refreshTable();
        });
        searchPanel.add(resetButton);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        // 表格区
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.setDefaultRenderer(Object.class, new StockWarningRenderer());
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        bookTable.setRowHeight(28);
        bookTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        bookTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        bookTable.getTableHeader().setBackground(PRIMARY_COLOR);
        bookTable.getTableHeader().setForeground(Color.WHITE);
        bookTable.getTableHeader().setPreferredSize(new Dimension(0, 32));
        bookTable.setGridColor(new Color(230, 230, 230));
        bookTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    fillFormFromSelection();
                }
            }
        });
        bookTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    fillFormFromSelection();
                }
            }
        });

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "书籍列表"));
        tablePanel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        add(tablePanel, BorderLayout.CENTER);

        // 表单与操作区
        JPanel southPanel = new JPanel(new BorderLayout(10, 10));
        southPanel.setBackground(Color.WHITE);
        southPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "图书信息"));

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 12, 12));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        styleFormLabel("ISBN", formPanel);
        formPanel.add(isbnField);
        styleFormLabel("书名", formPanel);
        formPanel.add(titleField);
        styleFormLabel("出版社", formPanel);
        formPanel.add(publisherField);
        styleFormLabel("作者", formPanel);
        formPanel.add(authorField);
        styleFormLabel("库存", formPanel);
        formPanel.add(stockField);
        styleFormLabel("价格", formPanel);
        formPanel.add(priceField);

        styleTextField(isbnField);
        styleTextField(titleField);
        styleTextField(publisherField);
        styleTextField(authorField);
        styleTextField(stockField);
        styleTextField(priceField);

        ((PlainDocument) stockField.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
        ((PlainDocument) priceField.getDocument()).setDocumentFilter(new DecimalDocumentFilter());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 5));
        buttonPanel.setBackground(Color.WHITE);
        JButton addButton = new JButton("添加");
        stylePrimaryButton(addButton);
        addButton.addActionListener(e -> addBook());

        JButton updateButton = new JButton("修改");
        stylePrimaryButton(updateButton);
        updateButton.addActionListener(e -> updateBook());

        JButton deleteButton = new JButton("删除");
        styleDangerButton(deleteButton);
        deleteButton.addActionListener(e -> deleteBook());

        JButton clearButton = new JButton("清空");
        styleSecondaryButton(clearButton);
        clearButton.addActionListener(e -> clearForm());

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);

        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void styleFormLabel(String text, JPanel panel) {
        JLabel label = new JLabel(text + ":");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        label.setForeground(new Color(60, 60, 60));
        panel.add(label);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(4, 6, 4, 6)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 16, 6, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(new Color(80, 80, 80));
        button.setBackground(new Color(220, 220, 220));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 16, 6, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleDangerButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 16, 6, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void refreshTable() {
        List<Book> books = bookService.findAllBooks();
        tableModel.setBooks(books);
        resizeColumnWidth();
    }

    private void searchBooks() {
        String field = (String) searchFieldCombo.getSelectedItem();
        String keyword = searchInput.getText().trim();
        List<Book> result;
        switch (field) {
            case "书名" -> result = bookService.searchBooks(keyword, null, null, null);
            case "ISBN" -> result = bookService.searchBooks(null, keyword, null, null);
            case "作者" -> result = bookService.searchBooks(null, null, keyword, null);
            case "出版社" -> result = bookService.searchBooks(null, null, null, keyword);
            default -> result = bookService.findAllBooks();
        }
        tableModel.setBooks(result);
        resizeColumnWidth();
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到匹配记录", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void fillFormFromSelection() {
        Book book = tableModel.getBookAt(bookTable.getSelectedRow());
        if (book == null) {
            return;
        }
        isbnField.setText(book.getIsbn());
        titleField.setText(book.getTitle());
        publisherField.setText(book.getPublisher());
        authorField.setText(book.getAuthor());
        stockField.setText(String.valueOf(book.getStock()));
        priceField.setText(book.getPrice().toString());
    }

    private Book readBookFromForm() {
        String isbn = isbnField.getText().trim();
        String title = titleField.getText().trim();
        String publisher = publisherField.getText().trim();
        String author = authorField.getText().trim();
        String stockText = stockField.getText().trim();
        String priceText = priceField.getText().trim();

        if (isbn.isEmpty() || title.isEmpty() || stockText.isEmpty() || priceText.isEmpty()) {
            throw new IllegalArgumentException("ISBN、书名、库存、价格为必填项");
        }

        int stock;
        BigDecimal price;
        try {
            stock = Integer.parseInt(stockText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("库存必须为正整数");
        }
        try {
            price = new BigDecimal(priceText);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("价格格式不正确");
        }

        return new Book(isbn, title, publisher, author, stock, price);
    }

    private void addBook() {
        try {
            Book book = readBookFromForm();
            bookService.addBook(book);
            JOptionPane.showMessageDialog(this, "添加成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBook() {
        try {
            Book book = readBookFromForm();
            bookService.updateBook(book);
            JOptionPane.showMessageDialog(this, "修改成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteBook() {
        String isbn = isbnField.getText().trim();
        if (isbn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请先选择或输入要删除的 ISBN", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "确定要删除 ISBN 为 " + isbn + " 的图书吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            bookService.deleteBook(isbn);
            JOptionPane.showMessageDialog(this, "删除成功", "提示", JOptionPane.INFORMATION_MESSAGE);
            refreshTable();
            clearForm();
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        isbnField.setText("");
        titleField.setText("");
        publisherField.setText("");
        authorField.setText("");
        stockField.setText("");
        priceField.setText("");
        bookTable.clearSelection();
    }

    private void resizeColumnWidth() {
        for (int column = 0; column < bookTable.getColumnCount(); column++) {
            int width = 80;
            for (int row = 0; row < bookTable.getRowCount(); row++) {
                Object value = bookTable.getValueAt(row, column);
                if (value == null) continue;
                int preferredWidth = bookTable.getCellRenderer(row, column)
                        .getTableCellRendererComponent(bookTable, value, false, false, row, column)
                        .getPreferredSize().width;
                width = Math.max(width, preferredWidth + 20);
            }
            bookTable.getColumnModel().getColumn(column).setPreferredWidth(width);
        }
    }

    private static class IntegerDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || string.matches("[0-9]*")) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            if (text == null || text.matches("[0-9]*")) {
                super.replace(fb, offset, length, text, attrs);
            }
        }
    }

    private static class DecimalDocumentFilter extends DocumentFilter {
        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
            if (string == null || isValidDecimal(fb.getDocument().getText(0, fb.getDocument().getLength()) + string)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
            String current = fb.getDocument().getText(0, fb.getDocument().getLength());
            String updated = current.substring(0, offset) + text + current.substring(offset + length);
            if (text == null || isValidDecimal(updated)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValidDecimal(String text) {
            return text.matches("[0-9]*\\.?[0-9]*");
        }
    }
}
