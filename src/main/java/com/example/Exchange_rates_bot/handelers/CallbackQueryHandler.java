package com.example.Exchange_rates_bot.handelers;

import com.example.Exchange_rates_bot.Service.CurrencyService;
import com.example.Exchange_rates_bot.bot.KeyBoard.ButtonName;
import com.example.Exchange_rates_bot.bot.KeyBoard.ButtonNameBank;
import com.example.Exchange_rates_bot.parseCourseBanks.SaleAndPurchaseBanks;
import com.example.Exchange_rates_bot.repository.CurrencyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class CallbackQueryHandler {

    private final SaleAndPurchaseBanks saleAndPurchaseBanks;
    private final CurrencyService service;
    private final CurrencyRepository currencyRepository;

    public CallbackQueryHandler(SaleAndPurchaseBanks saleAndPurchaseBanks, CurrencyService service, CurrencyRepository currencyRepository) {
        this.saleAndPurchaseBanks = saleAndPurchaseBanks;
        this.service = service;
        this.currencyRepository = currencyRepository;
    }

    public BotApiMethod<?> processCallbackQuery(CallbackQuery callbackQuery) {
        if(callbackQuery.getData().equals(String.valueOf(ButtonName.СПИСОК))) {
          return handelButtonClickAllCurrency(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonName.ДОЛАР))) {
            return handleButtonClickCurrencyUSD(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonName.ЕВРО))) {
            return handelButtonClickCurrencyEUR(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonName.КОНВЕРТИРОВАТЬ))) {
            return handelButtonClickConvert(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonName.ПОДПИСАТЬСЯ))) {
            return handelButtonSubscribeRate(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonName.РАССЫЛКА))) {
            return handelButtonSubscribeDaily(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonNameBank.ДОЛАР_БАНКИ))) {
            return handelButtonUSD(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonNameBank.ЕВРО_БАНКИ))) {
            return handelButtonEUR(callbackQuery.getFrom().getId());
        }
        if(callbackQuery.getData().equals(String.valueOf(ButtonNameBank.ЮАНЬ_БАНКИ))) {
            return handelButtonCYN(callbackQuery.getFrom().getId());
        }
        return null;
    }
    public SendMessage handelButtonClickAllCurrency(Long chatId) {
        StringBuilder currency = new StringBuilder();
        try {
            service.getAllCurrency().forEach(currencyDto -> {
                if (!currencyDto.getName().contains("СДР")) {
                    currency.append("за ").append(currencyDto.getNominal()).append(" ").append(currencyDto.getName()).append("/").append(currencyDto.getCharCode()).append("/").append("<b>").append(currencyDto.getValue()).append(" руб.").append("</b>").append(" \n");
                }
            });
        } catch (Exception e) {
            log.error(e.toString());
        }
        return executeAnswer(chatId, "&#x1F30D <b> Официальный курс валют Центробака на " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")) + "</b> \n" + currency);
    }
    public SendMessage handleButtonClickCurrencyUSD(Long chatId) {
        return executeAnswer(chatId, "&#128176;&#128178; Курс доллара на " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss \n")) + "<b>" + currencyRepository.findByCharCode("USD").getValue() + " руб.</b>");
    }
    public SendMessage handelButtonClickCurrencyEUR(Long chatId) {
       return executeAnswer(chatId, "&#8364; Курс евро на " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss \n")) + "<b>" + currencyRepository.findByCharCode("EUR").getValue() + " руб.</b>");
    }
    public SendMessage handelButtonClickConvert(Long chatId) {
        return executeAnswer(chatId, "Напишите &#9997 мне <b>/convert</b> и сумму которую хотите конвертировать в рубли, затем укажите трехбуквенный код валюты. Пример: /convert 100 USD");
    }
    public SendMessage handelButtonSubscribeRate(Long chatId) {
        return executeAnswer(chatId, "Для подписки на курс определенной валюты, напишите мне /subscribe курс на который хотите подписаться и трехбуквенный код валюты. Пример: /subscribe 100 USD");
    }
    public SendMessage handelButtonSubscribeDaily(Long chatId) {
        return executeAnswer(chatId, "Для подписки на ежедневную рассылку курсов валют, напишите мне ниже /receive_cost [трехбуквенный код].(Пример: /receive_cost USD). Подписаться можно как на одну валюту, так и на весь список, который предлагается ЦБ.");
    }

    public SendMessage handelButtonUSD(Long chatId) {
        return executeAnswer(chatId, saleAndPurchaseBanks.parseSalePurchase("https://www.banki.ru/products/currency/cash/usd/moskva/", "USD", "&#128181;"));
    }

    public SendMessage handelButtonEUR(Long chatId) {
        return executeAnswer(chatId, saleAndPurchaseBanks.parseSalePurchase("https://www.banki.ru/products/currency/cash/eur/moskva/", "EUR", "&#128182;"));
    }

    public SendMessage handelButtonCYN(Long chatId) {
        return executeAnswer(chatId, saleAndPurchaseBanks.parseSalePurchase("https://www.banki.ru/products/currency/cash/cny/moskva/", "CNY", "&#128180;"));
    }
    public SendMessage executeAnswer(Long id, String text) {
        SendMessage answer = new SendMessage();
        answer.setChatId(id);
        answer.setText(text);
        answer.enableHtml(true);
        return answer;
    }
}
