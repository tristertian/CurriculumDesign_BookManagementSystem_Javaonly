package com.bms.repository;

import com.bms.entity.Sale;

import java.util.List;

/**
 * 销售记录数据访问接口。
 */
public interface SaleRepository {

    /**
     * 保存销售记录。
     *
     * @param sale 销售记录
     * @return 保存后的销售记录
     */
    Sale save(Sale sale);

    /**
     * 查询全部销售记录，按时间倒序。
     *
     * @return 销售记录列表
     */
    List<Sale> findAll();

    /**
     * 统计销售总笔数。
     *
     * @return 总笔数
     */
    long count();

    /**
     * 统计销售总金额。
     *
     * @return 总金额
     */
    java.math.BigDecimal totalAmount();
}
