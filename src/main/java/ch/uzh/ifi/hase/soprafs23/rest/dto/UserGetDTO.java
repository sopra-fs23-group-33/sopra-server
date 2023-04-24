package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;

public class UserGetDTO {

  private Long userID;
  private String username;
  private String token;
  private LocalDate creationDate;
  private UserStatus status;
  private int totalRoundsPlayed;
  private int numberOfBetsWon;
  private int numberOfBetsLost;
  private int rank;

  private double winRate;

  public Long getUserID() {
    return this.userID;
  }

  public void setUserID(Long id) {
    this.userID = id;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public LocalDate getCreationDate(){
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate){
    this.creationDate = creationDate;
  }

  public UserStatus getStatus() {
    return status;
  }

  public void setStatus(UserStatus status) {
    this.status = status;
  }

  public int getTotalRoundsPlayed(){
    return totalRoundsPlayed;
  }

  public void setTotalRoundsPlayed(int totalRoundsPlayed){
    this.totalRoundsPlayed = totalRoundsPlayed;
  }

  public int getNumberOfBetsWon(){
    return numberOfBetsWon;
  }

  public void setNumberOfBetsWon(int numberOfBetsWon){
    this.numberOfBetsWon = numberOfBetsWon;
  }

  public int getNumberOfBetsLost(){
    return numberOfBetsLost;
  }

  public void setNumberOfBetsLost(int numberOfBetsLost){
    this.numberOfBetsLost = numberOfBetsLost;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public int getRank(){
    return rank;
  }

  public void setRank(int rank){
    this.rank = rank;
  }

    public double getWinRate() {
        return winRate;
    }

    public void setWinRate(double winRate) {
        this.winRate = winRate;
    }
}
