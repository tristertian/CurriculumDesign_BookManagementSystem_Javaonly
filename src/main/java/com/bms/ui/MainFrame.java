package com.bms.ui;

import com.bms.service.BookService;
import com.bms.service.SaleService;

import javax.swing.*;
import java.awt.*;

/**
 * 图书管理系统主窗口。
 */
public class MainFrame extends JFrame {

    private final BookService bookService;
    private final SaleService saleService;

    public MainFrame(BookService bookService, SaleService saleService) {
        this.bookService = bookService;
        this.saleService = saleService;
        initUI();
    }

    private void initUI() {
        setTitle("图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("图书管理", new BookManagePanel(bookService));
        tabbedPane.addTab("图书销售", new SalesPanel(bookService));
        tabbedPane.addTab("销售记录", new SalesHistoryPanel(saleService));
        tabbedPane.addTab("统计分析", new StatsPanel(bookService));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
