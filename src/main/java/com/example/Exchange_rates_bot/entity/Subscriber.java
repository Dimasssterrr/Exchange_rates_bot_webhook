package com.example.Exchange_rates_bot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "subscriber")
public class Subscriber {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "telegram_id")
    private Long telegramId;
    @Column(name = "name")
    private String name;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Subscription> subscriptions;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Newsletter> newsletters;
}
