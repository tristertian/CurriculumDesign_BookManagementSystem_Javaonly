package com.bms.ui;

import com.bms.entity.User;
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
    private final User user;

    public MainFrame(BookService bookService, SaleService saleService, User user) {
        this.bookService = bookService;
        this.saleService = saleService;
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("图书管理系统 - " + user.getUsername() + "(" + (user.isAdmin() ? "管理员" : "店员") + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        if (user.isAdmin()) {
            tabbedPane.addTab("图书管理", new BookManagePanel(bookService, user));
        }
        tabbedPane.addTab("图书销售", new SalesPanel(bookService));
        tabbedPane.addTab("销售记录", new SalesHistoryPanel(saleService));
        if (user.isAdmin()) {
            tabbedPane.addTab("统计分析", new StatsPanel(bookService));
        }

        add(tabbedPane, BorderLayout.CENTER);
    }
}
