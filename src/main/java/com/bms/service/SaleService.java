package com.bms.service;

import com.bms.entity.Sale;
import com.bms.repository.SaleRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 销售记录业务逻辑层。
 */
public class SaleService {

    private final SaleRepository saleRepository;

    public SaleService(SaleRepository saleRepository) {
        this.saleRepository = saleRepository;
    }

    /**
     * 记录一次销售。
     *
     * @param isbn     ISBN
     * @param title    书名
     * @param quantity 数量
     * @param amount   金额
     * @return 保存后的销售记录
     */
    public Sale recordSale(String isbn, String title, int quantity, BigDecimal amount) {
        Sale sale = new Sale(isbn, title, quantity, amount, LocalDateTime.now());
        return saleRepository.save(sale);
    }

    /**
     * 查询全部销售记录。
     *
     * @return 销售记录列表
     */
    public List<Sale> findAllSales() {
        return saleRepository.findAll();
    }

    /**
     * 获取销售总笔数。
     *
     * @return 总笔数
     */
    public long getSaleCount() {
        return saleRepository.count();
    }

    /**
     * 获取销售总金额。
     *
     * @return 总金额
     */
    public BigDecimal getTotalAmount() {
        return saleRepository.totalAmount();
    }
}
