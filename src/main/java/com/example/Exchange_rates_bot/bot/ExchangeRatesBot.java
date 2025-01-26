package com.example.Exchange_rates_bot.bot;

import com.example.Exchange_rates_bot.handelers.CallbackQueryHandler;
import com.example.Exchange_rates_bot.handelers.MessageHandler;
import com.example.Exchange_rates_bot.repository.CurrencyRepository;
import com.example.Exchange_rates_bot.repository.NewsLetterRepository;
import com.example.Exchange_rates_bot.repository.SubscriberRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Getter
@Setter
@Service
@Slf4j
public class ExchangeRatesBot extends SpringWebhookBot {
    private String webhookPath;
    private String botUserName;
    private String botToken;

    private final MessageHandler messageHandler;
    private final CallbackQueryHandler callbackQueryHandler;
    private final SubscriberRepository subscriberRepository;
    private final NewsLetterRepository newsLetterRepository;
    private final CurrencyRepository currencyRepository;


    public ExchangeRatesBot(SetWebhook setWebhook, MessageHandler messageHandler, CallbackQueryHandler callbackQueryHandler, SubscriberRepository subscriberRepository, NewsLetterRepository newsLetterRepository, CurrencyRepository currencyRepository) {
        super(setWebhook);
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.subscriberRepository = subscriberRepository;
        this.newsLetterRepository = newsLetterRepository;
        this.currencyRepository = currencyRepository;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        return handelUpdate(update);
    }

    public BotApiMethod<?> handelUpdate (Update update)  {
        if(update.hasCallbackQuery()) {
            return callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());
        }
        if(update.hasMessage()) {
            return messageHandler.answerMessage(update.getMessage());
        }
        return null;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    @Override
    public String getBotUsername() {
        return null;
    }

    @Override
    public void onRegister() {
        super.onRegister();
    }
}
