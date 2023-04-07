package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.Game.GameStatus;
import ch.uzh.ifi.hase.soprafs23.Game.LobbyState;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "Game")
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue
    Long gameID;

    @Column(name = "name", nullable = false)
    String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    GameType type;

    @Column(name = "totalLobbySize", nullable = false)
    int totalLobbySize;

    //@Column(name = "numberOfPlayersInLobby", nullable = false)
    //int numberOfPlayersInLobby;

    @Column(name = "numberOfRoundsToPlay", nullable = false)
    int numberOfRoundsToPlay;

    @Column(name = "numberOfRoundsPlayed", nullable = false)
    int currentRoundPlayed;

    @OneToMany(cascade = CascadeType.ALL)
    List<GameRound> gameRounds;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    List<Player> players;

    @OneToOne(cascade = CascadeType.PERSIST)
    User creator;

    @Column(name = "powerups_active", nullable = false)
    boolean powerupsActive = false;

    @Column(name = "events_active", nullable = false)
    boolean eventsActive = false;

    @Column(name = "timer")
    int timer;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, orphanRemoval = true)
    GameStatus gameStatus;

    public Game() {}

    public Game(User creator, GameData gameData){
        this.creator = creator;
        this.name = gameData.getName();
        this.eventsActive = gameData.isEventsActive();
        this.powerupsActive = gameData.isPowerupsActive();
        this.numberOfRoundsToPlay = gameData.getNumberOfRoundsToPlay();
        this.totalLobbySize = gameData.getTotalLobbySize();
        this.type = GameType.valueOf(gameData.getTypeOfGame());
        this.currentRoundPlayed = 0;
        //this.numberOfPlayersInLobby = 1;
        this.gameRounds = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public void init(){
        this.gameStatus = new LobbyState(this);
        Player player = this.join(this.creator);
    }


    public Player join(User user){
        return this.gameStatus.join(user);
    }

    public void leave(User user){
        this.gameStatus.leave(user);
    }

    void remove(Player player){
        if(this.players.contains(player) && player.getState().equals(PlayerState.INACTIVE)) {
            User user = player.getUser();

            if(user.getState().equals(UserState.PLAYING))
                user.setState(UserState.ONLINE);
            this.players.remove(player);
        }
    }

    public Player findPlayerByUser(User user){
        for(Player player: this.players){
            if(player.getUser().equals(user)){
                return player;
            }
        }
        String ErrorMessage = "User is not member of this game";
        throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }


    public Player creator(){
        return this.findPlayerByUser(this.creator);
    }

    public void start(){
        this.gameStatus.start();
    }

    public boolean canStart(){
        if(!this.type.validNumberOfPlayers(this.getNumberOfPlayersInLobby()))
            return false;
        else if(this.getNumberOfPlayersInLobby() > this.totalLobbySize)
            return false;
        else if(this.numberOfRoundsToPlay != this.gameRounds.size())
            return false;
        else
            return true;
    }
    public void endRound(){
        this.gameStatus.endRound();
    }

    public void nextRound(){
        this.gameStatus.nextRound();
    }

    public GameData status(){
        GameData data = new GameData();

        data.setGameID(this.getGameID());
        data.setStatus(this.gameStatus.gameState.toString());
        data.setName(this.getName());
        data.setTypeOfGame(this.getType().toString());
        data.setPowerupsActive(this.powerupsActive);
        data.setEventsActive(this.eventsActive);
        data.setTimer(this.getTimer());
        data.setTotalLobbySize(this.getTotalLobbySize());
        data.setNumberOfPlayersInLobby(this.getNumberOfPlayersInLobby());
        data.setCurrentRoundPlayed(this.getCurrentRoundPlayed());
        data.setEvent(null);
        data.setCreator(this.creator.getUsername());

        return data;
    }

    public Chart chart(){
        return this.gameStatus.chart();
    }

    public int getCurrentRoundPlayed() {
        return currentRoundPlayed;
    }

    public Long getGameID() {
        return gameID;
    }

    public void setGameID(Long gameID) {
        this.gameID = gameID;
    }

    public GameState getState() {
        return this.gameStatus.getGameState();
    }

    public void setGameStatus(GameStatus state) {
        this.gameStatus = state;
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
        int size = 0;
        for(Player player: this.players){
            if(player.getState().equals(PlayerState.ACTIVE))
                size++;
        }
        return size;
    }


    public int getNumberOfRoundsToPlay() {
        return numberOfRoundsToPlay;
    }


    public void setNumberOfRoundsToPlay(int numberOfRoundsToPlay) {
        this.numberOfRoundsToPlay = numberOfRoundsToPlay;
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
