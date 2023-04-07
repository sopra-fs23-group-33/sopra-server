package ch.uzh.ifi.hase.soprafs23.Betting;


import ch.qos.logback.core.rolling.TimeBasedFileNamingAndTriggeringPolicyBase;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;

import javax.persistence.Embeddable;

@Embeddable
public class Result {
    Direction outcome;
    int profit;
    int bettingAmount;

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
