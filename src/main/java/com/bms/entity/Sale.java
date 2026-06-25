package com.bms.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 销售记录实体。
 */
public class Sale {

    private Long id;
    private String isbn;
    private String title;
    private int quantity;
    private BigDecimal amount;
    private LocalDateTime saleTime;

    public Sale() {
    }

    public Sale(String isbn, String title, int quantity, BigDecimal amount, LocalDateTime saleTime) {
        this.isbn = isbn;
        this.title = title;
        this.quantity = quantity;
        this.amount = amount;
        this.saleTime = saleTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDateTime getSaleTime() {
        return saleTime;
    }

    public void setSaleTime(LocalDateTime saleTime) {
        this.saleTime = saleTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sale sale)) return false;
        return Objects.equals(id, sale.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", quantity=" + quantity +
                ", amount=" + amount +
                ", saleTime=" + saleTime +
                '}';
    }
}
