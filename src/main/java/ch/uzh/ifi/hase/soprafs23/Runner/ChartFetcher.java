package ch.uzh.ifi.hase.soprafs23.Runner;

import ch.uzh.ifi.hase.soprafs23.Forex.ChartAPI;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.Game.CorruptedState;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;



@Component
public class ChartFetcher {
    private final GameRoundRepository gameRoundRepository;
    private final GameRepository gameRepository;

    private final AsyncTransactionManager asyncTransactionManager;
    @Autowired
    public ChartFetcher(@Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository,@Qualifier("gameRepository") GameRepository gameRepository, AsyncTransactionManager asyncTransactionManager) {
        this.gameRoundRepository = gameRoundRepository;
        this.gameRepository = gameRepository;
        this.asyncTransactionManager = asyncTransactionManager;
    }


    @Async
    public void fetch(Long gameID) {

        int rounds;
        HashSet<CurrencyPair> currencyPairs;

        try {
            rounds = asyncTransactionManager.getRounds(gameID);
        }
        catch (NotFoundException e1) {
            return;
        }

        if (rounds < 1)
            return;
        else if (rounds >= 1 && rounds < Currency.values().length * Currency.values().length / 8)
            currencyPairs = this.generateCurrencyPairs(rounds);
        else
            return;

        if (currencyPairs.size() != rounds)
            return;

        ArrayList<GameRound> gameRounds = new ArrayList<>();
        ArrayList<CurrencyPair> currencyPairsList = new ArrayList<>(currencyPairs);
        CurrencyPair firstPair = currencyPairsList.remove(0);


        try {
            GameRound firstRound = this.fetch(firstPair, 2, true);
            this.asyncTransactionManager.addGameRound(firstRound, gameID);
        }
        catch (NotFoundException | ChartException e2) {
            return;
        }

        for (CurrencyPair currencyPair : currencyPairsList) {
            try {
                GameRound newRound = this.fetch(currencyPair, 5, true);
                this.asyncTransactionManager.addGameRound(newRound, gameID);
            }
            catch (NotFoundException | ChartException e4) {
                return;
            }

        }

    }

    private HashSet<CurrencyPair> generateCurrencyPairs(int rounds) {
        HashSet<CurrencyPair> currencyPairs = new HashSet<>();

        int count = 0;

        while ((currencyPairs.size() != rounds) && count < 100) {
            count++;

            Currency to = Currency.getRandomCurrency();
            Currency from = Currency.getRandomCurrency();

            if (to.equals(from))
                continue;
            else {
                currencyPairs.add(new CurrencyPair(to, from));
            }
        }
        return currencyPairs;
    }



    private GameRound fetch(CurrencyPair currencyPair, int trials, boolean delay) throws ChartException {
        ChartAPI api = new ChartAPI();
        int count = 0;

        while (count <= trials) {
            count++;
            try {
                if(delay)
                    Thread.sleep(2000);
                return api.getGameRound(currencyPair);
            }
            catch (Exception ignored) {
            }
        }

        throw new ChartException();
    }
}

