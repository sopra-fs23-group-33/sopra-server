package ch.uzh.ifi.hase.soprafs23.rest.dto;

import java.time.LocalDate;

import ch.uzh.ifi.hase.soprafs23.constant.UserState;

public class UserGetDTO {

  private Long id;
  private String username;
  private String token;
  private LocalDate creationDate;
  private UserState status;
  private int totalRoundsPlayed;
  private int numberOfBetsWon;
  private int numberOfBetsLost;
  private int rank;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
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

  public UserState getState() {
    return status;
  }

  public void setState(UserState status) {
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
}
