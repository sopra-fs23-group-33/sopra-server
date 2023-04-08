package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunner;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.ChartException;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToJoinException;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameStatusRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
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
    private final Logger log = LoggerFactory.getLogger(UserService.class);
    private final GameRepository gameRepository;
    private final UserService userService;
    private final PlayerRepository playerRepository;
    private final GameStatusRepository gameStatusRepository;

    private final GameRunner gameRunner;

    @Autowired
    public GameService(@Qualifier("gameRepository") GameRepository gameRepository,
                       UserService userService,
                       PlayerRepository playerRepository,
                       GameStatusRepository gameStatusRepository,
                       GameRunner gameRunner) {
        this.gameRepository = gameRepository;
        this.userService = userService;
        this.playerRepository = playerRepository;
        this.gameStatusRepository = gameStatusRepository;
        this.gameRunner = gameRunner;
    }

    private void flush(){
        this.playerRepository.flush();
        this.gameRepository.flush();
        this.gameStatusRepository.flush();
    }

    public Game createGame(User user, GameData gameData){
        if(!user.getState().equals(UserState.ONLINE)) {
            String ErrorMessage = "cannot Create game because user is still in an ongoing game or offline";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
        User creator = this.userService.getUserByUserID(user.getUserID());

        this.checkIfValidGameData(gameData);

        Game newGame = new Game(creator, gameData);
        newGame.init();

        Game createdGame = this.gameRepository.saveAndFlush(newGame);
        return createdGame;
    }

    private void checkIfValidName(String name){
        Pattern patternOneLetter = Pattern.compile("[a-zA-Z]");
        Pattern patternInvalidCharacters = Pattern.compile("[^a-zA-Z0-9_!?#@&$.]");

        Matcher matcherOneLetter = patternOneLetter.matcher(name);
        Matcher matcherInvalidCharacters = patternInvalidCharacters.matcher(name);

        String ErrorMessage = "";

        if (!matcherOneLetter.find()){
            ErrorMessage = ErrorMessage + "Invalid name: Does not contain alphabetic characters.\n";
        } if (matcherInvalidCharacters.find()){
            ErrorMessage = ErrorMessage +  "Invalid name: Contains invalid characters.\n";
        } if (name.length() > 30 || name.isEmpty()){
            ErrorMessage = ErrorMessage +  "Invalid name: Too long or empty.\n";
        }

        if(!ErrorMessage.isEmpty())
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }

    private void  checkIfValidGameData(GameData gameData){
        GameType gameType;
        int totalLobbySize = gameData.getTotalLobbySize();
        String name = gameData.getName();

        checkIfValidName(name);

        try{
            gameType = GameType.valueOf(gameData.getTypeOfGame());
        }
        catch (Exception e){
            String ErrorMessage = "invalid type of game was provided";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }

        if(!gameType.validNumberOfPlayers(totalLobbySize)){
            String ErrorMessage = "size of lobby does not match type of game";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
    }

    public Game getGameByGameID(Long gameID) {
        Game gameByID = this.gameRepository.findByGameID(gameID);

        if (gameByID != null)
            return gameByID;
        else {
            String ErrorMessage = "Game with gameId " + gameID + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);
        }
    }

    public Player join(User userToJoin, Long gameID){
        Game game = this.getGameByGameID(gameID);
        User user = this.userService.getUserByUsername(userToJoin.getUsername());

        try {
            Player player = game.join(user);
        }
        catch (FailedToJoinException e){
            String ErrorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }

        game = this.gameRepository.saveAndFlush(game);

        try{
            return game.findPlayerByUser(user);
        }
        catch (PlayerNotFoundException e){
            String ErrorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
    }

    public void leave(User userToLeave, Long gameID){
        Game game = this.getGameByGameID(gameID);
        User user = this.userService.getUserByUsername(userToLeave.getUsername());
        try {
            game.leave(user);
            game = this.gameRepository.saveAndFlush(game);
        }
        catch (PlayerNotFoundException e){
            String ErrorMessage = "Failed to leave game because player is not member of this game";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }

        if(game.getNumberOfPlayersInLobby() == 0){
            this.gameRepository.deleteByGameID(game.getGameID());
            this.gameRepository.flush();
        }
    }

    public void start(Long gameID){
        Game game = this.getGameByGameID(gameID);
        if(game.canStart() && game.getState().equals(GameState.LOBBY))
            gameRunner.run(game);
        else{
            String ErrorMessage = "Game with gameId " + gameID + " cannot be started";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
    }

    public Player creator(Long gameID){
        Game game = this.getGameByGameID(gameID);
        try {
            return game.creator();
        }
        catch (PlayerNotFoundException e){
            String ErrorMessage = "Creator was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);
        }
    }

    public Chart chart(Long gameID){
        Game game = this.getGameByGameID(gameID);
        try{
            return game.chart();
        }
        catch (ChartException e) {
            String ErrorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
    }

    public List<Player> players(Long gameID){
        Game game = this.getGameByGameID(gameID);
        List<Player> players =  game.getPlayers();
        players.sort(Comparator.comparingInt(Player ::getBalance));
        return players;
    }

    public List<GameData> getAllGames(String filter){
        GameState gameState;
        try {
            gameState = GameState.valueOf(filter);
        }
        catch (Exception e){
            String ErrorMessage = "invalid filter argument provided";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
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
}

