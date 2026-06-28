package com.bms.repository;

import com.bms.entity.Sale;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
    BigDecimal totalAmount();

    /**
     * 按日统计销售额与销量。
     *
     * @return key: 日期字符串(yyyy-MM-dd), value: [销量, 销售额]
     */
    Map<String, BigDecimal[]> statByDay();

    /**
     * 按月统计销售额与销量。
     *
     * @return key: 日期字符串(yyyy-MM), value: [销量, 销售额]
     */
    Map<String, BigDecimal[]> statByMonth();

    /**
     * 按年统计销售额与销量。
     *
     * @return key: 日期字符串(yyyy), value: [销量, 销售额]
     */
    Map<String, BigDecimal[]> statByYear();
}
