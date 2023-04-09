package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Betting.InstructionManager;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToPlaceBetException;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToPlaceBetExceptionBecauseBalance;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToPlaceBetExceptionBecauseDirection;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToPlaceBetExceptionBecauseInactive;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

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

    @Enumerated(EnumType.STRING)
    PlayerState state;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    InstructionManager instructionManager;

    @Embedded
    Result result;

    public Player() {}

    public Player(User user){
        this.user = user;
        this.balance = 1000;
        this.numberOfBetsWon = 0;
        this.numberOfBetsLost = 0;
        this.resetBet();
        this.state = PlayerState.ACTIVE;
        this.result = new Result(Direction.NONE, 0, 0);
        this.user.setState(UserState.PLAYING);
    }

    public void init(){
        InstructionManager newInstructionManager = new InstructionManager();
        newInstructionManager.init(this);
        this.instructionManager = newInstructionManager;
    }

    public void placeBet(Bet newBet) throws FailedToPlaceBetException {
        if (this.state != PlayerState.ACTIVE){
            throw new FailedToPlaceBetExceptionBecauseInactive();
        }
        else if(newBet.getAmount() > this.balance)
            throw new FailedToPlaceBetExceptionBecauseBalance();
        else if (newBet.getDirection().equals(Direction.NONE))
            throw new FailedToPlaceBetExceptionBecauseDirection();
        else
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

        int newBalance = this.instructionManager.computeNewBalance(direction, ratio);
        int profit = newBalance - this.balance;
        this.result = new Result(direction, profit, this.currentBet.getAmount());
        this.balance = newBalance;
    }

    public void resetBet(){
        this.currentBet = new Bet(Direction.NONE, 0);
    }

    public PlayerData status(){
        PlayerData data = new PlayerData();

        data.setPlayerID(this.playerID);
        data.setUsername(this.user.getUsername());
        data.setAccountBalance(this.balance);
        data.setNumberOfWonRounds(this.numberOfBetsWon);
        data.setNumberOfLostRounds(this.numberOfBetsLost);
        data.setTypeOfCurrentBet(this.currentBet.getDirection());

        return data;
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

    public User getUser() {
        return user;
    }

    public void setState(PlayerState state) {
        this.state = state;
    }

    public PlayerState getState() {
        return state;
    }

    public Result getResult(){
        return this.result;
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
