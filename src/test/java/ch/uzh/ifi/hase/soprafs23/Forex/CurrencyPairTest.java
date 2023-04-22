package ch.uzh.ifi.hase.soprafs23.Forex;

import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class CurrencyPairTest {

    private CurrencyPair currencyPair;

    @BeforeEach
    void setup(){
        this.currencyPair = new CurrencyPair(Currency.CHF, Currency.EUR);
    }

    @Test
    void getters(){
        assertEquals(Currency.CHF, currencyPair.getFrom());
        assertEquals(Currency.EUR, currencyPair.getTo());
    }

    @Test
    void equal(){
        CurrencyPair otherPair = new CurrencyPair(Currency.EUR, Currency.CHF);
        CurrencyPair thirdPair = new CurrencyPair(Currency.AUD, Currency.CNY);
        CurrencyPair fourthPair = new CurrencyPair(Currency.EUR, Currency.CHF);

        assertEquals(currencyPair, otherPair);
        assertEquals(currencyPair, currencyPair);
        assertNotEquals(currencyPair, thirdPair);
        assertNotEquals(null, currencyPair);
        assertNotEquals(Currency.CHF, currencyPair);
        assertEquals(currencyPair.hashCode(), otherPair.hashCode());
        assertNotEquals(currencyPair.hashCode(), thirdPair.hashCode());
        assertEquals(currencyPair, fourthPair);
    }
}
