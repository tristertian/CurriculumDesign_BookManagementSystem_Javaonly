package com.bms.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * 库存预警渲染器：库存低于阈值时高亮显示。
 */
public class StockWarningRenderer extends DefaultTableCellRenderer {

    private static final int STOCK_WARNING_THRESHOLD = 10;

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (!isSelected) {
            Object stockValue = table.getModel().getValueAt(row, 4);
            if (stockValue instanceof Integer stock && stock < STOCK_WARNING_THRESHOLD) {
                component.setBackground(new Color(255, 200, 200));
                component.setForeground(Color.RED);
            } else {
                component.setBackground(table.getBackground());
                component.setForeground(table.getForeground());
            }
        }
        return component;
    }
}
