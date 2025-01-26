package com.example.Exchange_rates_bot.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class TelegramBotConfiguration {
    @Value("${telegram.bot.username}")
    private String botUserName;
    @Value("${telegram.bot.token}")
    private String botToken;
    @Value("${telegram.bot.webhook-path}")
    private String webhookPath;
}
