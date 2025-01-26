package com.example.Exchange_rates_bot.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.Setter;


import java.util.List;
@Getter
@Setter
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValCurs {
    @XmlAttribute(name = "Date")
    private String date;
    @XmlElement(name = "Valute")
    private List<Valute> valutes;
}
