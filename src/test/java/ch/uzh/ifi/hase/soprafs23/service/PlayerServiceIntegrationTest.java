package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
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

import static org.junit.jupiter.api.Assertions.*;

@WebAppConfiguration
@SpringBootTest
@Transactional
public class PlayerServiceIntegrationTest {
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

    @BeforeEach
    void setup()  {
        gameRepository.deleteAll();
        gameRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();

        User creator = new User("Creator", "Pw3as?sword");
        this.userService.createUser(creator);


        GameData gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(2);
        gameData.setTypeOfGame(GameType.MULTIPLAYER);
        gameData.setPowerupsActive(false);
        gameData.setEventsActive(false);
        gameData.setName("GameRoom");
        gameData.setTotalLobbySize(3);

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
    void getPlayerByID() throws PlayerNotFoundException {

        Player firstPlayer = game.creator();

        Long playerID = firstPlayer.getPlayerID();
        Player foundPlayer = this.playerService.getPlayerByPlayerID(playerID);

        assertEquals(firstPlayer, foundPlayer);
    }


    @Test
    void getPlayerByIDInvalid(){
        assertThrows(ResponseStatusException.class, () -> this.playerService.getPlayerByPlayerID(10000000L));
    }


    @Test
    void placeInvalidBets() throws PlayerNotFoundException {
        Long playerID = game.creator().getPlayerID();

        Bet negativeBet = new Bet(Direction.DOWN, -10);
        Bet zeroBet = new Bet(Direction.DOWN, 0);
        Bet noneBet = new  Bet(Direction.NONE, 10);
        Bet highVolumeBet =  new Bet(Direction.UP, 100000);

        assertThrows(ResponseStatusException.class, () -> this.playerService.placeBet(negativeBet, playerID));
        assertThrows(ResponseStatusException.class, () -> this.playerService.placeBet(zeroBet, playerID));
        assertThrows(ResponseStatusException.class, () -> this.playerService.placeBet(noneBet, playerID));
        assertThrows(ResponseStatusException.class, () -> this.playerService.placeBet(highVolumeBet, playerID));

    }

    @Test
    void placeValidBets() throws PlayerNotFoundException {
        Long playerID = game.creator().getPlayerID();

        Bet validBet = new Bet(Direction.UP, 100);

        assertDoesNotThrow(() -> this.playerService.placeBet(validBet, playerID));

        Player player = this.playerService.getPlayerByPlayerID(playerID);
        assertEquals(validBet.getAmount(), player.getCurrentBet().getAmount());
        assertEquals(validBet.getDirection(), player.getCurrentBet().getDirection());

    }

    @Test
    void getResult() throws PlayerNotFoundException {
        Long playerID = game.creator().getPlayerID();

        Player player = this.playerService.getPlayerByPlayerID(playerID);
        Result result = this.playerService.getResult(player.getPlayerID());


        assertEquals(result.getBettingAmount(), player.getResult().getBettingAmount());
        assertEquals(result.getProfit(), player.getResult().getProfit());
        assertEquals(result.getOutcome(), player.getResult().getOutcome());

    }

    @Test
    void checkToken() throws PlayerNotFoundException {
        Long playerID = game.creator().getPlayerID();

        Player player = this.playerService.getPlayerByPlayerID(playerID);
        String token = player.getUser().getToken();

        assertThrows(ResponseStatusException.class, () -> this.playerService.tokenMatch("12345", playerID));
        assertDoesNotThrow(() -> this.playerService.tokenMatch(token, playerID));
        assertDoesNotThrow(() -> this.playerService.tokenMatch("test123", playerID));
    }




}
