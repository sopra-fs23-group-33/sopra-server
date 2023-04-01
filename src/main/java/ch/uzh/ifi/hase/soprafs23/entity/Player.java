package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Betting.InstructionManager;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity(name = "Player")
@Table(name = "player")
public class Player {
    @Id
    @GeneratedValue
    private Long playerID;

    @OneToOne(cascade = CascadeType.PERSIST)
    private User user;

    @Column(name = "balance", nullable = false)
    private int balance;

    @Column(name = "numberOfBetsWon", nullable = false)
    private int numberOfBetsWon;

    @Column(name = "numberOfBetsLost", nullable = false)
    private int numberOfBetsLost;

    @Embedded
    private Bet currentBet;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    InstructionManager instructionManager;

    public Player() {}

    public Player(User user){
        this.user = user;
        this.balance = 1000;
        this.numberOfBetsWon = 0;
        this.numberOfBetsLost = 0;
        this.currentBet = new Bet(Direction.NONE, 0);
    }

    public void init(){
        InstructionManager newInstructionManager = new InstructionManager();
        newInstructionManager.init(this);
        this.instructionManager = newInstructionManager;
    }

    public void placeBet(Bet newBet){
        this.currentBet = newBet;
    }

    public void addInstruction(Instruction instruction){
        this.instructionManager.addInstruction(instruction);
    }

    public void endRound(Direction direction, Double ratio){
        if(direction == this.currentBet.getDirection()){
            this.numberOfBetsWon += 1;
            this.user.incrementNumberOfBetsWon();
        }

        else {
            this.numberOfBetsLost += 1;
            this.user.incrementNumberOfBetsLost();
        }

        this.user.incrementTotalRoundsPlayed();

        this.balance = this.instructionManager.computeNewBalance(direction, ratio);

        this.user.incrementTotalRoundsPlayed();
        this.currentBet = new Bet(Direction.NONE, 0);
    }

    public Long getPlayerID() {
        return playerID;
    }

    public int getBalance() {
        return balance;
    }

    public Bet getCurrentBet() {
        return currentBet;
    }

    @Override
    public boolean equals(Object other){
        if(other == null) {
            return false;
        } else if(other == this ) {
            return true;
        } else if(other.getClass() != getClass()) {
            return false;
        } else {
            return Objects.equals(((Player) other).user, this.user);
        }
    }

    @Override
    public int hashCode(){
        return this.user.hashCode();
    }
}
