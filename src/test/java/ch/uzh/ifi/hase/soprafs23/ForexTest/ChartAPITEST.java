package ch.uzh.ifi.hase.soprafs23.ForexTest;

import ch.uzh.ifi.hase.soprafs23.Forex.ChartAPI;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.exceptions.ChartException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChartAPITEST {

    @Test
    void fetch_chart() throws ChartException {
        ChartAPI api = new ChartAPI();

        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

        GameRound gameRound = api.getGameRound(currencyPair);
        assertEquals(gameRound.getFirstChart().getDates().size(), gameRound.getFirstChart().getValues().size());
        assertTrue(gameRound.getFirstChart().getValues().size() > 0);
        assertTrue(gameRound.getFirstChart().getDates().size() > 0);
    }
}
