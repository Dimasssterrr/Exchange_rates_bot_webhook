package com.example.Exchange_rates_bot.mapper;

import com.example.Exchange_rates_bot.dto.CurrencyDto;
import com.example.Exchange_rates_bot.dto.Valute;
import com.example.Exchange_rates_bot.entity.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    Currency convertToEntity(Valute valute);
    CurrencyDto convertDto(Currency currency);

}
