package ch.uzh.ifi.hase.soprafs23.entity;

import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Game {

    @Id
    @GeneratedValue
    private Long gameID;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private GameState state;

    @Column(name = "name", nullable = false)
    private String name;


    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GameType type;

    @Column(name = "totalLobbySize", nullable = false)
    private int totalLobbySize;

    @Column(name = "numberOfPlayersInLobby", nullable = false)
    private int numberOfPlayersInLobby;

    @Column(name = "numberOfRoundsToPlay", nullable = false)
    private int numberOfRoundsToPlay;

    @Column(name = "numberOfRoundsPlayed", nullable = false)
    private int numberOfRoundsPlayed;

    @OneToMany(cascade = CascadeType.ALL)
    private List<GameRound> gameRounds;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Player> players;

    @OneToOne(cascade = CascadeType.PERSIST)
    private User creator;

    @Column(name = "powerups_active", nullable = false)
    private boolean powerupsActive = false;

    @Column(name = "events_active", nullable = false)
    private boolean eventsActive = false;

    @Column(name = "timer")
    private int timer;


    public Game() {}

    public Game(User creator, String name, int numberOfRoundsToPlay, boolean powerupsActive, boolean eventsActive, GameType type){
        this.creator = creator;
        this.name = name;
        this.eventsActive = eventsActive;
        this.powerupsActive = powerupsActive;
        this.numberOfRoundsToPlay = numberOfRoundsToPlay;
        this.state = GameState.LOBBY;
        this.totalLobbySize = 1;
        this.type = type;
        this.numberOfRoundsPlayed = 0;
        this.numberOfPlayersInLobby = 1;
        this.gameRounds = new ArrayList<>();
        this.players = new ArrayList<>();
        this.addPlayer(this.creator);

    }

    private void addPlayer(User user){
        if(this.state == GameState.LOBBY && !players.contains(new Player(user))) {
            Player newPlayer = new Player(user);
            newPlayer.init();
            this.players.add(newPlayer);
        }
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameType getType() {
        return type;
    }

    public void setType(GameType type) {
        this.type = type;
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

    public List<GameRound> getGameRounds() {
        return gameRounds;
    }

    public void setGameRounds(List<GameRound> gameRounds) {
        this.gameRounds = gameRounds;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
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
