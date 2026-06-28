package com.bms.ui;

import com.bms.entity.Sale;
import com.bms.service.SaleService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * 销售记录面板。
 */
public class SalesHistoryPanel extends JPanel {

    private final SaleService saleService;

    private final SaleTableModel tableModel = new SaleTableModel();
    private final JTable saleTable = new JTable(tableModel);

    private final JLabel countLabel = new JLabel("0");
    private final JLabel amountLabel = new JLabel("¥0.00");

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SalesHistoryPanel(SaleService saleService) {
        this.saleService = saleService;
        initUI();
        refreshSales();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // 标题
        JLabel titleLabel = new JLabel("销售记录");
        titleLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 18));
        titleLabel.setForeground(new Color(50, 50, 50));

        // KPI 卡片区
        JPanel kpiPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        kpiPanel.setBackground(Color.WHITE);
        kpiPanel.add(createKpiCard("销售笔数", countLabel, PRIMARY_COLOR));
        kpiPanel.add(createKpiCard("销售总额", amountLabel, SUCCESS_COLOR));

        JButton refreshButton = new JButton("刷新");
        stylePrimaryButton(refreshButton);
        refreshButton.addActionListener(e -> refreshSales());
        JPanel refreshPanel = new JPanel(new BorderLayout());
        refreshPanel.setBackground(Color.WHITE);
        refreshPanel.add(refreshButton, BorderLayout.SOUTH);
        kpiPanel.add(refreshPanel);

        // 表格区
        saleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        saleTable.setAutoCreateRowSorter(true);
        saleTable.setRowHeight(28);
        saleTable.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        saleTable.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 12));
        saleTable.getTableHeader().setBackground(PRIMARY_COLOR);
        saleTable.getTableHeader().setForeground(Color.WHITE);
        saleTable.getTableHeader().setPreferredSize(new Dimension(0, 32));
        saleTable.setGridColor(new Color(230, 230, 230));

        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)), "销售明细"));
        tablePanel.add(new JScrollPane(saleTable), BorderLayout.CENTER);

        // 组合
        JPanel topPanel = new JPanel(new BorderLayout(15, 15));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(titleLabel, BorderLayout.NORTH);
        topPanel.add(kpiPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
        add(tablePanel, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        valueLabel.setForeground(accentColor);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(6, 18, 6, 18));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void refreshSales() {
        List<Sale> sales = saleService.findAllSales();
        tableModel.setSales(sales);
        countLabel.setText(String.valueOf(saleService.getSaleCount()));
        amountLabel.setText("¥" + saleService.getTotalAmount());
        resizeColumnWidth();
    }

    private void resizeColumnWidth() {
        for (int column = 0; column < saleTable.getColumnCount(); column++) {
            int width = 80;
            for (int row = 0; row < saleTable.getRowCount(); row++) {
                Object value = saleTable.getValueAt(row, column);
                if (value == null) continue;
                int preferredWidth = saleTable.getCellRenderer(row, column)
                        .getTableCellRendererComponent(saleTable, value, false, false, row, column)
                        .getPreferredSize().width;
                width = Math.max(width, preferredWidth + 20);
            }
            saleTable.getColumnModel().getColumn(column).setPreferredWidth(width);
        }
    }

    private static class SaleTableModel extends AbstractTableModel {

        private static final String[] COLUMN_NAMES = {"ID", "ISBN", "书名", "数量", "金额", "销售时间"};

        private List<Sale> sales = new ArrayList<>();

        public void setSales(List<Sale> sales) {
            this.sales = sales == null ? new ArrayList<>() : new ArrayList<>(sales);
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return sales.size();
        }

        @Override
        public int getColumnCount() {
            return COLUMN_NAMES.length;
        }

        @Override
        public String getColumnName(int column) {
            return COLUMN_NAMES[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Sale sale = sales.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> sale.getId();
                case 1 -> sale.getIsbn();
                case 2 -> sale.getTitle();
                case 3 -> sale.getQuantity();
                case 4 -> sale.getAmount();
                case 5 -> sale.getSaleTime() != null ? sale.getSaleTime().format(FORMATTER) : "";
                default -> null;
            };
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return switch (columnIndex) {
                case 0 -> Long.class;
                case 3 -> Integer.class;
                case 4 -> BigDecimal.class;
                default -> String.class;
            };
        }
    }
}
