package com.example.Exchange_rates_bot.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrencyDto {

    Integer id;
    Integer numCode;
    String charCode;
    Integer nominal;
    String name;
    Double value;
}
