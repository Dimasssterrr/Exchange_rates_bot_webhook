package com.example.Exchange_rates_bot.repository;

import com.example.Exchange_rates_bot.entity.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Subscription findByCharCode(String charCode);
    List<Subscription> findAllBySubscriberId(Long id);

    @Transactional
    void deleteAllBySubscriberId(Long id);
}
