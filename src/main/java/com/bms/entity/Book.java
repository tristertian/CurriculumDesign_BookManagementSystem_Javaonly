package com.bms.entity;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 图书实体类。
 */
public class Book {

    private String isbn;
    private String title;
    private String publisher;
    private String author;
    private int stock;
    private BigDecimal price;

    public Book() {
    }

    public Book(String isbn, String title, String publisher, String author, int stock, BigDecimal price) {
        this.isbn = isbn;
        this.title = title;
        this.publisher = publisher;
        this.author = author;
        this.stock = stock;
        this.price = price;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 计算购买指定数量的应收金额。
     *
     * @param quantity 购买数量
     * @return 应收金额
     */
    public BigDecimal calculateAmount(int quantity) {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book book)) return false;
        return Objects.equals(isbn, book.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn);
    }

    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", publisher='" + publisher + '\'' +
                ", author='" + author + '\'' +
                ", stock=" + stock +
                ", price=" + price +
                '}';
    }
}
