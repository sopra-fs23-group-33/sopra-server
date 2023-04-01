package ch.uzh.ifi.hase.soprafs23.rest.dto;

import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.*;
import java.util.List;

public class GameGetDTO {

    private Long gameID;

    private GameState state;

    private GameType type;

    private String name;

    private int totalLobbySize;

    private int numberOfPlayersInLobby;

    private int numberOfRoundsToPlay;

    private int numberOfRoundsPlayed;

    private boolean powerupsActive = false;

    private boolean eventsActive = false;

    private int timer;

    public Long getGameID() {
        return gameID;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public GameType getType() {
        return type;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getNumberOfRoundsPlayed() {
        return numberOfRoundsPlayed;
    }

    public void setNumberOfRoundsPlayed(int numberOfRoundsPlayed) {
        this.numberOfRoundsPlayed = numberOfRoundsPlayed;
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


}
