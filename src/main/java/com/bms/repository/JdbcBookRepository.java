package com.bms.repository;

import com.bms.entity.Book;
import com.bms.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 基于 JDBC 的图书数据访问实现。
 */
public class JdbcBookRepository implements BookRepository {

    private Book mapRow(ResultSet rs) throws SQLException {
        return new Book(
                rs.getString("isbn"),
                rs.getString("title"),
                rs.getString("publisher"),
                rs.getString("author"),
                rs.getInt("stock"),
                rs.getBigDecimal("price")
        );
    }

    @Override
    public Book save(Book book) {
        String sql = "INSERT INTO books (isbn, title, publisher, author, stock, price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getIsbn());
            ps.setString(2, book.getTitle());
            ps.setString(3, book.getPublisher());
            ps.setString(4, book.getAuthor());
            ps.setInt(5, book.getStock());
            ps.setBigDecimal(6, book.getPrice());
            ps.executeUpdate();
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("保存图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Book update(Book book) {
        String sql = "UPDATE books SET title = ?, publisher = ?, author = ?, stock = ?, price = ? WHERE isbn = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, book.getTitle());
            ps.setString(2, book.getPublisher());
            ps.setString(3, book.getAuthor());
            ps.setInt(4, book.getStock());
            ps.setBigDecimal(5, book.getPrice());
            ps.setString(6, book.getIsbn());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("未找到要更新的图书: " + book.getIsbn());
            }
            return book;
        } catch (SQLException e) {
            throw new RuntimeException("更新图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByIsbn(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("删除图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Book> findByIsbn(String isbn) {
        String sql = "SELECT * FROM books WHERE isbn = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, isbn);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("查询图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findAll() {
        String sql = "SELECT * FROM books";
        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("查询全部图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findByConditions(String title, String isbn, String author, String publisher) {
        StringBuilder sql = new StringBuilder("SELECT * FROM books WHERE 1 = 1");
        List<String> params = new ArrayList<>();

        if (title != null && !title.isBlank()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title.trim() + "%");
        }
        if (isbn != null && !isbn.isBlank()) {
            sql.append(" AND isbn LIKE ?");
            params.add("%" + isbn.trim() + "%");
        }
        if (author != null && !author.isBlank()) {
            sql.append(" AND author LIKE ?");
            params.add("%" + author.trim() + "%");
        }
        if (publisher != null && !publisher.isBlank()) {
            sql.append(" AND publisher LIKE ?");
            params.add("%" + publisher.trim() + "%");
        }

        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setString(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    books.add(mapRow(rs));
                }
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("条件查询图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Book> findAllSorted(String sortField, boolean ascending) {
        String column = switch (sortField) {
            case "price" -> "price";
            case "stock" -> "stock";
            case "author" -> "author";
            case "publisher" -> "publisher";
            default -> "price";
        };
        String direction = ascending ? "ASC" : "DESC";
        String sql = "SELECT * FROM books ORDER BY " + column + " " + direction;

        List<Book> books = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                books.add(mapRow(rs));
            }
            return books;
        } catch (SQLException e) {
            throw new RuntimeException("排序查询图书失败: " + e.getMessage(), e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM books";
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("统计图书数量失败: " + e.getMessage(), e);
        }
    }
}
