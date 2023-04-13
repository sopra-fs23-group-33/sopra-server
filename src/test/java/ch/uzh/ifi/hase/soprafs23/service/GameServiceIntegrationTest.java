package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.Data.ChartData;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.Game.BettingState;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Game.GameStatus;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToJoinException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRoundRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.server.ResponseStatusException;

import javax.transaction.Transactional;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@WebAppConfiguration
@SpringBootTest
@Transactional
public class GameServiceIntegrationTest {
    @Qualifier("userRepository")
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Qualifier("playerRepository")
    @Autowired
    private PlayerRepository playerRepository;

    @Qualifier("gameRepository")
    @Autowired
    private GameRepository gameRepository;


    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameService gameService;

    private Game game;
    private GameData gameData;
    private User creator;


    @BeforeEach
    void setup()  {
        gameRepository.deleteAll();
        gameRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        creator = new User("Creator", "Pw3as?sword");
        this.userService.createUser(creator);


        gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(2);
        gameData.setTypeOfGame(GameType.MULTIPLAYER);
        gameData.setPowerupsActive(false);
        gameData.setEventsActive(false);
        gameData.setName("GameRoom");
        gameData.setTotalLobbySize(2);

        creator = this.userService.getUserByUsername(creator.getUsername());

        game = this.gameService.createGame(creator, gameData);

    }
    @AfterEach
    void teardown(){
        gameRepository.deleteAll();
        gameRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
    }

    @Test
    void validJoin(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        Player player = this.gameService.join(newUser, game.getGameID());
        game = gameService.getGameByGameID((game.getGameID()));

        assertEquals(newUser.getUsername(), player.getUser().getUsername());
        assertEquals(2, game.getNumberOfPlayersInLobby());
    }

    @Test
    void invalidJoin(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        Player player = this.gameService.join(newUser, game.getGameID());

        newUser = new User("newUser2", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;
        assertThrows(ResponseStatusException.class, () -> gameService.join(finalNewUser, game.getGameID()));
    }

    @Test
    void validLeave(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        Player player = this.gameService.join(newUser, game.getGameID());
        game = gameService.getGameByGameID((game.getGameID()));

        assertEquals(newUser.getUsername(), player.getUser().getUsername());
        assertEquals(2, game.getNumberOfPlayersInLobby());

        this.gameService.leave(newUser, game.getGameID());

        assertEquals(1, game.getNumberOfPlayersInLobby());
    }

    @Test
    void invalidLeave(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, () -> gameService.leave(finalNewUser, game.getGameID()));
        assertEquals(1, game.getNumberOfPlayersInLobby());
    }

    @Test
    void leaveAndDelete(){
        this.gameService.leave(creator, game.getGameID());

        List<Game> allGames = gameRepository.findAll();
        assertTrue(allGames.isEmpty());
    }

    @Test
    void findInvalidGameByID(){
        assertThrows(ResponseStatusException.class, () -> gameService.getGameByGameID(100000L));
    }

