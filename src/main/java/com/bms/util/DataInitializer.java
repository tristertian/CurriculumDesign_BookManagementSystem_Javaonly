package com.bms.util;

import com.bms.entity.Book;
import com.bms.repository.BookRepository;
import com.bms.repository.JdbcBookRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

/**
 * 演示数据初始化工具。
 */
public class DataInitializer {

    private static final String[] TITLES = {
            "Java 核心技术", "Effective Java", "深入理解 JVM", "Spring 实战", "算法导论",
            "Clean Code", "设计模式", "重构", "人月神话", "代码大全",
            "Python 编程从入门到实践", "流畅的 Python", "机器学习", "深度学习", "数据结构与算法",
            "操作系统导论", "计算机网络", "数据库系统概念", "编译原理", "计算机组成原理",
            "Head First 设计模式", "Java 并发编程实战", "Spring Boot 实战", "MySQL 必知必会", "Redis 设计与实现",
            "Kafka 权威指南", "Docker 容器与容器云", "Kubernetes 实战", "Git 版本控制管理", "Maven 实战"
    };

    private static final String[] PUBLISHERS = {
            "机械工业出版社", "电子工业出版社", "人民邮电出版社", "清华大学出版社", "高等教育出版社",
            "人民文学出版社", "商务印书馆", "中华书局", "译林出版社", "中信出版社"
    };

    private static final String[] AUTHORS = {
            "张三", "李四", "王五", "赵六", "钱七",
            "Cay Horstmann", "Joshua Bloch", "Martin Fowler", "Robert Martin", "Erich Gamma",
            "周志明", "李刚", "王珊", "萨师煊", "Silberschatz"
    };

    private static final Random RANDOM = new Random();

    private DataInitializer() {
    }

    /**
     * 如果图书表为空，则生成指定数量的演示数据。
     *
     * @param bookRepository 图书仓库
     * @param count          数量
     */
    public static void initializeIfEmpty(BookRepository bookRepository, int count) {
        if (bookRepository.count() > 0) {
            System.out.println("Books table already has data, skipping demo initialization.");
            return;
        }
        List<Book> books = generateBooks(count);
        for (Book book : books) {
            bookRepository.save(book);
        }
        System.out.println("Initialized " + count + " demo books.");
    }

    /**
     * 生成指定数量的图书数据。
     *
     * @param count 数量
     * @return 图书列表
     */
    public static List<Book> generateBooks(int count) {
        List<Book> books = new java.util.ArrayList<>();
        java.util.Set<String> usedIsbns = new java.util.HashSet<>();
        int index = 0;
        while (books.size() < count) {
            Book book = generateBook(index);
            if (usedIsbns.add(book.getIsbn())) {
                books.add(book);
            }
            index++;
        }
        return books;
    }

    private static Book generateBook(int index) {
        String isbn = String.format("978-7-%04d-%04d-%d",
                RANDOM.nextInt(10000),
                RANDOM.nextInt(10000),
                RANDOM.nextInt(10));
        String title = TITLES[RANDOM.nextInt(TITLES.length)] + " " + (index + 1);
        String publisher = PUBLISHERS[RANDOM.nextInt(PUBLISHERS.length)];
        String author = AUTHORS[RANDOM.nextInt(AUTHORS.length)];
        int stock = RANDOM.nextInt(200);
        BigDecimal price = BigDecimal.valueOf(RANDOM.nextDouble() * 200 + 20)
                .setScale(2, java.math.RoundingMode.HALF_UP);
        return new Book(isbn, title, publisher, author, stock, price);
    }

    public static void main(String[] args) {
        DatabaseUtil.initializeDatabase();
        BookRepository repository = new JdbcBookRepository();
        initializeIfEmpty(repository, 100);
        System.out.println("Total books in database: " + repository.count());
    }
}
