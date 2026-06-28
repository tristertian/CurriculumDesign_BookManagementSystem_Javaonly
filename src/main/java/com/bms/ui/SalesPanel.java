package com.bms.ui;

import com.bms.entity.Book;
import com.bms.service.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color INFO_BG = new Color(235, 245, 251);

    public SalesPanel(BookService bookService) {
        this.bookService = bookService;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabelMain = new JLabel("图书销售");
        titleLabelMain.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabelMain.setForeground(new Color(50, 50, 50));

        // 输入区
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "销售信息"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(createLabel("ISBN"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(isbnField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(createLabel("购买数量"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(quantityField, gbc);

        styleTextField(isbnField);
        styleTextField(quantityField);
        isbnField.addActionListener(e -> queryBook());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 5));
        buttonPanel.setBackground(Color.WHITE);
        JButton queryButton = new JButton("查询");
        stylePrimaryButton(queryButton);
        queryButton.addActionListener(e -> queryBook());

        JButton sellButton = new JButton("确认销售");
        styleSuccessButton(sellButton);
        sellButton.addActionListener(e -> sellBook());

        buttonPanel.add(queryButton);
        buttonPanel.add(sellButton);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        inputPanel.add(buttonPanel, gbc);

        // 图书信息区
        JPanel infoPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        infoPanel.setBackground(INFO_BG);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 240)),
                new EmptyBorder(20, 25, 20, 25)
        ));
        infoPanel.add(createInfoLabel("书名:"));
        infoPanel.add(styleValueLabel(titleLabel));
        infoPanel.add(createInfoLabel("单价:"));
        infoPanel.add(styleValueLabel(priceLabel));
        infoPanel.add(createInfoLabel("库存:"));
        infoPanel.add(styleValueLabel(stockLabel));
        infoPanel.add(createInfoLabel("应收金额:"));
        infoPanel.add(styleValueLabel(amountLabel));

        JPanel infoWrapper = new JPanel(new BorderLayout());
        infoWrapper.setBackground(Color.WHITE);
        JLabel infoTitle = new JLabel("图书信息");
        infoTitle.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        infoTitle.setForeground(new Color(60, 60, 60));
        infoTitle.setBorder(new EmptyBorder(0, 0, 10, 0));
        infoWrapper.add(infoTitle, BorderLayout.NORTH);
        infoWrapper.add(infoPanel, BorderLayout.CENTER);

        // 组合
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(inputPanel, BorderLayout.NORTH);
        centerPanel.add(infoWrapper, BorderLayout.CENTER);

        add(titleLabelMain, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text + ":");
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private JLabel styleValueLabel(JLabel label) {
        label.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        label.setForeground(PRIMARY_COLOR);
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(5, 8, 5, 8)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 18, 6, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSuccessButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(39, 174, 96));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 18, 6, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        priceLabel.setText("¥" + book.getPrice());
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
            amountLabel.setText("¥" + amount);
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
                    "销售成功，应收金额: ¥" + amount,
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
