package com.example.Exchange_rates_bot.handelers;

import com.example.Exchange_rates_bot.Service.CurrencyService;
import com.example.Exchange_rates_bot.Service.NewsLetterService;
import com.example.Exchange_rates_bot.Service.SubscriberService;
import com.example.Exchange_rates_bot.bot.KeyBoard.KyeBoard;
import com.example.Exchange_rates_bot.entity.Currency;
import com.example.Exchange_rates_bot.entity.Newsletter;
import com.example.Exchange_rates_bot.entity.Subscriber;
import com.example.Exchange_rates_bot.entity.Subscription;
import com.example.Exchange_rates_bot.repository.CurrencyRepository;
import com.example.Exchange_rates_bot.repository.NewsLetterRepository;
import com.example.Exchange_rates_bot.repository.SubscriberRepository;
import com.example.Exchange_rates_bot.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageHandler {
    private final SubscriberService subscriberService;
    private final KyeBoard kyeBoard;
    private final CurrencyService currencyService;
    private final NewsLetterService newsLetterService;

    private final SubscriberRepository subscriberRepository;
    private final NewsLetterRepository newsLetterRepository;
    private final CurrencyRepository currencyRepository;
    private final SubscriptionRepository subscriptionRepository;

    @Value("${telegram.bot.token}")
    private String BOT_TOKEN;

    public BotApiMethod<?> answerMessage(Message message) {
        String chatId = message.getChatId().toString();
//        unpinMessage(message.getChatId().toString(), message.getMessageId().toString());
        if (message.hasPhoto()) {
            String fileId = "";
            List<PhotoSize> photos = message.getPhoto();
            for (PhotoSize photo : photos) {
                fileId = photo.getFileId();
            }
            return answer(chatId, fileId);
        }
        if (message.getText() == null) {
            log.error("Error method answerMessage: " + message.getFrom().getId() + " getText = null");
            throw new IllegalArgumentException();
        }
        String text = message.getText();

        if (text.equals("/start")) {
            return startCommand(chatId, message);
        }
        if (text.equals("/help")) {
            return answer(chatId, readText("/root/description.txt"));
        }
        if (text.contains("/subscribe")) {
            return subscribeCommand(chatId, message);
        }
        if (text.equals("/get_subscription")) {
            return getSubscriptionCommand(chatId, message);
        }
        if (text.contains("/convert")) {
            return convertCurrencyCommand(chatId, message);
        }
        if (text.contains("/cancel_subscription")) {
            return cancelSubscriptionCommand(chatId, message);
        }
        if (text.contains("/receive_cost")) {
            return receiveCostEveryDayCommand(chatId, message);
        }
        if (text.equals("/get_daily")) {
            return getDailyMailingsCommand(chatId, message);
        }
        if (text.contains("/cancel_receive")) {
            return cancelReceiveCostEveryDayCommand(chatId, message);
        }
        if (text.equals("/banks")) {
            return getExchangeRatesBanksCommand(chatId, message);
        }
        return answer(chatId, "Неверная команда. Попробуйте еще раз &#x1F609! &#127384; Или воспользуйтесь /help &#128071;");
    }

    public MessageEntity sendPhoto(String chatId, String fileId) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendPhoto";
        String json = String.format("{\"chat_id\": \"%s\", \"photo\": \"%s\"}", chatId, fileId);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url, entity, String.class);
        MessageEntity message = new MessageEntity();
        message.setText("");
        return message;
    }
    public SendMessage startCommand(String chatId, Message message) {
        Subscriber subscriber = subscriberService.createSubscriber(message);
        if(subscriber != null) {
            sendMessage("1468907315", "Добавился новый пользователь - " + subscriber.getName());
        }
        MessageEntity messagePhoto = sendPhoto(chatId,"AgACAgIAAxkBAAIJ0GeBNJdTL7ki3yOD7vnzXlPp28xsAAL55TEbMscISPiAEfKjyYvPAQADAgADeQADNgQ");
        SendMessage messageAnswer = answer(chatId,happyNewYear(LocalDate.now()) + readText("/root/text.txt"));
        List<MessageEntity> messages = new ArrayList<>();
        messages.add(messagePhoto);
        messageAnswer.setReplyMarkup(kyeBoard.addMainKeyBoard());
        messageAnswer.setEntities(messages);
        return messageAnswer;
    }
    public SendMessage subscribeCommand(String chatId, Message message) {
        String txt = "";
        Subscription subscription = subscriberService.subscribeCourse(message);
        txt = subscriberService.checkCharCode(message) ? "Отлично! Вы подписаны на " + subscription.getCharCode() + ", стоимость <b> " + subscription.getPrice() + "</b> руб. &#128065;" : "Не верно указан трехбуквенный код,такой валюты не существует &#129335. Попробуйте еще раз &#x1F609;!";
        return answer(chatId,txt);
    }
    public SendMessage getSubscriptionCommand(String chatId, Message message) {
        StringBuilder sub = new StringBuilder();
        List<Subscription> subscriptions = subscriberService.getAllSubscription(message.getFrom().getId());
        if (subscriptions.isEmpty()) {
            return answer(chatId, "У Вас нет подписок &#129335");
        }
        subscriptions.forEach(subscription -> {
            sub.append("\n").append(subscription.getCharCode()).append(" - ").append(subscription.getPrice()).append(" руб.");
        });
        return answer(chatId, sub.toString());
    }
    public SendMessage convertCurrencyCommand(String chatId, Message message) {
        String text = "";
        if (subscriberService.getCharCode(message).isEmpty()) {
            text = "&#10060; Вы не указали трехбуквенный код валюты! &#129335. Попробуйте еще раз &#x1F609;!";
            return answer(chatId, text);
        }
        if (!subscriberService.checkCharCode(message)) {
            text = "&#10060; Не верно указан трехбуквенный код,такой валюты не существует. Или валюта не указана &#129335. Попробуйте еще раз &#x1F609;!";
            return answer(chatId, text);
        }

        Double value = currencyService.convertCurrency(message);
        text = value == null ? "&#10060; Не указана сумма которую Вы хотите конвертировать.Попробуйте снова &#x1F609" : "Отлично! Конвертируем...";
        if (value != null) {
            return answer(chatId, text + "\n" + "Получается <b> " + value + " руб. </b>");
        }
        return  answer(chatId, text);
    }
    public SendMessage cancelSubscriptionCommand(String chatId,Message message) {
        System.out.println("no");
        Subscription subscription = subscriberService.cancelSubscription(message);
        String text = "";
        if(message.getText().toLowerCase().contains("all")) {
            List<Subscription> subscriptions = subscriberService.cancelAllSubscriptions(message);
            text = subscriptions.isEmpty() ? "У Вас отсутствуют активные подписки!" : "Все подписки отменены!";
            return answer(chatId,text);
        }
        text = subscription == null? "У Вас нет подписки, или не указана валюта &#129335;. Попробуйте еще раз &#128521;!":"Подписка на " + subscription.getCharCode() + " отменена!";
         return answer(chatId, text);
    }
    public SendMessage receiveCostEveryDayCommand(String chatId, Message message) {
        newsLetterService.createNewsletter(message);
        String text = "";
        if(subscriberService.checkCharCode(message)) {
            text =   NewsLetterService.isCreate.get()?
                    "Теперь Вам ежедневно будут приходить курсы валют, которые Вы указали) &#128065;" : "Такая подписка уже существует!";
        } else {
            text = "Не верно указан трехбуквенный код,такой валюты не существует &#129335. Попробуйте еще раз &#x1F609;!";
        }
        return answer(chatId, text);
    }
    public SendMessage getDailyMailingsCommand(String chatId, Message message) {
        List<Newsletter> newsletters = newsLetterService.getDailyMailings(message);
        String text = "";
        if (newsletters.isEmpty()) {
            text = "У Вас нет подписок на ежедневную рассылку курсов! &#128181;&#128183;";
        } else {
            StringBuilder list = new StringBuilder();
            list.append("Валюты на которые будет приходить ежедневная рассылка:");
            newsletters.forEach(newsletter -> {
                list.append("\n").append(newsletter.getCharCode());
            });
            text = list.toString();
        }
        return answer(chatId, text);
    }
    public SendMessage cancelReceiveCostEveryDayCommand(String chatId, Message message){
        String text = "";
        System.out.println("yes");
        if (message.getText().toLowerCase().contains("all")) {
            List<Newsletter> newsletters = newsLetterService.cancelAllNewsLatter(message);
            text = newsletters.isEmpty() ? "Отсутствуют активные подписки, на рассылку курсов валют!" : "Отменены все подписки на рассылку курсов валют!";
        } else {
            Newsletter newsletter = newsLetterService.cancelNewsLatter(message);
            text = newsletter == null ? "У Вас нет подписки, или не указана валюта &#129335;. Попробуйте еще раз &#128521;!" : "Отменена подписка на рассылку курса " + newsletter.getCharCode();
        }
        return answer(chatId, text);
    }
    public SendMessage getExchangeRatesBanksCommand(String chatId, Message message) {
        SendMessage mes = answer(chatId, "Предложения банков Москвы:");
        mes.setReplyMarkup(kyeBoard.addKeyBoard());
        return mes;
    }

    public SendMessage answer(String chatId, String text) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.enableHtml(true);
        answer.setText(text);
        return answer;
    }

    public String happyNewYear(LocalDate date) {
        if(date.getMonth().getValue() == 12 && date.getDayOfMonth() > 15 || date.getMonth().getValue() == 1 && date.getDayOfMonth() < 20) {
            return "&#127876; &#127876; &#127876;";
        }
        return "";
    }

    public static String readText(String way) {
        Path path = Paths.get(way);
        try {
            return Files.readString(path);
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
    }
    public void unpinMessage(String chatId, String messageId) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/unpinAllChatMessages";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(url);
            JSONObject json = new JSONObject();
            json.put("chat_id", chatId);
//            json.put("message_id", messageId);
            json.put("disable_notification", true);
            post.setEntity(new StringEntity(json.toString()));
            post.setHeader("Content-Type", "application/json");
            HttpResponse response = httpClient.execute(post);
//            HttpEntity responseEntity = response.getEntity();
//            if (responseEntity != null) {
//                String responseString = EntityUtils.toString(responseEntity);
//                System.out.println("Pin response: " + responseString);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void sendMessage(String chatId, String text) {
        String url = "https://api.telegram.org/bot" + BOT_TOKEN + "/sendMessage";
        String json = String.format("{\"chat_id\": \"%s\", \"text\": \"%s\"}", chatId, text);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(json, headers);
        restTemplate.postForObject(url, entity, String.class);
    }

    @Scheduled(cron = "${app.mailing time}")
    @Async
    public void receiveCostEveryDay() {
        List<Subscriber> subscribers = subscriberRepository.findAll();
        subscribers.forEach(subscriber -> {
            StringBuilder textAnswerCurrencyCost = new StringBuilder();
            textAnswerCurrencyCost.append("Курсы валют \uD83D\uDCCA \uD83D\uDCB5 \uD83D\uDCB4 \uD83D\uDCB6 на ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"))).append(": \n");
            List<Newsletter> newsletters = newsLetterRepository.findAllBySubscriberId(subscriber.getId());
            if (newsletters.isEmpty()) {
                return;
            }
            newsletters.forEach(subscription -> {
                Currency currency = currencyRepository.findByCharCode(subscription.getCharCode());
                textAnswerCurrencyCost.append(currency.getCharCode()).append(" - ").append(currency.getValue()).append(" руб. \n");
            });
            sendMessage(subscriber.getTelegramId().toString(), textAnswerCurrencyCost.toString());
        });
    }
    @Scheduled(cron = "${app.notification time}")
    @Async
    public void timeBuyMessage () {
        List<Subscription> subscriptions = subscriptionRepository.findAll();
        List<Currency> currencies = currencyRepository.findAll();
        subscriptions.forEach(subscription -> {
            currencies.forEach(currency -> {
                if(subscription.getCharCode().equals(currency.getCharCode()) && subscription.getPrice() > currency.getValue()) {
                    Subscriber subscriber = subscriberRepository.findById(subscription.getSubscriber().getId()).orElseThrow();
                   sendMessage(subscriber.getTelegramId().toString(),"Пора покупать! \uD83D\uDC49 \uD83D\uDCAF ❗ \n" + currency.getName() + " стоит  " + currency.getValue() + " руб.");
                }
            });
        });
    }
}

