package ch.uzh.ifi.hase.soprafs23.constant;

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

    static Random random = new Random();

    public static Currency getRandomCurrency(){
        Currency[] currencies = Currency.values();
        return currencies[random.nextInt(currencies.length)];
    }

}


