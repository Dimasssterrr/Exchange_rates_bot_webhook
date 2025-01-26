package com.example.Exchange_rates_bot.handlerXML;

import com.example.Exchange_rates_bot.dto.ValCurs;
import com.example.Exchange_rates_bot.dto.Valute;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

@Component
public class HandlerXML {
    @Value("${centralBank.url}")
    private String url;

    public List<Valute> getValuteList() throws JAXBException, MalformedURLException {
        JAXBContext context = JAXBContext.newInstance(ValCurs.class);
        Unmarshaller un = context.createUnmarshaller();
        ValCurs valute = (ValCurs) un.unmarshal(new URL(url));
        return valute.getValutes();
    }
}
