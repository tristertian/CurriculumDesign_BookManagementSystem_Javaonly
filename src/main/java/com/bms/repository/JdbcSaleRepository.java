package com.bms.repository;

import com.bms.entity.Sale;
import com.bms.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 基于 JDBC 的销售记录数据访问实现。
 */
public class JdbcSaleRepository implements SaleRepository {

    private Sale mapRow(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getLong("id"));
        sale.setIsbn(rs.getString("isbn"));
        sale.setTitle(rs.getString("title"));
        sale.setQuantity(rs.getInt("quantity"));
        sale.setAmount(rs.getBigDecimal("amount"));
        Timestamp timestamp = rs.getTimestamp("sale_time");
        sale.setSaleTime(timestamp != null ? timestamp.toLocalDateTime() : null);
        return sale;
    }

    @Override
    public Sale save(Sale sale) {
        String sql = "INSERT INTO sales (isbn, title, quantity, amount, sale_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sale.getIsbn());
            ps.setString(2, sale.getTitle());
            ps.setInt(3, sale.getQuantity());
            ps.setBigDecimal(4, sale.getAmount());
            ps.setTimestamp(5, Timestamp.valueOf(sale.getSaleTime()));
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    sale.setId(keys.getLong(1));
                }
            }
            return sale;
        } catch (SQLException e) {
            throw new RuntimeException("保存销售记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Sale> findAll() {
        String sql = "SELECT * FROM sales ORDER BY sale_time DESC";
        List<Sale> sales = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                sales.add(mapRow(rs));
            }
            return sales;
        } catch (SQLException e) {
            throw new RuntimeException("查询销售记录失败: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM sales";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计销售笔数失败: " + e.getMessage(), e);
        }
    }

    @Override
    public BigDecimal totalAmount() {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM sales";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getBigDecimal(1);
            }
            return BigDecimal.ZERO;
        } catch (SQLException e) {
            throw new RuntimeException("统计销售金额失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, BigDecimal[]> statByDay() {
        String sql = "SELECT FORMATDATETIME(sale_time, 'yyyy-MM-dd') AS period, " +
                "SUM(quantity) AS total_quantity, SUM(amount) AS total_amount " +
                "FROM sales GROUP BY period ORDER BY period";
        return executeStatQuery(sql);
    }

    @Override
    public Map<String, BigDecimal[]> statByMonth() {
        String sql = "SELECT FORMATDATETIME(sale_time, 'yyyy-MM') AS period, " +
                "SUM(quantity) AS total_quantity, SUM(amount) AS total_amount " +
                "FROM sales GROUP BY period ORDER BY period";
        return executeStatQuery(sql);
    }

    @Override
    public Map<String, BigDecimal[]> statByYear() {
        String sql = "SELECT FORMATDATETIME(sale_time, 'yyyy') AS period, " +
                "SUM(quantity) AS total_quantity, SUM(amount) AS total_amount " +
                "FROM sales GROUP BY period ORDER BY period";
        return executeStatQuery(sql);
    }

    private Map<String, BigDecimal[]> executeStatQuery(String sql) {
        Map<String, BigDecimal[]> result = new LinkedHashMap<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String period = rs.getString("period");
                BigDecimal quantity = BigDecimal.valueOf(rs.getLong("total_quantity"));
                BigDecimal amount = rs.getBigDecimal("total_amount");
                result.put(period, new BigDecimal[]{quantity, amount});
            }
            return result;
        } catch (SQLException e) {
            throw new RuntimeException("销售统计查询失败: " + e.getMessage(), e);
        }
    }
}
