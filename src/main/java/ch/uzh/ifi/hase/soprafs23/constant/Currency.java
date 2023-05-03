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
    NOK,
    NZD,
    MXN,
    ZAR,
    BRL,
    DKK;

    static final Random random = new Random();

    public static Currency getRandomCurrency(){
        Currency[] currencies = Currency.values();
        return currencies[random.nextInt(currencies.length)];
    }

}


