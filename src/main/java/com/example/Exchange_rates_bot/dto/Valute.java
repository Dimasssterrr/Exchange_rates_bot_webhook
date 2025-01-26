package com.example.Exchange_rates_bot.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import lombok.Getter;
import lombok.Setter;



@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Valute {
    @XmlElement(name = "NumCode")
    Integer numCode;
    @XmlElement(name = "CharCode")
    String charCode;
    @XmlElement(name = "Nominal")
    Integer nominal;
    @XmlElement(name = "Name")
    String name;
    @XmlElement(name = "Value")
    String value;
    @XmlElement(name = "VunitRate")
    String vunitRate;
}
