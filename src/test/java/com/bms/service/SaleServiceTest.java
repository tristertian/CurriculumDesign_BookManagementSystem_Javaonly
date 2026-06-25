package com.bms.service;

import com.bms.entity.Sale;
import com.bms.repository.SaleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SaleService 单元测试。
 */
class SaleServiceTest {

    private SaleService service;
    private InMemorySaleRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemorySaleRepository();
        service = new SaleService(repository);
    }

    @Test
    void recordSale_shouldSaveAndReturn() {
        Sale sale = service.recordSale("978-7-111", "Java 核心技术", 3, new BigDecimal("297.00"));
        assertNotNull(sale.getId());
        assertEquals("978-7-111", sale.getIsbn());
        assertEquals(3, sale.getQuantity());
        assertEquals(new BigDecimal("297.00"), sale.getAmount());
        assertNotNull(sale.getSaleTime());
    }

    @Test
    void findAllSales_shouldReturnInReverseOrder() {
        service.recordSale("111", "A", 1, new BigDecimal("10.00"));
        service.recordSale("222", "B", 2, new BigDecimal("20.00"));

        List<Sale> sales = service.findAllSales();
        assertEquals(2, sales.size());
        assertEquals("222", sales.get(0).getIsbn());
    }

    @Test
    void getSaleCount_shouldReturnTotal() {
        service.recordSale("111", "A", 1, new BigDecimal("10.00"));
        service.recordSale("222", "B", 2, new BigDecimal("20.00"));
        assertEquals(2, service.getSaleCount());
    }

    @Test
    void getTotalAmount_shouldReturnSum() {
        service.recordSale("111", "A", 1, new BigDecimal("10.00"));
        service.recordSale("222", "B", 2, new BigDecimal("20.00"));
        assertEquals(new BigDecimal("30.00"), service.getTotalAmount());
    }

    private static class InMemorySaleRepository implements SaleRepository {

        private final List<Sale> sales = new ArrayList<>();
        private long nextId = 1;

        @Override
        public Sale save(Sale sale) {
            sale.setId(nextId++);
            sales.add(0, sale);
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
