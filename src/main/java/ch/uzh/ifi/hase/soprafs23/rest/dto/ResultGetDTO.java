package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Direction;

public class ResultGetDTO {

    private Direction outcome;
    private int profit;
    private int bettingAmount;


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
