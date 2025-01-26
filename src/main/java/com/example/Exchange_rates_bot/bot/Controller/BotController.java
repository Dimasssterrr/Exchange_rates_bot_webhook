package com.example.Exchange_rates_bot.bot.Controller;

import com.example.Exchange_rates_bot.bot.ExchangeRatesBot;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class BotController {

    private final ExchangeRatesBot exchangeRatesBot;

    @PostMapping("/webhook")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return exchangeRatesBot.onWebhookUpdateReceived(update);
    }
    @GetMapping("/get")
    public ResponseEntity<String> get() {
        return ResponseEntity.ok("Yes");
    }
}
