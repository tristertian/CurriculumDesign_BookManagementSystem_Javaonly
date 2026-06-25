package com.bms;

import com.bms.entity.User;
import com.bms.repository.*;
import com.bms.service.BookService;
import com.bms.service.SaleService;
import com.bms.service.UserService;
import com.bms.ui.LoginDialog;
import com.bms.ui.MainFrame;
import com.bms.util.DataInitializer;
import com.bms.util.DatabaseUtil;

import javax.swing.*;
import java.util.Optional;

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

        UserRepository userRepository = new JdbcUserRepository();
        UserService userService = new UserService(userRepository);
        userService.initializeDefaultUsers();

        BookRepository bookRepository = new JdbcBookRepository();
        DataInitializer.initializeIfEmpty(bookRepository, 100);

        SaleRepository saleRepository = new JdbcSaleRepository();
        SaleService saleService = new SaleService(saleRepository);
        BookService bookService = new BookService(bookRepository, saleService);

        SwingUtilities.invokeLater(() -> {
            LoginDialog loginDialog = new LoginDialog(null, userService);
            loginDialog.setVisible(true);

            Optional<User> userOpt = loginDialog.getLoggedInUser();
            if (userOpt.isEmpty()) {
                System.exit(0);
            }

            MainFrame frame = new MainFrame(bookService, saleService, userOpt.get());
            frame.setVisible(true);
        });
    }
}
