package com.example.Exchange_rates_bot.mapper;

import com.example.Exchange_rates_bot.dto.CurrencyDto;
import com.example.Exchange_rates_bot.dto.Valute;
import com.example.Exchange_rates_bot.entity.Currency;
import jakarta.annotation.Generated;
import org.springframework.stereotype.Component;
@Generated(
        value = "org.mapstruct.ap.MappingProcessor",
        date = "2024-11-06T15:29:19+0300",
        comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.7 (Oracle Corporation)"
)
@Component
public class CurrencyMapperImpl implements CurrencyMapper{
    @Override
    public Currency convertToEntity(Valute valute) {
        if(valute == null) {
            return null;
        }
        Currency currency = new Currency();
        currency.setNumCode(valute.getNumCode());
        currency.setCharCode(valute.getCharCode());
        currency.setNominal(valute.getNominal());
        currency.setName(valute.getName());
        currency.setValue(Double.parseDouble(valute.getValue().replaceAll(",",".")));
        return currency;
    }

    @Override
    public CurrencyDto convertDto(Currency currency) {
        if(currency == null) {
            return  null;
        }
        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setId(currency.getId());
        currencyDto.setNumCode(currency.getNumCode());
        currencyDto.setCharCode(currency.getCharCode());
        currencyDto.setNominal(currency.getNominal());
        currencyDto.setName(currency.getName());
        currencyDto.setValue(currency.getValue());
        return currencyDto;
    }
}
