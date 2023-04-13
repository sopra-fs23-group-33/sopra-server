package ch.uzh.ifi.hase.soprafs23.Betting;


import ch.uzh.ifi.hase.soprafs23.constant.Direction;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Result {
    @Enumerated(EnumType.STRING)
    private Direction outcome;
    private int profit;
    private int bettingAmount;

    public Result(){}

    public Result(Direction outcome, int profit, int bettingAmount){
        this.bettingAmount = bettingAmount;
        this.profit = profit;
        this.outcome = outcome;
    }

    public Direction getOutcome() {
        return outcome;
    }

    public void setOutcome(Direction outcome) {
        this.outcome = outcome;
    }

    public int getProfit() {
        return profit;
    }

    public void setProfit(int profit) {
        this.profit = profit;
    }

    public int getBettingAmount() {
        return bettingAmount;
    }

    public void setBettingAmount(int bettingAmount) {
        this.bettingAmount = bettingAmount;
    }

}
