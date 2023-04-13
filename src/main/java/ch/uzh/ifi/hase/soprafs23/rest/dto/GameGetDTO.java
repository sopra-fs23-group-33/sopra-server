package ch.uzh.ifi.hase.soprafs23.rest.dto;


public class GameGetDTO {
    Long gameID;

    String status;

    String name;

    String typeOfGame;

    boolean powerupsActive;

    boolean eventsActive;

    int timer;

    int totalLobbySize;

    int numberOfPlayersInLobby;

    int numberOfRoundsToPlay;

    int currentRoundPlayed;

    String event;

    String creator;

    public Long getGameID() {
        return gameID;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeOfGame() {
        return typeOfGame;
    }

    public void setTypeOfGame(String typeOfGame) {
        this.typeOfGame = typeOfGame;
    }

    public boolean isPowerupsActive() {
        return powerupsActive;
    }

    public void setPowerupsActive(boolean powerupsActive) {
        this.powerupsActive = powerupsActive;
    }

    public boolean isEventsActive() {
        return eventsActive;
    }

    public void setEventsActive(boolean eventsActive) {
        this.eventsActive = eventsActive;
    }

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public int getTotalLobbySize() {
        return totalLobbySize;
    }

    public void setTotalLobbySize(int totalLobbySize) {
        this.totalLobbySize = totalLobbySize;
    }

    public int getNumberOfPlayersInLobby() {
        return numberOfPlayersInLobby;
    }

    public void setNumberOfPlayersInLobby(int numberOfPlayersInLobby) {
        this.numberOfPlayersInLobby = numberOfPlayersInLobby;
    }

    public int getNumberOfRoundsToPlay() {
        return numberOfRoundsToPlay;
    }

    public void setNumberOfRoundsToPlay(int numberOfRoundsToPlay) {
        this.numberOfRoundsToPlay = numberOfRoundsToPlay;
    }


    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }


    public int getCurrentRoundPlayed() {
        return currentRoundPlayed;
    }

    public void setCurrentRoundPlayed(int currentRoundPlayed) {
        this.currentRoundPlayed = currentRoundPlayed;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

}
