package com.example.Exchange_rates_bot.parseCourseBanks;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class SaleAndPurchaseBanks {

    public String parseSalePurchase(String url, String currency, String emoji) {
        StringBuilder bankRatesOffer = new StringBuilder();
        try {
            Connection.Response response = getConnection(url);
            Elements elements = response.parse().getElementsByClass("Panel__sc-1g68tnu-0 cieTjP");
            Map<String, List<Double>> euroRatesBanks = new LinkedHashMap<>();
            for (Element element : elements) {
                String price = element.getElementsByClass("Text__sc-vycpdy-0 cQqMIr").text().replaceAll("₽", "").replaceAll(",", ".").trim();
                String[] prices = price.split("\\s+");
                List<Double> priceBank = new ArrayList<>();
                Arrays.stream(prices).toList().forEach(p -> {
                    if (!p.isEmpty()) {
                        priceBank.add(Double.parseDouble(p));
                    }
                });
                String nameBank = element.getElementsByClass("Text__sc-vycpdy-0 OiTuY").text();
                if (!nameBank.isEmpty()) {
                    euroRatesBanks.put(nameBank, priceBank);
                }
            }
            bankRatesOffer.append("Предложения банков по продаже и покупке ").append(currency).append(" на: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss "))).append(emoji).append("\n").append("Банк       - покупка / продажа : \n");
            euroRatesBanks.forEach((e1, e2) -> {
                if (!e2.isEmpty()) {
                    bankRatesOffer.append(e1).append(" - ").append(e2.get(0)).append(" руб.").append("/ ").append(e2.get(1)).append(" руб.").append("\n");
                }
            });
        } catch (IOException e) {
            log.error(e.toString());
            throw new RuntimeException(e);
        }
        return bankRatesOffer.toString();
    }
    public static void getLinks(String url) {
        try {
            Connection.Response response = getConnection(url);
            Document doc = response.parse();
            Elements elements = doc.select("a");
            for(Element el : elements) {
                String ur = el.attr("abs:href");
                if(ur.contains("page=2")) {
                    System.out.println(ur);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Connection.Response getConnection(String url) throws IOException {
        return Jsoup.connect(url)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                .timeout(40_000)
                .execute();
    }
}
