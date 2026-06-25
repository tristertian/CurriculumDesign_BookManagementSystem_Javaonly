package com.bms.ui;

import com.bms.entity.User;
import com.bms.service.UserService;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

/**
 * 用户登录对话框。
 */
public class LoginDialog extends JDialog {

    private final UserService userService;
    private User loggedInUser;

    private final JTextField usernameField = new JTextField(15);
    private final JPasswordField passwordField = new JPasswordField(15);

    public LoginDialog(JFrame owner, UserService userService) {
        super(owner, "用户登录", true);
        this.userService = userService;
        initUI();
        pack();
        setLocationRelativeTo(owner);
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setResizable(false);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.add(new JLabel("用户名:"));
        formPanel.add(usernameField);
        formPanel.add(new JLabel("密码:"));
        formPanel.add(passwordField);

        passwordField.addActionListener(e -> doLogin());

        JButton loginButton = new JButton("登录");
        loginButton.addActionListener(e -> doLogin());

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> dispose());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);

        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        Optional<User> userOpt = userService.login(username, password);
        if (userOpt.isPresent()) {
            loggedInUser = userOpt.get();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "用户名或密码错误",
                    "登录失败",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    public Optional<User> getLoggedInUser() {
        return Optional.ofNullable(loggedInUser);
    }
}
