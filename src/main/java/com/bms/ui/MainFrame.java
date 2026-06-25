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

    public MainFrame(BookService bookService, SaleService saleService, User user) {
        this.bookService = bookService;
        this.saleService = saleService;
        this.user = user;
        initUI();
    }

    private void initUI() {
        setTitle("图书管理系统");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        // 顶部状态栏
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        headerPanel.setBackground(new Color(41, 128, 185));

        JLabel titleLabel = new JLabel("图书管理系统");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        JLabel userLabel = new JLabel("当前用户: " + user.getUsername() + " (" + (user.isAdmin() ? "管理员" : "店员") + ")");
        userLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userLabel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // 左侧导航栏
        navList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        navList.setFixedCellHeight(45);
        navList.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        navList.setBorder(new EmptyBorder(10, 0, 10, 0));
        navList.setBackground(new Color(245, 245, 245));
        navList.setCellRenderer(new NavCellRenderer());

        JScrollPane navScrollPane = new JScrollPane(navList);
        navScrollPane.setPreferredSize(new Dimension(160, 0));
        navScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, new Color(220, 220, 220)));
        add(navScrollPane, BorderLayout.WEST);

        // 右侧内容区
        contentPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
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

        // 默认选中第一项
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
        if (user.isAdmin()) {
            addNavItem("统计分析", new StatsPanel(bookService));
        }
    }

    private void addNavItem(String name, JPanel panel) {
        navModel.addElement(name);
        contentPanel.add(panel, name);
    }

    /**
     * 导航项自定义渲染器。
     */
    private static class NavCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setBorder(new EmptyBorder(0, 10, 0, 10));
            if (isSelected) {
                label.setBackground(new Color(41, 128, 185));
                label.setForeground(Color.WHITE);
                label.setFont(label.getFont().deriveFont(Font.BOLD));
            } else {
                label.setBackground(list.getBackground());
                label.setForeground(list.getForeground());
                label.setFont(label.getFont().deriveFont(Font.PLAIN));
            }
            label.setOpaque(true);
            return label;
        }
    }
}
