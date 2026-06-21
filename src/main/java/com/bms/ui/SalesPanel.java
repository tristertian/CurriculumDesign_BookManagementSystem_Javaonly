package com.bms.ui;

import com.bms.entity.Book;
import com.bms.service.BookService;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * 图书销售面板。
 */
public class SalesPanel extends JPanel {

    private final BookService bookService;

    private final JTextField isbnField = new JTextField(20);
    private final JTextField quantityField = new JTextField(10);

    private final JLabel titleLabel = new JLabel("-");
    private final JLabel priceLabel = new JLabel("-");
    private final JLabel stockLabel = new JLabel("-");
    private final JLabel amountLabel = new JLabel("-");

    public SalesPanel(BookService bookService) {
        this.bookService = bookService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("ISBN:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(isbnField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("购买数量:"), gbc);
        gbc.gridx = 1;
        inputPanel.add(quantityField, gbc);

        JButton queryButton = new JButton("查询");
        queryButton.addActionListener(e -> queryBook());
        gbc.gridx = 0;
        gbc.gridy = 2;
        inputPanel.add(queryButton, gbc);

        JButton sellButton = new JButton("确认销售");
        sellButton.addActionListener(e -> sellBook());
        gbc.gridx = 1;
        inputPanel.add(sellButton, gbc);

        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("图书信息"));
        infoPanel.add(new JLabel("书名:"));
        infoPanel.add(titleLabel);
        infoPanel.add(new JLabel("单价:"));
        infoPanel.add(priceLabel);
        infoPanel.add(new JLabel("库存:"));
        infoPanel.add(stockLabel);
        infoPanel.add(new JLabel("应收金额:"));
        infoPanel.add(amountLabel);

        add(inputPanel, BorderLayout.NORTH);
        add(infoPanel, BorderLayout.CENTER);
    }

    private void queryBook() {
        String isbn = isbnField.getText().trim();
        Optional<Book> bookOpt = bookService.findByIsbn(isbn);
        if (bookOpt.isEmpty()) {
            JOptionPane.showMessageDialog(this, "该编号不存在", "提示", JOptionPane.WARNING_MESSAGE);
            clearInfo();
            return;
        }
        Book book = bookOpt.get();
        titleLabel.setText(book.getTitle());
        priceLabel.setText(book.getPrice().toString());
        stockLabel.setText(String.valueOf(book.getStock()));
        calculateAmount(book);
    }

    private void calculateAmount(Book book) {
        String quantityText = quantityField.getText().trim();
        if (quantityText.isEmpty()) {
            amountLabel.setText("-");
            return;
        }
        try {
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                amountLabel.setText("数量需大于 0");
                return;
            }
            BigDecimal amount = book.calculateAmount(quantity);
            amountLabel.setText(amount.toString());
        } catch (NumberFormatException e) {
            amountLabel.setText("数量格式错误");
        }
    }

    private void sellBook() {
        String isbn = isbnField.getText().trim();
        String quantityText = quantityField.getText().trim();

        try {
            if (quantityText.isEmpty()) {
                throw new IllegalArgumentException("请输入购买数量");
            }
            int quantity = Integer.parseInt(quantityText);
            BigDecimal amount = bookService.sellBook(isbn, quantity);
            JOptionPane.showMessageDialog(this,
                    "销售成功，应收金额: " + amount + " 元",
                    "提示",
                    JOptionPane.INFORMATION_MESSAGE);
            clearInfo();
            isbnField.setText("");
            quantityField.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "购买数量必须为整数", "错误", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException | IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInfo() {
        titleLabel.setText("-");
        priceLabel.setText("-");
        stockLabel.setText("-");
        amountLabel.setText("-");
    }
}
