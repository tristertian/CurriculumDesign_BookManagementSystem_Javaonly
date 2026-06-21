package com.bms.ui;

import com.bms.entity.Book;
import com.bms.service.BookService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * 统计分析面板。
 */
public class StatsPanel extends JPanel {

    private final BookService bookService;

    private final BookTableModel tableModel = new BookTableModel();
    private final JTable statsTable = new JTable(tableModel);

    private final JLabel countLabel = new JLabel("图书总数: 0");
    private final JComboBox<String> sortFieldCombo = new JComboBox<>(new String[]{"价格", "库存量", "作者", "出版社"});
    private final JComboBox<String> sortOrderCombo = new JComboBox<>(new String[]{"降序", "升序"});

    public StatsPanel(BookService bookService) {
        this.bookService = bookService;
        initUI();
        refreshStats();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.add(countLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(new JLabel("排序字段:"));
        controlPanel.add(sortFieldCombo);
        controlPanel.add(new JLabel("排序方式:"));
        controlPanel.add(sortOrderCombo);
        JButton sortButton = new JButton("统计");
        sortButton.addActionListener(e -> refreshStats());
        controlPanel.add(sortButton);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(statsTable), BorderLayout.CENTER);
    }

    private void refreshStats() {
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
        countLabel.setText("图书总数: " + bookService.getBookCount());
    }
}
