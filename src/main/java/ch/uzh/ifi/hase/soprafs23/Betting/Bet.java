package ch.uzh.ifi.hase.soprafs23.Betting;

import ch.uzh.ifi.hase.soprafs23.constant.Direction;

import javax.persistence.*;

@Embeddable
public class Bet {

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @Column(name = "amount", nullable = false)
    private int amount;

    public Bet(){

    }
    public Bet(Direction direction, int amount){
        this.amount = amount;
        this.direction = direction;
    }



    public Direction getDirection() {
        return direction;
    }

    public int getAmount() {
        return amount;
    }


}
