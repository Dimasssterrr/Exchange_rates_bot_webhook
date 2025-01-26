package com.example.Exchange_rates_bot.repository;

import com.example.Exchange_rates_bot.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    Currency findByCharCode(String charCode);

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE currency", nativeQuery = true)
    void truncate();
}
