package ch.uzh.ifi.hase.soprafs23.Forex;


import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import com.crazzyghost.alphavantage.AlphaVantage;
import com.crazzyghost.alphavantage.Config;
import com.crazzyghost.alphavantage.forex.response.ForexResponse;
import com.crazzyghost.alphavantage.forex.response.ForexUnit;
import com.crazzyghost.alphavantage.parameters.Interval;
import com.crazzyghost.alphavantage.parameters.OutputSize;

import java.util.ArrayList;
import java.util.Date;

public class ChartAPI {

    private final String apiKey = "L5DXRR793TYLP95U";
    private final Interval interval = Interval.FIVE_MIN;
    private final OutputSize outputSize = OutputSize.FULL;

    private final Config cfg = Config.builder()
            .key(apiKey)
            .timeOut(10)
            .build();

    public ChartAPI(){
        AlphaVantage.api().init(cfg);
    }

    public GameRound getGameRound(CurrencyPair currencyPair){
        ForexResponse response = this.fetchChart(currencyPair);
        Chart chart = this.convertToChart(response);
        return new GameRound(chart);
    }

    private ForexResponse fetchChart(CurrencyPair currencyPair){

        ForexResponse response = AlphaVantage.api()
                .forex()
                .intraday()
                .fromSymbol(currencyPair.getFrom().toString())
                .toSymbol(currencyPair.getTo().toString())
                .interval(this.interval)
                .outputSize(this.outputSize)
                .fetchSync();

        return response;
    }

    private Chart convertToChart(ForexResponse response){
        ArrayList<ForexUnit> ForexUnits = new ArrayList<>(response.getForexUnits());
        ArrayList<String> dates = new ArrayList<>();
        ArrayList<Double> values = new ArrayList<>();

        for(ForexUnit fx: ForexUnits){
            dates.add(fx.getDate());
            values.add(fx.getClose());
        }

        CurrencyPair currencyPair = new CurrencyPair(Currency.valueOf(response.getMetaData().getFromSymbol()), Currency.valueOf(response.getMetaData().getToSymbol()));

        return new Chart(values, dates, currencyPair);

    }

}
