package ch.uzh.ifi.hase.soprafs23.Forex;

import ch.uzh.ifi.hase.soprafs23.constant.Currency;

import javax.persistence.*;

@Embeddable
public class CurrencyPair {

    @Enumerated(EnumType.STRING)
    @Column(name = "from", nullable = false)
    private Currency from;
    @Enumerated(EnumType.STRING)
    @Column(name = "to", nullable = false)
    private Currency to;

    public CurrencyPair(){

    }

    public CurrencyPair(Currency from, Currency to){
        this.from = from;
        this.to = to;
    }

    public Currency getFrom() {
        return from;
    }

    public Currency getTo() {
        return to;
    }
}
