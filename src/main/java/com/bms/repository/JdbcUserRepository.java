package com.bms.repository;

import com.bms.entity.User;
import com.bms.util.DatabaseUtil;

import java.sql.*;
import java.util.Optional;

/**
 * 基于 JDBC 的用户数据访问实现。
 */
public class JdbcUserRepository implements UserRepository {

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getString("username"),
                rs.getString("password"),
                User.Role.valueOf(rs.getString("role"))
        );
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败: " + e.getMessage(), e);
        }
    }

    @Override
    public User save(User user) {
        String sql = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getRole().name());
            ps.executeUpdate();
            return user;
        } catch (SQLException e) {
            throw new RuntimeException("保存用户失败: " + e.getMessage(), e);
        }
    }
}
