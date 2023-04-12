package ch.uzh.ifi.hase.soprafs23.ForexTest;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameRoundTest {

    private ArrayList<Double> numbers = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();

    private CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

    @BeforeEach
    void setup(){
        for(int i = 0; i < 999; i++){
            numbers.add(1.0);
            dates.add("Date" + i);
        }
    }

    @Test
    void positive_ratio(){
        numbers.add(2.0);
        dates.add("Date999");

        Chart chart = new Chart(numbers, dates, currencyPair);

        GameRound gameRound = new GameRound(chart);

        assertEquals(2.0, gameRound.getRatio());
        assertEquals(1000, gameRound.getSecondChart().getValues().size());
        assertEquals(1000, gameRound.getSecondChart().getDates().size());
    }

    @Test
    void negative_ratio(){
        numbers.add(0.5);
        dates.add("Date999");

        Chart chart = new Chart(numbers, dates, currencyPair);

        GameRound gameRound = new GameRound(chart);

        assertEquals(2.0, gameRound.getRatio());
        assertEquals(Direction.DOWN, gameRound.getOutcome());
        assertEquals(1000, gameRound.getSecondChart().getValues().size());
        assertEquals(1000, gameRound.getSecondChart().getDates().size());
    }

    @Test
    void one_ratio(){
        numbers.add(1.0);
        dates.add("Date999");

        Chart chart = new Chart(numbers, dates, currencyPair);

        GameRound gameRound = new GameRound(chart);

        assertEquals(1.0, gameRound.getRatio());
        assertEquals(Direction.UP, gameRound.getOutcome());
        assertEquals(1000, gameRound.getSecondChart().getValues().size());
        assertEquals(1000, gameRound.getSecondChart().getDates().size());
    }

    @Test
    void test_large_split(){
        numbers.add(2.0);
        dates.add("Date999");

        Chart chart = new Chart(numbers, dates, currencyPair);

        GameRound gameRound = new GameRound(chart);

        assertEquals(2.0, gameRound.getRatio());
        assertEquals(Direction.UP, gameRound.getOutcome());
        assertEquals(1000, gameRound.getSecondChart().getValues().size());
        assertEquals(1000, gameRound.getSecondChart().getDates().size());

        assertEquals(1000-289, gameRound.getFirstChart().getValues().size());
        assertEquals(1000-289, gameRound.getFirstChart().getDates().size());
    }

    @Test
    void test_small_split(){


        Chart chart = new Chart(new ArrayList<>(numbers.subList(0,100)), new ArrayList<>(dates.subList(0,100)), currencyPair);

        GameRound gameRound = new GameRound(chart);

        assertEquals(1.0, gameRound.getRatio());
        assertEquals(Direction.UP, gameRound.getOutcome());
        assertEquals(100, gameRound.getSecondChart().getValues().size());
        assertEquals(100, gameRound.getSecondChart().getDates().size());

        assertEquals(49, gameRound.getFirstChart().getValues().size());
        assertEquals(49, gameRound.getFirstChart().getDates().size());
    }
}
