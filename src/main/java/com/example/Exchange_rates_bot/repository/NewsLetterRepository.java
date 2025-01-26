package com.example.Exchange_rates_bot.repository;

import com.example.Exchange_rates_bot.entity.Newsletter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NewsLetterRepository extends JpaRepository<Newsletter,Long> {
    List<Newsletter> findAllBySubscriberId(Long id);
    Newsletter findByCharCodeAndSubscriberId(String charCode,Long id);
    Newsletter findByCharCode(String charCode);
    @Transactional
    void deleteAllFindBySubscriberId(Long id);
}
