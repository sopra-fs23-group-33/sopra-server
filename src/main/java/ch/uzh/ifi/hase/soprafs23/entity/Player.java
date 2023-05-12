package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Betting.InstructionManager;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;


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
    private PlayerState state;

    @OneToOne(mappedBy = "player", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private InstructionManager instructionManager;

    @Embedded
    private Result result;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY )
    private List<AbstractPowerUp> powerups;

    public Player() {}

    public Player(User user){
        this.user = user;
        this.balance = 1000;
        this.numberOfBetsWon = 0;
        this.numberOfBetsLost = 0;
        this.resetBet();
        this.state = PlayerState.ACTIVE;
        this.result = new Result(Direction.NONE, 0, 0);
        //this.user.setStatus(UserStatus.PLAYING);
    }

    public void init(){
        InstructionManager newInstructionManager = new InstructionManager();
        newInstructionManager.init(this);
        this.instructionManager = newInstructionManager;
        this.powerups = new ArrayList<>();
    }

    public void placeBet(Bet newBet) throws FailedToPlaceBetException {
        if (this.state != PlayerState.ACTIVE)
            throw new FailedToPlaceBetExceptionBecauseInactive();
        else if(newBet.getAmount() > this.balance)
            throw new FailedToPlaceBetExceptionBecauseBalance();
        else if (newBet.getDirection().equals(Direction.NONE))
            throw new FailedToPlaceBetExceptionBecauseDirection();
        else if(newBet.getAmount() < 0)
            throw new FailedToPlaceBetExceptionBecauseNegative();
        else
            this.currentBet = newBet;
    }

    public void addInstruction(Instruction instruction){
        this.instructionManager.addInstruction(instruction);
    }

    public void endRound(Direction direction, Double ratio){
        if(direction == this.currentBet.getDirection()){
            this.numberOfBetsWon += 1;
            this.user.roundWon();
        }
        else if(direction != this.currentBet.getDirection() && this.currentBet.getDirection() != Direction.NONE){
            this.numberOfBetsLost += 1;
            this.user.roundLost();
        }

        int newBalance = this.instructionManager.computeNewBalance(direction, ratio);

        int profit = newBalance - this.balance;
        this.result = new Result(direction, profit, this.currentBet.getAmount());
        this.user.incrementProfit(profit);
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

    public void setPlayerID(Long playerID) {
        this.playerID = playerID;
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

    public int getNumberOfBetsWon() {
        return numberOfBetsWon;
    }

    public int getNumberOfBetsLost() {
        return numberOfBetsLost;
    }

    public InstructionManager getInstructionManager() {
        return instructionManager;
    }

    public ArrayList<AbstractPowerUp> getActivePowerups() {
        ArrayList<AbstractPowerUp> activePowerups = new ArrayList<>();

        for(AbstractPowerUp powerUp: this.powerups){
            if(powerUp.isActive())
                activePowerups.add(powerUp);
        }
        return activePowerups;
    }

    public List<AbstractPowerUp> getAvailablePowerups(){
        return this.powerups;
    }

    public void resetPowerups(){
        ArrayList<AbstractPowerUp> activatedPowerups = new ArrayList<>();

        for(AbstractPowerUp powerUp: this.powerups){
            if(powerUp.isActive())
                activatedPowerups.add(powerUp);
        }

        for(AbstractPowerUp powerUp: activatedPowerups){
            this.powerups.remove(powerUp);
        }
    }

    public void addPowerup(AbstractPowerUp powerup){
        if(powerup.getOwnerID().equals(this.playerID))
            this.powerups.add(powerup);
    }

    public void activatePowerup(AbstractPowerUp powerup) throws PowerupNotFoundException{
        if(this.powerups.contains(powerup)){
            powerup.activate();
        }
        else {
            throw new PowerupNotFoundException();
        }
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
