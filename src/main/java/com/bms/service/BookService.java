package com.bms.service;

import com.bms.entity.Book;
import com.bms.repository.BookRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 图书业务逻辑层。
 */
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     * 添加图书。
     *
     * @param book 图书
     * @return 添加后的图书
     */
    public Book addBook(Book book) {
        validateBook(book);
        if (bookRepository.findByIsbn(book.getIsbn()).isPresent()) {
            throw new IllegalArgumentException("ISBN 已存在: " + book.getIsbn());
        }
        return bookRepository.save(book);
    }

    /**
     * 更新图书。
     *
     * @param book 图书
     * @return 更新后的图书
     */
    public Book updateBook(Book book) {
        validateBook(book);
        if (bookRepository.findByIsbn(book.getIsbn()).isEmpty()) {
            throw new IllegalArgumentException("该编号不存在: " + book.getIsbn());
        }
        return bookRepository.update(book);
    }

    /**
     * 删除图书。
     *
     * @param isbn ISBN
     */
    public void deleteBook(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN 不能为空");
        }
        if (bookRepository.findByIsbn(isbn).isEmpty()) {
            throw new IllegalArgumentException("该编号不存在: " + isbn);
        }
        bookRepository.deleteByIsbn(isbn);
    }

    /**
     * 根据 ISBN 查询图书。
     *
     * @param isbn ISBN
     * @return 图书 optional
     */
    public Optional<Book> findByIsbn(String isbn) {
        if (isbn == null || isbn.isBlank()) {
            return Optional.empty();
        }
        return bookRepository.findByIsbn(isbn.trim());
    }

    /**
     * 查询全部图书。
     *
     * @return 图书列表
     */
    public List<Book> findAllBooks() {
        return bookRepository.findAll();
    }

    /**
     * 多条件查询图书。
     *
     * @param title     书名
     * @param isbn      ISBN
     * @param author    作者
     * @param publisher 出版社
     * @return 图书列表
     */
    public List<Book> searchBooks(String title, String isbn, String author, String publisher) {
        return bookRepository.findByConditions(title, isbn, author, publisher);
    }

    /**
     * 排序查询图书。
     *
     * @param sortField 排序字段
     * @param ascending 是否升序
     * @return 图书列表
     */
    public List<Book> listBooksSorted(String sortField, boolean ascending) {
        return bookRepository.findAllSorted(sortField, ascending);
    }

    /**
     * 获取图书总数。
     *
     * @return 总数
     */
    public long getBookCount() {
        return bookRepository.count();
    }

    /**
     * 销售图书。
     *
     * @param isbn     ISBN
     * @param quantity 购买数量
     * @return 销售金额
     */
    public BigDecimal sellBook(String isbn, int quantity) {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN 不能为空");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("购买数量必须大于 0");
        }

        Book book = bookRepository.findByIsbn(isbn.trim())
                .orElseThrow(() -> new IllegalArgumentException("该编号不存在: " + isbn));

        if (book.getStock() < quantity) {
            throw new IllegalStateException("库存不足，当前库存: " + book.getStock());
        }

        BigDecimal amount = book.calculateAmount(quantity);
        book.setStock(book.getStock() - quantity);
        bookRepository.update(book);
        return amount;
    }

    private void validateBook(Book book) {
        if (book == null) {
            throw new IllegalArgumentException("图书信息不能为空");
        }
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN 不能为空");
        }
        if (book.getTitle() == null || book.getTitle().isBlank()) {
            throw new IllegalArgumentException("书名不能为空");
        }
        if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("价格必须为非负数");
        }
        if (book.getStock() < 0) {
            throw new IllegalArgumentException("库存量必须为非负数");
        }
    }
}
