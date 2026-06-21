package com.bms.ui;

import com.bms.service.BookService;

import javax.swing.*;
import java.awt.*;

/**
 * 图书管理系统主窗口。
 */
public class MainFrame extends JFrame {

    private final BookService bookService;

    public MainFrame(BookService bookService) {
        this.bookService = bookService;
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
        tabbedPane.addTab("统计分析", new StatsPanel(bookService));

        add(tabbedPane, BorderLayout.CENTER);
    }
}
