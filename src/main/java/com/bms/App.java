package com.bms;

import com.bms.repository.BookRepository;
import com.bms.repository.JdbcBookRepository;
import com.bms.service.BookService;
import com.bms.ui.MainFrame;
import com.bms.util.DatabaseUtil;

import javax.swing.*;

/**
 * 图书管理系统入口。
 */
public class App {

    public static void main(String[] args) {
        try {
            DatabaseUtil.initializeDatabase();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                    "数据库初始化失败: " + e.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        BookRepository repository = new JdbcBookRepository();
        BookService service = new BookService(repository);

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame(service);
            frame.setVisible(true);
        });
    }
}
