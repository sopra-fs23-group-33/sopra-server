package ch.uzh.ifi.hase.soprafs23.Forex;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
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

        assertTrue(currencyPair.equals(otherPair));
        assertTrue(currencyPair.equals(currencyPair));
        assertFalse(currencyPair.equals(thirdPair));
        assertFalse(currencyPair.equals(null));
        assertFalse(currencyPair.equals(new Instruction()));
        assertEquals(currencyPair.hashCode(), otherPair.hashCode());
        assertNotEquals(currencyPair.hashCode(), thirdPair.hashCode());
        assertTrue(currencyPair.equals(fourthPair));
    }
}
