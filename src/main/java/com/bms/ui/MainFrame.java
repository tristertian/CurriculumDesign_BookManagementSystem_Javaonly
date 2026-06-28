package com.bms.ui;

import com.bms.entity.User;
import com.bms.service.BookService;
import com.bms.service.SaleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * 图书管理系统主窗口（左侧导航栏布局）。
 */
public class MainFrame extends JFrame {

    private final BookService bookService;
    private final SaleService saleService;
    private final User user;

    private final JPanel contentPanel = new JPanel(new CardLayout());
    private final DefaultListModel<String> navModel = new DefaultListModel<>();
    private final JList<String> navList = new JList<>(navModel);

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color PRIMARY_DARK = new Color(31, 97, 141);
    private static final Color NAV_BG = new Color(245, 245, 245);
    private static final Color NAV_HOVER = new Color(230, 230, 230);

    public MainFrame(BookService bookService, SaleService saleService, User user) {
        this.bookService = bookService;
        this.saleService = saleService;
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 顶部状态栏
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_COLOR);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel titleLabel = new JLabel("图书管理系统");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("当前用户: " + user.getUsername() + " / " + (user.isAdmin() ? "管理员" : "店员"));
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        userLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 左侧导航栏
        navList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        navList.setFixedCellHeight(48);
        navList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        navList.setBorder(new EmptyBorder(10, 0, 10, 0));
        navList.setBackground(NAV_BG);
        navList.setCellRenderer(new NavCellRenderer());

        JScrollPane navScrollPane = new JScrollPane(navList);
        navScrollPane.setPreferredSize(new Dimension(160, 0));
        navScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        add(navScrollPane, BorderLayout.WEST);

        // 右侧内容区
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

        setupPanels();

        navList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = navList.getSelectedValue();
                if (selected != null) {
                    CardLayout layout = (CardLayout) contentPanel.getLayout();
                    layout.show(contentPanel, selected);
                }
            }
        });

        if (navModel.size() > 0) {
            navList.setSelectedIndex(0);
        }
    }

    private void setupPanels() {
        if (user.isAdmin()) {
            addNavItem("数据概览", new DataOverviewPanel(bookService));
            addNavItem("库存管理", new InventoryPanel(bookService));
        }
        addNavItem("图书销售", new SalesPanel(bookService));
        addNavItem("销售记录", new SalesHistoryPanel(saleService));
    }

    private void addNavItem(String name, JPanel panel) {
        navModel.addElement(name);
        contentPanel.add(panel, name);
    }

    private static class NavCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(0, 12, 0, 12));
            label.setOpaque(true);

            if (isSelected) {
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(new Color(60, 60, 60));
                label.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
            }
            return label;
        }
    }
}
