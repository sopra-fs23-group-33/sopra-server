package ch.uzh.ifi.hase.soprafs23.constant;

import java.util.ArrayList;
import java.util.Random;

public enum Currency {
    USD,
    EUR,
    JPY,
    GBP,
    CNY,
    AUD,
    CAD,
    CHF,
    HKD,
    SGD,
    SEK,
    KRW,
    NOK,
    NZD,
    INR,
    MXN,
    TWD,
    ZAR,
    BRL,
    DKK;

    public static Currency getRandomCurrency(){
        Random random = new Random();
        Currency[] currencies = Currency.values();
        return currencies[random.nextInt(currencies.length)];
    }

}


