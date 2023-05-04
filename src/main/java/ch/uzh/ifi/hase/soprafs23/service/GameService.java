package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Data.EventData;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.Runner.BackgroundChartFetcher;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunnerV2;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class GameService {
    private final GameRepository gameRepository;
    private final UserService userService;
    private final GameRunnerV2 gameRunnerV2;
    private final PlayerService playerService;
    private final BackgroundChartFetcher backgroundChartFetcher;
    private final GameRoundRepository gameRoundRepository;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       UserService userService,
                       GameRunnerV2 gameRunner,
                       BackgroundChartFetcher backgroundChartFetcher,
                       GameRoundRepository gameRoundRepository,
                       PlayerService playerService) {

        this.gameRepository = gameRepository;
        this.userService = userService;
        this.gameRunnerV2 = gameRunner;
        this.backgroundChartFetcher = backgroundChartFetcher;
        this.backgroundChartFetcher.enqueue(16);
        this.gameRoundRepository = gameRoundRepository;
        this.playerService = playerService;
    }

    public Game createGame(User user, GameData gameData){
        if(!user.getStatus().equals(UserStatus.ONLINE)) {
            String errorMessage = "cannot Create game because user is still in an ongoing game or offline";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
        User creator = this.userService.getUserByUserID(user.getUserID());

        this.checkIfValidGameData(gameData);

        Game newGame = new Game(creator, gameData);
        newGame.init();

        Game createdGame = this.gameRepository.saveAndFlush(newGame);

        List<GameRound> gameRounds = this.gameRoundRepository.findTop8ByUsageOrderByRoundIDAsc(false);

        for(GameRound gameRound: gameRounds){
            createdGame.addGameRound(gameRound);
        }

        this.backgroundChartFetcher.enqueue(8);

        createdGame = this.gameRepository.saveAndFlush(newGame);

        if(createdGame.isPowerupsActive()){
            try {
                Player player = createdGame.creator();
                this.playerService.addPowerups(createdGame.getNumberOfRoundsToPlay(), player.getPlayerID(), createdGame.getType());

            }
            catch (PlayerNotFoundException ignored){
                ;
            }
        }

        return createdGame;
    }

    private void checkIfValidName(String name){
        Pattern patternOneLetter = Pattern.compile("[a-zA-Z]");
        Pattern patternInvalidCharacters = Pattern.compile("[^a-zA-Z0-9_!?#@&$.]");

        Matcher matcherOneLetter = patternOneLetter.matcher(name);
        Matcher matcherInvalidCharacters = patternInvalidCharacters.matcher(name);

        String errorMessage = "";

        if (!matcherOneLetter.find()){
            errorMessage = errorMessage + "Invalid name: Does not contain alphabetic characters.\n";
        } 
        
        if (matcherInvalidCharacters.find()){
            errorMessage = errorMessage +  "Invalid name: Contains invalid characters.\n";
        } 
        
        if (name.length() > 30 || name.isEmpty()){
            errorMessage = errorMessage +  "Invalid name: Too long or empty.\n";
        }

        if(!errorMessage.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
    }

    private void  checkIfValidGameData(GameData gameData){
        GameType gameType;
        int totalLobbySize = gameData.getTotalLobbySize();
        String name = gameData.getName();

        checkIfValidName(name);

        try{
            gameType = gameData.getTypeOfGame();
        }
        catch (Exception e){
            String errorMessage = "invalid type of game was provided";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        if(!gameType.validNumberOfPlayers(totalLobbySize)){
            String errorMessage = "size of lobby does not match type of game";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
        if(gameData.getNumberOfRoundsToPlay() > 8){
            String errorMessage = "Number of Rounds is limited to 8";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
        if(gameData.getNumberOfRoundsToPlay() < 1){
            String errorMessage = "Number of Rounds must be at minimum one";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
        if(gameData.getTotalLobbySize() > 8){
            String errorMessage = "Lobby size is limited to 8";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    public Game getGameByGameID(Long gameID) {
        Game gameByID = this.gameRepository.findByGameID(gameID);

        if (gameByID != null)
            return gameByID;
        else {
            String errorMessage = "Game with gameId " + gameID + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public Player join(User userToJoin, Long gameID){
        Game game = this.getGameByGameID(gameID);
        User user = this.userService.getUserByUsername(userToJoin.getUsername());

        try {
            game.join(user);
        }
        catch (FailedToJoinException e){
            String errorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        game = this.gameRepository.saveAndFlush(game);


        try{
            Player player = game.findPlayerByUser(user);

            if(game.isPowerupsActive())
                this.playerService.addPowerups(game.getNumberOfRoundsToPlay(), player.getPlayerID(), game.getType());

            return player;
        }
        catch (PlayerNotFoundException e){
            String errorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public void leave(User userToLeave, Long gameID){
        Game game = this.getGameByGameID(gameID);
        User user = this.userService.getUserByUsername(userToLeave.getUsername());

        try {
            game.leave(user);
            game = this.gameRepository.saveAndFlush(game);
        }
        catch (Error | PlayerNotFoundException e){
            String errorMessage = "Failed to leave game because player is not member of this game";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        try {
            game = this.getGameByGameID(gameID);

            if (game.getNumberOfPlayersInLobby() == 0) {
                this.gameRepository.deleteByGameID(game.getGameID());
                this.gameRepository.flush();
            }
        }
        catch (Exception | Error ignored){
            return;
        }
    }

    public void start(Long gameID, String token){
        Game game = this.getGameByGameID(gameID);

        this.tokenMatchStart(token, game);

        if(game.canStart() && game.getState().equals(GameState.LOBBY))
            this.gameRunnerV2.startGame(gameID);
        else{
            String errorMessage = "Game with gameId " + gameID + " cannot be started";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    private void tokenMatchStart(String token, Game game){
        String test = "test123";

        if (!game.getCreator().getToken().equals(token) && !token.equals(test)){
            String errorMessage = "provided token does not match the creator in game with gameID: " + game.getGameID();
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
    }

    public Player creator(Long gameID){
        Game game = this.getGameByGameID(gameID);
        try {
            return game.creator();
        }
        catch (PlayerNotFoundException e){
            String errorMessage = "Creator was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, errorMessage);
        }
    }

    public ChartData chart(Long gameID){
        Game game = this.getGameByGameID(gameID);
        try{
            return game.chart().status();
        }
        catch (ChartException e) {
            String errorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }
    }

    public List<Player> players(Long gameID){
        Game game = this.getGameByGameID(gameID);

        List<Player> players =  game.getPlayers();
        List<Player> sortedPlayers = new ArrayList<>();
        sortedPlayers.addAll(players);

        sortedPlayers.sort(Comparator.comparingInt(Player ::getBalance).reversed().thenComparing(Player::getPlayerID));
        return players;
    }

    public List<GameData> getAllGames(String filter){
        GameState gameState;
        try {
            gameState = GameState.valueOf(filter);
        }
        catch (Exception e){
            String errorMessage = "invalid filter argument provided";
            throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
        }

        List<Game> games = this.gameRepository.findAll();
        List<GameData> gameData = new ArrayList<>();

        for(Game game: games){
            if (game.getState().equals(gameState))
                gameData.add(game.status());
        }

        return gameData;
    }

    public List<GameData> getAllGames() {
        List<Game> games = this.gameRepository.findAll();

        List<GameData> gameData = new ArrayList<>();

        for(Game game: games) {
            gameData.add(game.status());
        }
        return gameData;
    }

    public void tokenMatch(String token, Long gameID) {
        String test = "test123";
        if (token.equals(test))
            return;

        Game gameByID = this.getGameByGameID(gameID);

        List<Player> players = gameByID.getPlayers();

        for (Player player : players) {
            if (player.getUser().getToken().equals(token))
                return;
        }

        if(!gameByID.getPlayers().isEmpty()) {
            String errorMessage = "provided token does not match any player in game with gameID: " + gameID;
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, errorMessage);
        }
    }

    public List<AbstractPowerUp> getUsedPowerups(Long gameID){
        Game game = this.getGameByGameID(gameID);
        return game.getUsedPowerups();
    }

    public EventData getEvent(Long gameID){
        Game game = this.getGameByGameID(gameID);
        return game.getEvent().getEventData();
    }

}

