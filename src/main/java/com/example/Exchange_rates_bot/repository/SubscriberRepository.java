package com.example.Exchange_rates_bot.repository;

import com.example.Exchange_rates_bot.entity.Subscriber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriberRepository extends JpaRepository<Subscriber, Long> {
    Subscriber findByTelegramId(Long id);
}
