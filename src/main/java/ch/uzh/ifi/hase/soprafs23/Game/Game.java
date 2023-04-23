package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;

import ch.uzh.ifi.hase.soprafs23.Powerups.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.Powerups.PowerupX2;
import ch.uzh.ifi.hase.soprafs23.constant.*;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;


import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;



@Entity(name = "Game")
@Table(name = "game")
public class Game {

    @Id
    @GeneratedValue
    private Long gameID;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private GameType type;

    @Column(name = "totalLobbySize", nullable = false)
    private int totalLobbySize;


    @Column(name = "numberOfRoundsToPlay", nullable = false)
    int numberOfRoundsToPlay;

    @Column(name = "numberOfRoundsPlayed", nullable = false)
    int currentRoundPlayed;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.LAZY )
    private List<GameRound> gameRounds;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    List<Player> players;

    @OneToOne
    private User creator;

    @Column(name = "powerups_active", nullable = false)
    private boolean powerupsActive = false;

    @Column(name = "events_active", nullable = false)
    private boolean eventsActive = false;

    @Column(name = "timer")
    private int timer;

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private GameStatus gameStatus;

    @Column(name = "resultTime", nullable = false)
    private int resultTime;

    @Column(name = "bettingTime", nullable = false)
    private int bettingTime;

    public Game() {}

    public Game(User creator, GameData gameData){
        this.creator = creator;
        this.name = gameData.getName();
        this.eventsActive = gameData.isEventsActive();
        this.powerupsActive = gameData.isPowerupsActive();
        this.numberOfRoundsToPlay = gameData.getNumberOfRoundsToPlay();
        this.totalLobbySize = gameData.getTotalLobbySize();
        this.type = gameData.getTypeOfGame();
        this.currentRoundPlayed = 0;
        this.gameRounds = new ArrayList<>();
        this.players = new ArrayList<>();
        this.bettingTime = 15;
        this.resultTime = 15;
    }

    public void init(){
        this.gameStatus = new LobbyState(this);
        try {
            this.join(this.creator);
        }
        catch (FailedToJoinException ignored){
            ;
        }
    }


    public Player join(User user) throws FailedToJoinException {
        return this.gameStatus.join(user);
    }

    public void leave(User user) throws PlayerNotFoundException {
        this.gameStatus.leave(user);
    }

    void remove(Player player){
        if(this.players.contains(player) && player.getState().equals(PlayerState.INACTIVE)) {
            User user = player.getUser();

            if(user.getStatus().equals(UserStatus.PLAYING))
                user.setStatus(UserStatus.ONLINE);
            this.players.remove(player);
        }
    }

    public Player findPlayerByUser(User user) throws PlayerNotFoundException {
        for(Player player: this.players){
            if(player.getUser().equals(user)){
                return player;
            }
        }
        throw new PlayerNotFoundException();
    }


    public Player creator() throws PlayerNotFoundException {
        return this.findPlayerByUser(this.creator);
    }

    public void start() throws StartException {
        this.gameStatus.start();
    }

    public boolean checkIntegrity(){

        if(this.currentRoundPlayed + 1 > this.gameRounds.size())
            return false;
        else
            return this.type.validNumberOfPlayers(this.getNumberOfPlayersInLobby());


    }

    public boolean canStart(){
        if(!this.checkIntegrity())
            return false;
        else if(this.getNumberOfPlayersInLobby() > this.totalLobbySize)
            return false;
        else return !this.gameRounds.isEmpty();
    }

    public void addGameRound(GameRound gameRound){
        if(this.gameRounds.size() < this.numberOfRoundsToPlay) {
            gameRound.activate();
            this.gameRounds.add(gameRound);
        }
    }

    public void endRound() throws endRoundException {
        this.gameStatus.endRound();
    }

    public void nextRound() throws nextRoundException {
        this.gameStatus.nextRound();
    }

    public GameData status(){
        GameData data = new GameData();

        data.setGameID(this.getGameID());
        data.setStatus(this.gameStatus.gameState);
        data.setName(this.getName());
        data.setTypeOfGame(this.getType());
        data.setPowerupsActive(this.powerupsActive);
        data.setEventsActive(this.eventsActive);
        data.setTimer(this.getTimer());
        data.setTotalLobbySize(this.getTotalLobbySize());
        data.setNumberOfPlayersInLobby(this.getNumberOfPlayersInLobby());
        data.setNumberOfRoundsToPlay(this.numberOfRoundsToPlay);
        data.setCurrentRoundPlayed(this.getCurrentRoundPlayed());
        data.setEvent(null);
        data.setCreator(this.creator.getUsername());

        return data;
    }

    public boolean allBetsPlaced(){
        for(Player player: this.players){
            if(player.getState().equals(PlayerState.ACTIVE) && player.getCurrentBet().getDirection().equals(Direction.NONE)){
                return false;
            }
        }
        return true;
    }

    public Chart chart() throws ChartException {
        return this.gameStatus.chart();
    }

    public void setTimerForBetting(){
        this.setTimer(this.bettingTime);
    }

    public void setTimerForResult(){
        this.setTimer(this.resultTime);
    }

    public void decrementTimer(){
        if(this.timer > 0){
            this.timer -= 1;
        }
    }

    public void collectAndDistributeInstructions(){
        ArrayList<Instruction> instructions = new ArrayList<>();
        ArrayList<AbstractPowerUp> playerPowerups = new ArrayList<>();

        for(Player player: this.players){
           playerPowerups.addAll(player.getActivePowerups());
        }

        for(AbstractPowerUp powerUp: playerPowerups)
            instructions.addAll(powerUp.generateInstructions(this));

        for(Instruction instruction: instructions){
            Long ownerID = instruction.getOwnerID();

            try {
                Player player = this.findPlayerByID(ownerID);
                player.addInstruction(instruction);
            }
            catch (PlayerNotFoundException ignored){

            }
        }
    }

    public Player findPlayerByID(Long ID) throws PlayerNotFoundException {
        for(Player player: this.players){
            if(player.getPlayerID().equals(ID)){
                return player;
            }
        }
        throw new PlayerNotFoundException();
    }


    public void incrementRoundsPlayed(){
        this.currentRoundPlayed++;
    }

    public void update() throws endRoundException, nextRoundException, StartException {
        this.gameStatus.update();
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

    public void setCurrentRoundPlayed(int currentRoundPlayed) {
        this.currentRoundPlayed = currentRoundPlayed;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getBettingTime() {
        return bettingTime;
    }

    public void setBettingTime(int bettingTime) {
        this.bettingTime = bettingTime;
    }

    public int getResultTime() {
        return resultTime;
    }

    public void setResultTime(int resultTime) {
        this.resultTime = resultTime;
    }


}
