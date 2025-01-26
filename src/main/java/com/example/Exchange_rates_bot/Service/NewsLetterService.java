package com.example.Exchange_rates_bot.Service;

import com.example.Exchange_rates_bot.entity.Newsletter;
import com.example.Exchange_rates_bot.entity.Subscriber;
import com.example.Exchange_rates_bot.repository.NewsLetterRepository;
import com.example.Exchange_rates_bot.repository.SubscriberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
@RequiredArgsConstructor
public class NewsLetterService {
    private final SubscriberRepository subscriberRepository;
    private final SubscriberService subscriberService;
    private final NewsLetterRepository newsLetterRepository;
    public static AtomicBoolean isCreate = new AtomicBoolean(false);

    public void createNewsletter(Message message) {
        isCreate.set(false);
        Subscriber subscriber = subscriberRepository.findByTelegramId(message.getFrom().getId());
        String charCode = subscriberService.getCharCode(message);
        if(!subscriberService.checkCharCode(message)) {
            return;
        }
        Newsletter newsletter = new Newsletter();
        newsletter.setSubscriber(subscriber);
        newsletter.setCharCode(charCode);
        if(newsLetterRepository.count() == 0) {
            isCreate.set(true);
            newsLetterRepository.save(newsletter);
        }
        Newsletter newsletterDb = newsLetterRepository.findByCharCodeAndSubscriberId(charCode,subscriber.getId());
        if(newsletterDb == null) {
            isCreate.set(true);
            newsLetterRepository.save(newsletter);
        }
    }
    public Newsletter cancelNewsLatter(Message message) {
        String charCode = subscriberService.getCharCode(message);
        Subscriber subscriber = subscriberRepository.findByTelegramId(message.getFrom().getId());
        Newsletter newsletter = newsLetterRepository.findByCharCodeAndSubscriberId(charCode,subscriber.getId());
        if(newsletter == null) {
            return null;
        }
        newsLetterRepository.delete(newsletter);
        return newsletter;
    }
    public List<Newsletter> cancelAllNewsLatter(Message message) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(message.getFrom().getId());
       List<Newsletter> newsletters = newsLetterRepository.findAllBySubscriberId(subscriber.getId());
       if(newsletters != null) {
           newsLetterRepository.deleteAllFindBySubscriberId(subscriber.getId());
       }
       return newsletters;
    }
    public List<Newsletter> getDailyMailings(Message message) {
        Subscriber subscriber = subscriberRepository.findByTelegramId(message.getFrom().getId());
        return newsLetterRepository.findAllBySubscriberId(subscriber.getId());
    }
}
