package ch.uzh.ifi.hase.soprafs23.Runner;

import ch.uzh.ifi.hase.soprafs23.Forex.ChartAPI;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.exceptions.ChartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

        for(int i = 0; i <= n+1; i++){
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
                currencyPair = this.generateCurrencyPair();
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
                Thread.sleep(2000);
                GameRound gameRound = api.getGameRound(currencyPair);
                asyncTransactionManager.addSingleGameRound(gameRound);
                return true;
            }
            catch (Exception e) {
                System.out.println(e.getMessage());
                return false;
            }

            catch (Error er) {
                System.out.println("chart failed");
                return false;
            }


        }
    }

}