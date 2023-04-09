package ch.uzh.ifi.hase.soprafs23.Data;

import ch.uzh.ifi.hase.soprafs23.constant.Currency;

import java.util.List;

public class ChartData {
    private Currency fromCurrency;
    private Currency toCurrency;
    private List<String> dates;
    private List<Double> numbers;

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }

    public List<Double> getNumbers() {
        return numbers;
    }

    public void setNumbers(List<Double> numbers) {
        this.numbers = numbers;
    }
}
