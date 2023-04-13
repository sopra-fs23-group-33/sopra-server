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

    public CurrencyPair(){}

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

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        else if (other == this) {
            return true;
        }
        else if (other.getClass() != getClass()) {
            return false;
        }
        else {
            CurrencyPair cp = (CurrencyPair) other;

            return (this.to.equals(cp.to) && this.from.equals(cp.from)) ||
                    (this.to.equals(cp.from) && this.from.equals(cp.to));
        }
    }


    @Override
    public int hashCode() {
            return this.to.hashCode() * this.from.hashCode();
    }

}