    @Test
    void failedToCreateGame(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        newUser.setStatus(UserState.PLAYING);
        userRepository.saveAndFlush(newUser);

        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, () -> gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void failedStart(){
        assertThrows(ResponseStatusException.class, () -> gameService.start(game.getGameID(), "test123"));
    }

    @Test
    void validStart(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        Player player = this.gameService.join(newUser, game.getGameID());
        game = gameService.getGameByGameID((game.getGameID()));

        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

        for(int i = 0; i < 10; i++){
            numbers.add((double) i);
            dates.add("Date" + i);
        }
        GameRound gameRound = new GameRound(new Chart(numbers, dates, currencyPair));

        game.addGameRound(gameRound);
        gameRepository.saveAndFlush(game);

        game = gameService.getGameByGameID((game.getGameID()));
        assertEquals(1, game.getGameRounds().size());

        assertDoesNotThrow(()-> gameService.start(game.getGameID(), "test123"));
    }

    @Test
    void checkValidToken(){
        assertDoesNotThrow(()-> gameService.tokenMatch(creator.getToken(), game.getGameID()));
    }

    @Test
    void checkValidTEstToken(){
        assertDoesNotThrow(()-> gameService.tokenMatch("test123", game.getGameID()));
    }

    @Test
    void checkInvalidToken(){
        assertThrows(ResponseStatusException.class, ()-> gameService.tokenMatch("12345", game.getGameID()));
    }


    @Test
    void checkInvalidStartToken(){
        assertThrows(ResponseStatusException.class, ()-> gameService.start(game.getGameID(),  "12345"));
    }

    @Test
    void validCreator(){
        Player player = this.gameService.creator(game.getGameID());
        assertEquals(creator.getUsername(), player.getUser().getUsername());
    }

    @Test
    void invalidCreator(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        Player player = this.gameService.join(newUser, game.getGameID());
        game = gameService.getGameByGameID((game.getGameID()));

        this.gameService.leave(creator, game.getGameID());
        assertThrows(ResponseStatusException.class, ()-> this.gameService.creator(game.getGameID()));
    }

    @Test
    void validChart(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        Player player = this.gameService.join(newUser, game.getGameID());
        game = gameService.getGameByGameID((game.getGameID()));

        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

        for(int i = 0; i < 10; i++){
            numbers.add((double) i);
            dates.add("Date" + i);
        }
        GameRound gameRound = new GameRound(new Chart(numbers, dates, currencyPair));

        game.addGameRound(gameRound);
        gameRepository.saveAndFlush(game);

        game = gameService.getGameByGameID((game.getGameID()));
        assertEquals(1, game.getGameRounds().size());


        game.setGameStatus(new BettingState(game));
        gameRepository.saveAndFlush(game);

        ChartData chart = this.gameService.chart(game.getGameID());
        assertNotNull(chart);
    }

    @Test
    void invalidChart(){
        assertThrows(ResponseStatusException.class, ()-> this.gameService.chart(game.getGameID()));
    }

    @Test
    void getPlayers(){
        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());

        Player player = this.gameService.join(newUser, game.getGameID());
        game = gameService.getGameByGameID((game.getGameID()));
        List<Player> players =  gameService.players(game.getGameID());

        assertEquals(creator.getUsername(), players.get(0).getUser().getUsername());
        assertEquals(newUser.getUsername(), players.get(1).getUser().getUsername());
    }

    @Test
    void allGamesNoFilter(){
        List<Game> allGames = this.gameRepository.findAll();
        List<GameData> foundGames = this.gameService.getAllGames();

        assertEquals(foundGames.size(), allGames.size());
        assertEquals(1, foundGames.size());
        assertEquals(game.getGameID(), foundGames.get(0).getGameID());
    }

    @Test
    void allGamesValidFilter(){
        List<Game> allGames = this.gameRepository.findAll();
        List<GameData> foundGames = this.gameService.getAllGames(String.valueOf(GameState.LOBBY));

        assertEquals(foundGames.size(), allGames.size());
        assertEquals(1, foundGames.size());
        assertEquals(game.getGameID(), foundGames.get(0).getGameID());
    }

    @Test
    void allGamesInValidFilter(){
        assertThrows(ResponseStatusException.class, ()-> this.gameService.getAllGames("invalid"));
    }

    @Test
    void invalidGameNameEmpty(){
        gameData.setName("");

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void invalidGameNameLength(){
        gameData.setName("Test                                               Test");

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void invalidGameNameInvalidCharacters(){
        gameData.setName("Test123%*");

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void invalidGameType(){
        gameData.setTypeOfGame(GameType.SINGLEPLAYER);

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void invalidPlayerNumberTooLow(){
        gameData.setTotalLobbySize(1);

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void invalidLobbySizeTooHigh(){
        gameData.setTotalLobbySize(1000);

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }
    @Test
    void invalidLobbySizeNegative(){
        gameData.setTotalLobbySize(-1);

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

    @Test
    void invalidNumberOfRoundsTooHigh(){
        gameData.setNumberOfRoundsToPlay(10000);

        User newUser = new User("newUser", "Pw3as?sword");
        this.userService.createUser(newUser);
        newUser = this.userService.getUserByUsername(newUser.getUsername());
        User finalNewUser = newUser;

        assertThrows(ResponseStatusException.class, ()-> this.gameService.createGame(finalNewUser, gameData));
    }

}
