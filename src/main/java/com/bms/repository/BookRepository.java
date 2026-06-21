package com.bms.repository;

import com.bms.entity.Book;

import java.util.List;
import java.util.Optional;

/**
 * 图书数据访问接口。
 */
public interface BookRepository {

    /**
     * 保存图书。
     *
     * @param book 图书实体
     * @return 保存后的图书
     */
    Book save(Book book);

    /**
     * 更新图书信息。
     *
     * @param book 图书实体
     * @return 更新后的图书
     */
    Book update(Book book);

    /**
     * 根据 ISBN 删除图书。
     *
     * @param isbn ISBN
     */
    void deleteByIsbn(String isbn);

    /**
     * 根据 ISBN 查询图书。
     *
     * @param isbn ISBN
     * @return 图书 optional
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * 查询全部图书。
     *
     * @return 图书列表
     */
    List<Book> findAll();

    /**
     * 多条件模糊查询图书。
     *
     * @param title     书名，可为空
     * @param isbn      ISBN，可为空
     * @param author    作者，可为空
     * @param publisher 出版社，可为空
     * @return 图书列表
     */
    List<Book> findByConditions(String title, String isbn, String author, String publisher);

    /**
     * 按指定字段和方向排序查询。
     *
     * @param sortField 排序字段
     * @param ascending 是否升序
     * @return 图书列表
     */
    List<Book> findAllSorted(String sortField, boolean ascending);

    /**
     * 获取图书总数。
     *
     * @return 总数
     */
    long count();
}
