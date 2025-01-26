package com.example.Exchange_rates_bot.bot.KeyBoard;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
@Component
public class KyeBoard {
    public InlineKeyboardMarkup addMainKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        ButtonName[] buttonNames = ButtonName.values();
        String[] names = {"Все валюты", "Курс USD", "Курс EUR", "Конвертировать", "Подписаться на курс", "Подписаться на ежедневную рассылку курсов"};
        for (int k = 0; k < 4; k++) {
            List<InlineKeyboardButton> buttons = new ArrayList<>();
            for (int i = 0; i < ButtonName.values().length; i++) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(names[i]);
                button.setCallbackData(buttonNames[i].name());
                if (k == 0 && i == 0 ) {
                    buttons.add(button);
                }
                if(k == 1 && i > 0 && i < 3) {
                    buttons.add(button);
                }
                if(k == 2 && i == 3) {
                    buttons.add(button);
                }
                if(k == 3 && i > 3 && i < ButtonName.values().length) {
                    buttons.add(button);
                }
            }
            rowsInLine.add(buttons);
        }
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
       return inlineKeyboardMarkup;
    }
    public InlineKeyboardMarkup addKeyBoard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        ButtonNameBank[] buttonNamesBank = ButtonNameBank.values();
        String[] names = {"USD","EUR","CNY"};
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for (int i = 0; i < ButtonNameBank.values().length; i++) {
            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(names[i]);
            button.setCallbackData(buttonNamesBank[i].name());
            buttons.add(button);
        }
        rowsInLine.add(buttons);
        inlineKeyboardMarkup.setKeyboard(rowsInLine);
        return inlineKeyboardMarkup;
    }
}

