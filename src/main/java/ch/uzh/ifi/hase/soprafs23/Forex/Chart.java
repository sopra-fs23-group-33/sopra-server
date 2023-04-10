package ch.uzh.ifi.hase.soprafs23.Forex;

import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Chart {
    @ElementCollection(fetch = FetchType.LAZY)
    private List<Double> numbers;
    @ElementCollection(fetch = FetchType.LAZY)
    private List<String> dates;
    @Embedded
    private CurrencyPair currencyPair;


    public Chart(){

    }

    public Chart(ArrayList<Double> values, ArrayList<String> dates, CurrencyPair currencyPair){
        this.dates = dates;
        this.numbers = values;
        this.currencyPair = currencyPair;
    }

    public List<String> getDates() {
        return dates;
    }
    public List<Double> getValues() {
        return numbers;
    }

    public Currency getFrom() {
        return currencyPair.getFrom();
    }

    public Currency getTo() {
        return currencyPair.getTo();
    }
    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public ChartData status(){
        ChartData data = new ChartData();

        data.setFromCurrency(this.currencyPair.getFrom());
        data.setToCurrency(this.currencyPair.getTo());
        data.setNumbers(this.numbers);
        data.setDates(this.getDates());

        return data;
    }
}
