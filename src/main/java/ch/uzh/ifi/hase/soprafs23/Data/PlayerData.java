package ch.uzh.ifi.hase.soprafs23.Data;

import ch.uzh.ifi.hase.soprafs23.constant.Direction;

public class PlayerData {
    String username;
    Long playerID;
    Integer accountBalance;
    Integer numberOfWonRounds;
    Integer numberOfLostRounds;
    Direction typeOfCurrentBet;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getPlayerID() {
        return playerID;
    }

    public void setPlayerID(Long playerID) {
        this.playerID = playerID;
    }

    public Integer getAccountBalance() {
        return accountBalance;
    }

    public void setAccountBalance(Integer accountBalance) {
        this.accountBalance = accountBalance;
    }

    public Integer getNumberOfWonRounds() {
        return numberOfWonRounds;
    }

    public void setNumberOfWonRounds(Integer numberOfWonRounds) {
        this.numberOfWonRounds = numberOfWonRounds;
    }

    public Integer getNumberOfLostRounds() {
        return numberOfLostRounds;
    }

    public void setNumberOfLostRounds(Integer numberOfLostRounds) {
        this.numberOfLostRounds = numberOfLostRounds;
    }

    public Direction getTypeOfCurrentBet() {
        return typeOfCurrentBet;
    }

    public void setTypeOfCurrentBet(Direction typeOfCurrentBet) {
        this.typeOfCurrentBet = typeOfCurrentBet;
    }

}
