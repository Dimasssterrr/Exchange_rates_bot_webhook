package com.example.Exchange_rates_bot.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "currency")
public class Currency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;
    @Column(name = "num_code")
    Integer numCode;
    @Column(name = "char_code")
    String charCode;
    @Column(name = "nominal")
    Integer nominal;
    @Column(name = "name")
    String name;
    @Column(name = "value")
    Double value;
}
