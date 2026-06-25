package com.bms.ui;

import com.bms.entity.Book;
import com.bms.entity.User;
import com.bms.service.BookService;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 图书管理面板。
 */
public class BookManagePanel extends JPanel {

    private final BookService bookService;
    private final User user;

    private final BookTableModel tableModel = new BookTableModel();
    private final JTable bookTable = new JTable(tableModel);

    private final JComboBox<String> searchFieldCombo = new JComboBox<>(new String[]{"书名", "ISBN", "作者", "出版社"});
    private final JTextField searchInput = new JTextField(20);

    private final JTextField isbnField = new JTextField();
    private final JTextField titleField = new JTextField();
    private final JTextField publisherField = new JTextField();
    private final JTextField authorField = new JTextField();
    private final JTextField stockField = new JTextField();
    private final JTextField priceField = new JTextField();

    public BookManagePanel(BookService bookService, User user) {
        this.bookService = bookService;
        this.user = user;
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 搜索区域
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("查询字段:"));
        searchPanel.add(searchFieldCombo);
        searchPanel.add(new JLabel("关键字:"));
        searchPanel.add(searchInput);
        JButton searchButton = new JButton("查询");
        searchButton.addActionListener(e -> searchBooks());
        searchPanel.add(searchButton);
        JButton resetButton = new JButton("重置");
        resetButton.addActionListener(e -> {
            searchInput.setText("");
            refreshTable();
        });
        searchPanel.add(resetButton);

        add(searchPanel, BorderLayout.NORTH);

        // 表格区域
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.setDefaultRenderer(Object.class, new StockWarningRenderer());
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
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
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // 表单区域
        JPanel formPanel = new JPanel(new GridLayout(3, 4, 5, 5));
        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("书名:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("出版社:"));
        formPanel.add(publisherField);
        formPanel.add(new JLabel("作者:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("库存:"));
        formPanel.add(stockField);
        formPanel.add(new JLabel("价格:"));
        formPanel.add(priceField);

        ((PlainDocument) stockField.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
        ((PlainDocument) priceField.getDocument()).setDocumentFilter(new DecimalDocumentFilter());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        JButton addButton = new JButton("添加");
        addButton.addActionListener(e -> addBook());
        JButton updateButton = new JButton("修改");
        updateButton.addActionListener(e -> updateBook());
        JButton deleteButton = new JButton("删除");
        deleteButton.addActionListener(e -> deleteBook());
        JButton importButton = new JButton("导入");
        importButton.addActionListener(e -> importBooks());
        JButton exportButton = new JButton("导出");
        exportButton.addActionListener(e -> exportBooks());
        JButton clearButton = new JButton("清空");
        clearButton.addActionListener(e -> clearForm());

        boolean admin = user.isAdmin();
        addButton.setEnabled(admin);
        updateButton.setEnabled(admin);
        deleteButton.setEnabled(admin);
        importButton.setEnabled(admin);
        exportButton.setEnabled(admin);

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(clearButton);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.add(formPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        List<Book> books = bookService.findAllBooks();
        tableModel.setBooks(books);
        resizeColumnWidth();
    }

    private void resizeColumnWidth() {
        for (int column = 0; column < bookTable.getColumnCount(); column++) {
            int width = 50;
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
            JOptionPane.showMessageDialog(this, "该标题不存在！", "提示", JOptionPane.INFORMATION_MESSAGE);
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

    private void importBooks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel/CSV files", "xlsx", "csv"));
        int result = chooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        String name = file.getName().toLowerCase();
        try {
            List<Book> books;
            if (name.endsWith(".xlsx")) {
                books = readExcel(file);
            } else if (name.endsWith(".csv")) {
                books = readCsv(file);
            } else {
                JOptionPane.showMessageDialog(this, "不支持的文件格式", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int success = 0;
            int failed = 0;
            for (Book book : books) {
                try {
                    bookService.addBook(book);
                    success++;
                } catch (IllegalArgumentException e) {
                    failed++;
                }
            }
            refreshTable();
            JOptionPane.showMessageDialog(this,
                    "导入完成：成功 " + success + " 条，失败 " + failed + " 条",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导入失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private List<Book> readExcel(File file) throws IOException {
        List<Book> books = new java.util.ArrayList<>();
        try (InputStream is = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                Book book = parseBookRow(
                        getCellString(row.getCell(0)),
                        getCellString(row.getCell(1)),
                        getCellString(row.getCell(2)),
                        getCellString(row.getCell(3)),
                        getCellString(row.getCell(4)),
                        getCellString(row.getCell(5))
                );
                if (book != null) {
                    books.add(book);
                }
            }
        }
        return books;
    }

    private List<Book> readCsv(File file) throws Exception {
        List<Book> books = new java.util.ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(file))) {
            String[] line;
            boolean first = true;
            while ((line = reader.readNext()) != null) {
                if (first) {
                    first = false;
                    continue;
                }
                if (line.length < 6) continue;
                Book book = parseBookRow(line[0], line[1], line[2], line[3], line[4], line[5]);
                if (book != null) {
                    books.add(book);
                }
            }
        }
        return books;
    }

    private Book parseBookRow(String isbn, String title, String publisher, String author, String stockText, String priceText) {
        if (isbn == null || isbn.isBlank() || title == null || title.isBlank()) {
            return null;
        }
        try {
            int stock = Integer.parseInt(stockText.trim());
            BigDecimal price = new BigDecimal(priceText.trim());
            return new Book(isbn.trim(), title.trim(), publisher != null ? publisher.trim() : "",
                    author != null ? author.trim() : "", stock, price);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String getCellString(Cell cell) {
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            default -> "";
        };
    }

    private void exportBooks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
        int result = chooser.showSaveDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".xlsx")) {
            file = new File(file.getAbsolutePath() + ".xlsx");
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(file)) {
            Sheet sheet = workbook.createSheet("图书");
            Row header = sheet.createRow(0);
            String[] columns = {"ISBN", "书名", "出版社", "作者", "库存", "价格"};
            for (int i = 0; i < columns.length; i++) {
                header.createCell(i).setCellValue(columns[i]);
            }

            List<Book> books = tableModel.getBooks();
            int rowNum = 1;
            for (Book book : books) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(book.getIsbn());
                row.createCell(1).setCellValue(book.getTitle());
                row.createCell(2).setCellValue(book.getPublisher());
                row.createCell(3).setCellValue(book.getAuthor());
                row.createCell(4).setCellValue(book.getStock());
                row.createCell(5).setCellValue(book.getPrice().doubleValue());
            }

            workbook.write(fos);
            JOptionPane.showMessageDialog(this, "导出成功", "提示", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "导出失败: " + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * 整数输入过滤器。
     */
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

    /**
     * 小数输入过滤器。
     */
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
