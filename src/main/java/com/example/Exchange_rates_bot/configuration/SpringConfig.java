package com.example.Exchange_rates_bot.configuration;

import com.example.Exchange_rates_bot.bot.ExchangeRatesBot;
import com.example.Exchange_rates_bot.handelers.CallbackQueryHandler;
import com.example.Exchange_rates_bot.handelers.MessageHandler;
import com.example.Exchange_rates_bot.repository.CurrencyRepository;
import com.example.Exchange_rates_bot.repository.NewsLetterRepository;
import com.example.Exchange_rates_bot.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;

@Configuration
@RequiredArgsConstructor
public class SpringConfig {

     private final TelegramBotConfiguration telegramBotConfiguration;

     @Bean
     public SetWebhook setWebhookInstance() {
         return SetWebhook.builder().url(telegramBotConfiguration.getWebhookPath()).build();
     }
     @Bean
     public ExchangeRatesBot webHookBot(SetWebhook setWebhook,
                                        CallbackQueryHandler callbackQueryHandler,
                                        MessageHandler messageHandler,
                                        SubscriberRepository subscriberRepository,
                                        NewsLetterRepository newsLetterRepository,
                                        CurrencyRepository currencyRepository) {

        ExchangeRatesBot exchangeRatesBot = new ExchangeRatesBot(setWebhook, messageHandler, callbackQueryHandler, subscriberRepository, newsLetterRepository, currencyRepository);
        exchangeRatesBot.setBotToken(telegramBotConfiguration.getBotToken());
        exchangeRatesBot.setBotUserName(telegramBotConfiguration.getBotUserName());
        exchangeRatesBot.setWebhookPath(telegramBotConfiguration.getWebhookPath());
        return exchangeRatesBot;
     }
}
