package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Powerups.AbstractPowerUp;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;

import javax.transaction.Transactional;
import java.util.List;

@WebAppConfiguration
@SpringBootTest
@Transactional
public class PlayerServiceIntergationWithPowerupsTest {
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
    private GameRoundRepository gameRoundRepository;

    @Autowired
    private GameService gameService;

    @Autowired
    private PlayerService playerService;

    private Game game;
    private GameData gameData;
    private User creator;


    @BeforeEach
    void setup() throws InterruptedException {
        gameRepository.deleteAll();
        gameRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        gameRepository.deleteAll();
        gameRepository.flush();

        creator = new User("Creator", "Pw3as?sword");
        this.userService.createUser(creator);


        gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(5);
        gameData.setTypeOfGame(GameType.MULTIPLAYER);
        gameData.setPowerupsActive(true);
        gameData.setEventsActive(false);
        gameData.setName("GameRoom");
        gameData.setTotalLobbySize(3);

        creator = this.userService.getUserByUsername(creator.getUsername());

        game = this.gameService.createGame(creator, gameData);
        Thread.sleep(200);
    }

    @AfterEach
    void teardown() {
        gameRepository.deleteAll();
        gameRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        gameRepository.deleteAll();
        gameRepository.flush();
    }

    @Test
    void checkPowerupsCreated() throws PlayerNotFoundException {
        Game newGame = gameService.getGameByGameID(game.getGameID());
        Player p = newGame.creator();

        List<AbstractPowerUp> powerups = playerService.getPowerups(p.getPlayerID());
        assertEquals(5, powerups.size());
    }

    @Test
    void activatePowerups() throws PlayerNotFoundException {
        Game newGame = gameService.getGameByGameID(game.getGameID());
        Player p = newGame.creator();

        List<AbstractPowerUp> powerups = playerService.getPowerups(p.getPlayerID());
        assertEquals(5, powerups.size());

        AbstractPowerUp powerup = powerups.get(0);

        Player finalP = p;

        assertDoesNotThrow(() -> playerService.activatePowerup(powerup.getPowerupID(), finalP.getPlayerID()));

        newGame = gameService.getGameByGameID(game.getGameID());
        p = newGame.creator();

        assertEquals(1, p.getActivePowerups().size());
    }

    @Test
    void activatePowerupsFailed() throws PlayerNotFoundException {
        Game newGame = gameService.getGameByGameID(game.getGameID());
        Player p = newGame.creator();

        List<AbstractPowerUp> powerups = playerService.getPowerups(p.getPlayerID());
        assertEquals(5, powerups.size());

        AbstractPowerUp powerup = powerups.get(0);

        Player finalP = p;

        assertThrows(ResponseStatusException.class, () -> playerService.activatePowerup(powerup.getPowerupID() + 1000, finalP.getPlayerID()));

        newGame = gameService.getGameByGameID(game.getGameID());
        p = newGame.creator();

        assertEquals(0, p.getActivePowerups().size());
    }

    @Test
    void foreignPowerup() throws PlayerNotFoundException {
        User second = new User("second", "Pw3as?sword");
        second = this.userService.createUser(second);

        Player p2 = gameService.join(second, game.getGameID());

        List<AbstractPowerUp> powerups = playerService.getPowerups(p2.getPlayerID());
        assertEquals(5, powerups.size());

        AbstractPowerUp foreignPowerup = powerups.get(0);

        Player p = game.creator();
        assertThrows(ResponseStatusException.class, () -> playerService.activatePowerup(foreignPowerup.getPowerupID(), p.getPlayerID()));

    }
}
