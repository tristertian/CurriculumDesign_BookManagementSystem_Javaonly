package com.bms.ui;

import com.bms.entity.Book;

import javax.swing.table.AbstractTableModel;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书列表表格模型。
 */
public class BookTableModel extends AbstractTableModel {

    private static final String[] COLUMN_NAMES = {"ISBN", "书名", "出版社", "作者", "库存", "价格"};

    private List<Book> books = new ArrayList<>();

    public void setBooks(List<Book> books) {
        this.books = books == null ? new ArrayList<>() : new ArrayList<>(books);
        fireTableDataChanged();
    }

    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public Book getBookAt(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= books.size()) {
            return null;
        }
        return books.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return books.size();
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
        Book book = books.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> book.getIsbn();
            case 1 -> book.getTitle();
            case 2 -> book.getPublisher();
            case 3 -> book.getAuthor();
            case 4 -> book.getStock();
            case 5 -> book.getPrice();
            default -> null;
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return switch (columnIndex) {
            case 4 -> Integer.class;
            case 5 -> BigDecimal.class;
            default -> String.class;
        };
    }
}
