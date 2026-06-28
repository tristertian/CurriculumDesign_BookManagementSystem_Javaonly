package com.bms.ui;

import com.bms.service.SaleService;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 销售统计图表面板。
 */
public class SalesChartPanel extends JPanel {

    private final SaleService saleService;

    private final JComboBox<String> periodCombo = new JComboBox<>(new String[]{"按日", "按月", "按年"});
    private final JPanel chartContainer = new JPanel(new GridLayout(1, 2, 15, 0));

    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Font TITLE_FONT = getChartFont(Font.BOLD, 16);
    private static final Font LABEL_FONT = getChartFont(Font.PLAIN, 12);
    private static final Font TICK_LABEL_FONT = getChartFont(Font.PLAIN, 10);
    private static final Font LEGEND_FONT = getChartFont(Font.PLAIN, 11);

    public SalesChartPanel(SaleService saleService) {
        this.saleService = saleService;
        initUI();
        refreshCharts();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                "销售统计",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("Microsoft YaHei", Font.BOLD, 13),
                new Color(60, 60, 60)));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(new JLabel("统计周期:"));
        controlPanel.add(periodCombo);

        JButton refreshButton = new JButton("刷新图表");
        stylePrimaryButton(refreshButton);
        refreshButton.addActionListener(e -> refreshCharts());
        controlPanel.add(refreshButton);

        add(controlPanel, BorderLayout.NORTH);

        chartContainer.setBackground(Color.WHITE);
        add(chartContainer, BorderLayout.CENTER);

        periodCombo.addActionListener(e -> refreshCharts());
    }

    private void stylePrimaryButton(JButton button) {
        button.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 14, 5, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    public void refreshCharts() {
        String selected = (String) periodCombo.getSelectedItem();
        Map<String, BigDecimal[]> data = switch (selected) {
            case "按日" -> saleService.statSalesByDay();
            case "按月" -> saleService.statSalesByMonth();
            case "按年" -> saleService.statSalesByYear();
            default -> saleService.statSalesByDay();
        };

        chartContainer.removeAll();
        chartContainer.add(createBarChart(data, selected));
        chartContainer.add(createPieChart(data, selected));
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    private ChartPanel createBarChart(Map<String, BigDecimal[]> data, String period) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, BigDecimal[]> entry : data.entrySet()) {
            dataset.addValue(entry.getValue()[1], "销售额", entry.getKey());
        }

        JFreeChart chart = ChartFactory.createBarChart(
                period + "销售额统计",
                "时间",
                "销售额（元）",
                dataset
        );

        chart.setTitle(new TextTitle(period + "销售额统计", TITLE_FONT));

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(220, 220, 220));

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, PRIMARY_COLOR);
        renderer.setDrawBarOutline(false);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setLabelFont(LABEL_FONT);
        domainAxis.setTickLabelFont(TICK_LABEL_FONT);
        if (data.size() > 6) {
            domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        }

        plot.getRangeAxis().setLabelFont(LABEL_FONT);
        plot.getRangeAxis().setTickLabelFont(TICK_LABEL_FONT);

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(LEGEND_FONT);
        }

        return new ChartPanel(chart);
    }

    private ChartPanel createPieChart(Map<String, BigDecimal[]> data, String period) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        for (Map.Entry<String, BigDecimal[]> entry : data.entrySet()) {
            dataset.setValue(entry.getKey(), entry.getValue()[0]);
        }

        JFreeChart chart = ChartFactory.createPieChart(
                period + "销量占比",
                dataset,
                true,
                true,
                false
        );

        chart.setTitle(new TextTitle(period + "销量占比", TITLE_FONT));

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelFont(LABEL_FONT);

        LegendTitle legend = chart.getLegend();
        if (legend != null) {
            legend.setItemFont(LEGEND_FONT);
        }

        return new ChartPanel(chart);
    }

    private static Font getChartFont(int style, int size) {
        String[] fontNames = {"Microsoft YaHei", "SimHei", "SimSun", "Dialog"};
        for (String name : fontNames) {
            for (Font font : GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts()) {
                if (font.getName().equals(name)) {
                    return new Font(name, style, size);
                }
            }
        }
        return new Font("Dialog", style, size);
    }
}
