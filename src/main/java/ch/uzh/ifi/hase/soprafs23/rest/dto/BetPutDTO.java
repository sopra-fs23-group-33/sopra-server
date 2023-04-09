package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.constant.Direction;

public class BetPutDTO {
    private Direction type;
    private int amount;


    public Direction getType() {
        return type;
    }

    public void setType(Direction type) {
        this.type = type;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }


}
