package com.bms.ui;

import com.bms.entity.Book;
import com.bms.service.BookService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.opencsv.CSVReader;

import java.awt.*;
import java.io.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 数据概览面板：查询、统计排序、导入导出、书籍列表展示。
 */
public class DataOverviewPanel extends JPanel {

    private final BookService bookService;

    private final BookTableModel tableModel = new BookTableModel();
    private final JTable bookTable = new JTable(tableModel);

    private final JComboBox<String> searchFieldCombo = new JComboBox<>(new String[]{"书名", "ISBN", "作者", "出版社"});
    private final JTextField searchInput = new JTextField(18);

    private final JComboBox<String> sortFieldCombo = new JComboBox<>(new String[]{"价格", "库存量", "作者", "出版社"});
    private final JComboBox<String> sortOrderCombo = new JComboBox<>(new String[]{"降序", "升序"});

    private final JLabel totalLabel = new JLabel("图书总数: 0");
    private final JLabel shownLabel = new JLabel("当前展示: 0");

    public DataOverviewPanel(BookService bookService) {
        this.bookService = bookService;
        initUI();
        refreshTable();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // 查询区
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBorder(BorderFactory.createTitledBorder("查询"));
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

        // 统计排序区
        JPanel sortPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        sortPanel.setBorder(BorderFactory.createTitledBorder("统计排序"));
        sortPanel.add(new JLabel("排序字段:"));
        sortPanel.add(sortFieldCombo);
        sortPanel.add(new JLabel("排序方式:"));
        sortPanel.add(sortOrderCombo);
        JButton sortButton = new JButton("统计");
        sortButton.addActionListener(e -> sortBooks());
        sortPanel.add(sortButton);

        // 信息区
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        infoPanel.add(totalLabel);
        infoPanel.add(shownLabel);

        // 顶部组合
        JPanel topPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        topPanel.add(searchPanel);
        topPanel.add(sortPanel);
        topPanel.add(infoPanel);

        add(topPanel, BorderLayout.NORTH);

        // 表格区
        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setAutoCreateRowSorter(true);
        bookTable.setDefaultRenderer(Object.class, new StockWarningRenderer());
        bookTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // 操作区
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton importButton = new JButton("导入 Excel/CSV");
        importButton.addActionListener(e -> importBooks());
        JButton exportButton = new JButton("导出 Excel");
        exportButton.addActionListener(e -> exportBooks());
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void refreshTable() {
        List<Book> books = bookService.findAllBooks();
        tableModel.setBooks(books);
        totalLabel.setText("图书总数: " + bookService.getBookCount());
        shownLabel.setText("当前展示: " + books.size());
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
        shownLabel.setText("当前展示: " + result.size());
        resizeColumnWidth();
        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "未找到匹配记录", "提示", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void sortBooks() {
        String field = (String) sortFieldCombo.getSelectedItem();
        String order = (String) sortOrderCombo.getSelectedItem();

        String sortField = switch (field) {
            case "价格" -> "price";
            case "库存量" -> "stock";
            case "作者" -> "author";
            case "出版社" -> "publisher";
            default -> "price";
        };
        boolean ascending = "升序".equals(order);

        List<Book> books = bookService.listBooksSorted(sortField, ascending);
        tableModel.setBooks(books);
        shownLabel.setText("当前展示: " + books.size());
        resizeColumnWidth();
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

    private void importBooks() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel/CSV files", "xlsx", "csv"));
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
        chooser.setFileFilter(new FileNameExtensionFilter("Excel (*.xlsx)", "xlsx"));
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
}
