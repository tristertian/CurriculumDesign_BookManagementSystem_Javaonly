package com.bms.ui;

import com.bms.entity.User;
import com.bms.service.UserService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Optional;

/**
 * 用户登录对话框。
 */
public class LoginDialog extends JDialog {

    private final UserService userService;
    private User loggedInUser;

    private final JTextField usernameField = new JTextField(18);
    private final JPasswordField passwordField = new JPasswordField(18);

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color BG_COLOR = Color.WHITE;

    public LoginDialog(JFrame owner, UserService userService) {
        super(owner, "用户登录", true);
        this.userService = userService;
        initUI();
        setSize(380, 360);
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initUI() {
        getContentPane().setBackground(BG_COLOR);
        setLayout(new BorderLayout());

        // 顶部标题栏
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(BG_COLOR);
        headerPanel.setBorder(new EmptyBorder(25, 0, 10, 0));

        ImageIcon loadingIcon = loadIcon("/icons/loading.png");
        JLabel iconLabel = new JLabel(loadingIcon, SwingConstants.CENTER);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("用户登录", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // 表单区
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BG_COLOR);
        formPanel.setBorder(new EmptyBorder(25, 35, 15, 35));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        styleTextField(usernameField);
        styleTextField(passwordField);
        passwordField.addActionListener(e -> doLogin());

        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(createLabel("用户名"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        formPanel.add(createLabel("密码"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        formPanel.add(passwordField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // 按钮区
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(BG_COLOR);
        buttonPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JButton loginButton = new JButton("登录");
        stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> doLogin());

        JButton cancelButton = new JButton("取消");
        styleSecondaryButton(cancelButton);
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        label.setForeground(new Color(60, 60, 60));
        return label;
    }

    private void styleTextField(JTextField field) {
        field.setPreferredSize(new Dimension(180, 30));
        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(4, 8, 4, 8)
        ));
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 25, 8, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void styleSecondaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        button.setForeground(new Color(80, 80, 80));
        button.setBackground(new Color(210, 210, 210));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(8, 25, 8, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
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

    private ImageIcon loadIcon(String path) {
        java.net.URL url = getClass().getResource(path);
        if (url == null) {
            System.err.println("图标加载失败: " + path);
            return new ImageIcon();
        }
        return new ImageIcon(url);
    }
}
