package com.bms.service;

import com.bms.entity.Book;
import com.bms.entity.Sale;
import com.bms.repository.BookRepository;
import com.bms.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * BookService 单元测试（使用内存模拟 Repository）。
 */
class BookServiceTest {

    private BookService service;
    private InMemorySaleRepository saleRepository;

    @BeforeEach
    void setUp() {
        saleRepository = new InMemorySaleRepository();
        SaleService saleService = new SaleService(saleRepository);
        service = new BookService(new InMemoryBookRepository(), saleService);
    }

    @Test
    void addBook_shouldSucceed() {
        Book book = new Book("978-7-111", "Java 核心技术", "机械工业出版社", "Cay", 10, new BigDecimal("99.00"));
        Book saved = service.addBook(book);
        assertEquals("978-7-111", saved.getIsbn());
        assertEquals(1, service.getBookCount());
    }

    @Test
    void addBook_duplicateIsbn_shouldThrow() {
        Book book = new Book("978-7-111", "Java 核心技术", "机械工业出版社", "Cay", 10, new BigDecimal("99.00"));
        service.addBook(book);
        assertThrows(IllegalArgumentException.class, () -> service.addBook(book));
    }

    @Test
    void addBook_negativePrice_shouldThrow() {
        Book book = new Book("978-7-111", "Java 核心技术", "机械工业出版社", "Cay", 10, new BigDecimal("-1"));
        assertThrows(IllegalArgumentException.class, () -> service.addBook(book));
    }

    @Test
    void sellBook_shouldDeductStockAndReturnAmount() {
        Book book = new Book("978-7-111", "Java 核心技术", "机械工业出版社", "Cay", 10, new BigDecimal("99.00"));
        service.addBook(book);

        BigDecimal amount = service.sellBook("978-7-111", 3);
        assertEquals(new BigDecimal("297.00"), amount);

        Book updated = service.findByIsbn("978-7-111").orElseThrow();
        assertEquals(7, updated.getStock());

        assertEquals(1, saleRepository.count());
        Sale sale = saleRepository.findAll().get(0);
        assertEquals("978-7-111", sale.getIsbn());
        assertEquals(3, sale.getQuantity());
        assertEquals(new BigDecimal("297.00"), sale.getAmount());
    }

    @Test
    void sellBook_insufficientStock_shouldThrow() {
        Book book = new Book("978-7-111", "Java 核心技术", "机械工业出版社", "Cay", 2, new BigDecimal("99.00"));
        service.addBook(book);
        assertThrows(IllegalStateException.class, () -> service.sellBook("978-7-111", 3));
        assertEquals(0, saleRepository.count());
    }

    @Test
    void searchBooks_shouldSupportFuzzySearch() {
        service.addBook(new Book("111", "Java 编程思想", "机械工业出版社", "Bruce", 5, new BigDecimal("108.00")));
        service.addBook(new Book("222", "Effective Python", "Addison-Wesley", "Joshua", 3, new BigDecimal("68.00")));

        List<Book> result = service.searchBooks("编程思想", null, null, null);
        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getIsbn());
    }

    @Test
    void listBooksSorted_shouldSortByPriceDescending() {
        service.addBook(new Book("111", "A", "P1", "A1", 5, new BigDecimal("50.00")));
        service.addBook(new Book("222", "B", "P2", "A2", 3, new BigDecimal("100.00")));

        List<Book> sorted = service.listBooksSorted("price", false);
        assertEquals("222", sorted.get(0).getIsbn());
        assertEquals("111", sorted.get(1).getIsbn());
    }

    private static class InMemoryBookRepository implements BookRepository {

        private final Map<String, Book> books = new HashMap<>();

        @Override
        public Book save(Book book) {
            books.put(book.getIsbn(), book);
            return book;
        }

        @Override
        public Book update(Book book) {
            books.put(book.getIsbn(), book);
            return book;
        }

        @Override
        public void deleteByIsbn(String isbn) {
            books.remove(isbn);
        }

        @Override
        public Optional<Book> findByIsbn(String isbn) {
            return Optional.ofNullable(books.get(isbn));
        }

        @Override
        public List<Book> findAll() {
            return new ArrayList<>(books.values());
        }

        @Override
        public List<Book> findByConditions(String title, String isbn, String author, String publisher) {
            return books.values().stream()
                    .filter(b -> title == null || b.getTitle().contains(title))
                    .filter(b -> isbn == null || b.getIsbn().contains(isbn))
                    .filter(b -> author == null || (b.getAuthor() != null && b.getAuthor().contains(author)))
                    .filter(b -> publisher == null || (b.getPublisher() != null && b.getPublisher().contains(publisher)))
                    .toList();
        }

        @Override
        public List<Book> findAllSorted(String sortField, boolean ascending) {
            List<Book> list = new ArrayList<>(books.values());
            list.sort((b1, b2) -> {
                int cmp = b1.getPrice().compareTo(b2.getPrice());
                return ascending ? cmp : -cmp;
            });
            return list;
        }

        @Override
        public long count() {
            return books.size();
        }
    }

    private static class InMemorySaleRepository implements SaleRepository {

        private final List<Sale> sales = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Sale save(Sale sale) {
            sale.setId(nextId++);
            sales.add(sale);
            return sale;
        }

        @Override
        public List<Sale> findAll() {
            return new ArrayList<>(sales);
        }

        @Override
        public long count() {
            return sales.size();
        }

        @Override
        public BigDecimal totalAmount() {
            return sales.stream()
                    .map(Sale::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
    }
}
