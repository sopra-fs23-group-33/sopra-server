package ch.uzh.ifi.hase.soprafs23.rest.dto;

public class PlayerGetDTO {
    private String username;
    private Long playerID;
    private Integer accountBalance;
    private Integer numberOfWonRounds;
    private Integer numberOfLostRounds;
    private String typeOfCurrentBet;

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

    public String getTypeOfCurrentBet() {
        return typeOfCurrentBet;
    }

    public void setTypeOfCurrentBet(String typeOfCurrentBet) {
        this.typeOfCurrentBet = typeOfCurrentBet;
    }
}
