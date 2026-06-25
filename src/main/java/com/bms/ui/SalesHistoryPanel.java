package com.bms.ui;

import com.bms.entity.Sale;
import com.bms.service.SaleService;

import javax.swing.*;
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

    private final JLabel countLabel = new JLabel("销售笔数: 0");
    private final JLabel amountLabel = new JLabel("销售总额: 0.00");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SalesHistoryPanel(SaleService saleService) {
        this.saleService = saleService;
        initUI();
        refreshSales();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.add(countLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(amountLabel);

        JButton refreshButton = new JButton("刷新");
        refreshButton.addActionListener(e -> refreshSales());
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(refreshButton);

        add(infoPanel, BorderLayout.NORTH);
        add(new JScrollPane(saleTable), BorderLayout.CENTER);
    }

    public void refreshSales() {
        List<Sale> sales = saleService.findAllSales();
        tableModel.setSales(sales);
        countLabel.setText("销售笔数: " + saleService.getSaleCount());
        amountLabel.setText("销售总额: " + saleService.getTotalAmount());
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
