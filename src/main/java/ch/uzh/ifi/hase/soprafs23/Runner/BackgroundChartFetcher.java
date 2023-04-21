package ch.uzh.ifi.hase.soprafs23.Runner;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.ChartAPI;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.exceptions.ChartException;
import ch.uzh.ifi.hase.soprafs23.exceptions.NotFoundException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Component
public class BackgroundChartFetcher {
    private final AsyncTransactionManager asyncTransactionManager;


    @Autowired
    public BackgroundChartFetcher(AsyncTransactionManager asyncTransactionManager) {
        this.asyncTransactionManager = asyncTransactionManager;
    }

    @Async
    public void enqueue(int n)  {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        for(int i = 0; i <= n+2; i++){
                FetchAndStore fetchAndStore = new FetchAndStore();
                executorService.submit(fetchAndStore);
            }

        executorService.shutdown();
    }

    private class FetchAndStore implements Runnable {
        private final ChartAPI api = new ChartAPI();

        @Override
        public void run() {
            System.out.println("started");
            CurrencyPair currencyPair = this.generateCurrencyPair();

            boolean result = false;
            int count = 0;

            while (!result && count < 5) {
                result = this.fetch(currencyPair);
                count++;
                System.out.println(count);
            }
            System.out.println("ended");
        }

        private CurrencyPair generateCurrencyPair() {
            Currency to = Currency.getRandomCurrency();
            Currency from = Currency.getRandomCurrency();

            while (to.equals(from)) {
                from = Currency.getRandomCurrency();
            }

            return new CurrencyPair(to, from);
        }

        private boolean fetch(CurrencyPair currencyPair) {
            try {
                Thread.sleep(1200);
                GameRound gameRound = api.getGameRound(currencyPair);
                asyncTransactionManager.addSingleGameRound(gameRound);
                return true;
            }
            catch (InterruptedException e) {
                System.out.println(e.getMessage());
                return false;
            }

            catch (ChartException | NotFoundException e) {
                System.out.println("chart failed");
                return false;
            }


        }
    }

}