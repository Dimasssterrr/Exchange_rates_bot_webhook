package com.example.Exchange_rates_bot.Service;

import com.example.Exchange_rates_bot.dto.CurrencyDto;
import com.example.Exchange_rates_bot.entity.Currency;
import com.example.Exchange_rates_bot.handlerXML.HandlerXML;
import com.example.Exchange_rates_bot.mapper.CurrencyMapper;
import com.example.Exchange_rates_bot.repository.CurrencyRepository;
import jakarta.xml.bind.JAXBException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class CurrencyService {
    private final HandlerXML handlerXML;
    private final CurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final SubscriberService subscriberService;

    public List<CurrencyDto> createCurrency() throws MalformedURLException, JAXBException {
        List<CurrencyDto> dtoList = new ArrayList<>();
       handlerXML.getValuteList().forEach(valute -> {
          dtoList.add(currencyMapper.convertDto(currencyRepository.save(currencyMapper.convertToEntity(valute))));
       });
        return dtoList;
    }
    public List<CurrencyDto> getAllCurrency() {
        List<CurrencyDto> currencyDtoList = new ArrayList<>();
        currencyRepository.findAll().forEach(currency -> {
            currencyDtoList.add(currencyMapper.convertDto(currency));
        });
        return currencyDtoList;
    }

    public Double convertCurrency(Message message) {
        String val = message.getText().replaceAll("[^0-9]","").replaceAll(",",".");
        String charCode = subscriberService.getCharCode(message);
        if(val.isEmpty()) {
            return null;
        }
        double value = Double.parseDouble(val);
        Currency currency = currencyRepository.findByCharCode(charCode);
        if(currency == null) {
            return null;
        }
        return new BigDecimal(currency.getValue() / currency.getNominal() * value)
                .setScale(2,RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Scheduled(fixedDelayString = "${app.rate update frequency}")
    public void updateCurrencyDb() {
        try {
            currencyRepository.truncate();
            createCurrency();
            log.info("Записи в Db обновлены " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }
}
