package ch.uzh.ifi.hase.soprafs23.Game;


import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.Powerups.*;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import ch.uzh.ifi.hase.soprafs23.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

@DataJpaTest
public class GamePowerupTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PowerupRepository powerupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameRoundRepository gameRoundRepository;

    private Game game;
    private GameData gameData;
    private User creator;
    private User second;

    private User third;

    private Player player1;

    private Player player2;

    private Player player3;

    @BeforeEach
    void setup_for_Betting_State() throws FailedToJoinException, StartException, endRoundException, nextRoundException, PlayerNotFoundException {

        gameRepository.deleteAll();
        gameRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        powerupRepository.deleteAll();
        powerupRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();


        creator = new User("creator", "password");
        creator = userRepository.saveAndFlush(creator);

        gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(3);
        gameData.setTypeOfGame(GameType.MULTIPLAYER);
        gameData.setPowerupsActive(true);
        gameData.setEventsActive(false);
        gameData.setName("GameRoom");
        gameData.setTotalLobbySize(5);

        game = new Game(creator, gameData);
        game.init();

        second = new User("second", "password");
        second = userRepository.saveAndFlush(second);
        player2 = game.join(second);

        third = new User("third", "password");
        third = userRepository.saveAndFlush(third);
        player3 = game.join(third);

        add_gameRound();
        add_gameRound();
        add_gameRound();

        game = gameRepository.saveAndFlush(game);

        player1 = game.creator();

    }

    private void add_gameRound() {
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF, Currency.EUR);

        for (int i = 0; i < 10; i++) {
            numbers.add((double) 1);
            dates.add("Date" + i);
        }

        //ratio is 1.0
        //outcome is UP

        GameRound gameRound = new GameRound(new Chart(numbers, dates, currencyPair));
        gameRound = gameRoundRepository.saveAndFlush(gameRound);

        game.addGameRound(gameRound);
        game = gameRepository.saveAndFlush(game);
    }

    private void placeBetsAllWinning() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);

        player1.placeBet(validBet);
        player1 = playerRepository.saveAndFlush(player1);

        player2.placeBet(validBet);
        player2 = playerRepository.saveAndFlush(player2);
    }

    private void addPowerupsBasic() {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();

        AbstractPowerUp x2P1 = new PowerupX2(p1ID);
        x2P1 = this.powerupRepository.saveAndFlush(x2P1);
        player1.addPowerup(x2P1);

        AbstractPowerUp Plus100P1 = new PowerupPlus100(p1ID);
        Plus100P1 = this.powerupRepository.saveAndFlush(Plus100P1);
        player1.addPowerup(Plus100P1);

        player1 = playerRepository.saveAndFlush(player1);

        AbstractPowerUp x2P2 = new PowerupX2(p2ID);
        x2P2 = this.powerupRepository.saveAndFlush(x2P2);
        player2.addPowerup(x2P2);

        AbstractPowerUp X5P2 = new PowerupX5(p2ID);
        X5P2 = this.powerupRepository.saveAndFlush(X5P2);
        player2.addPowerup(X5P2);

        player2 = playerRepository.saveAndFlush(player2);

    }

    void actiavatePowerups() throws PowerupNotFoundException {
        List<AbstractPowerUp> powerups = player1.getAvailablePowerups();

        for (AbstractPowerUp powerUp : powerups) {
            player1.activatePowerup(powerUp);
        }

        player1 = playerRepository.saveAndFlush(player1);

        powerups = player2.getAvailablePowerups();

        for (AbstractPowerUp powerUp : powerups) {
            player2.activatePowerup(powerUp);
        }

        player2 = playerRepository.saveAndFlush(player2);
    }

    void playFirstRound() throws FailedToPlaceBetException {
        Bet betP1 = new Bet(Direction.UP, 200);
        Bet betP2 = new Bet(Direction.UP, 100);
        Bet betP3 = new Bet(Direction.DOWN, 100);

        player1.placeBet(betP1);
        player1 = playerRepository.saveAndFlush(player1);

        player2.placeBet(betP2);
        player2 = playerRepository.saveAndFlush(player2);

        player3.placeBet(betP3);
        player3 = playerRepository.saveAndFlush(player3);

    }

    void add_RobinHood_and_Guardian() {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();
        Long p3ID = player3.getPlayerID();

        AbstractPowerUp RBHP1 = new PowerupRobinHood(p1ID);
        RBHP1 = this.powerupRepository.saveAndFlush(RBHP1);
        player1.addPowerup(RBHP1);

        AbstractPowerUp GR1 = new PowerupGuardian(p1ID);
        GR1 = this.powerupRepository.saveAndFlush(GR1);
        player1.addPowerup(GR1);

        AbstractPowerUp RBHP2 = new PowerupRobinHood(p2ID);
        RBHP2 = this.powerupRepository.saveAndFlush(RBHP2);
        player2.addPowerup(RBHP2);

        AbstractPowerUp GR2 = new PowerupGuardian(p2ID);
        GR2 = this.powerupRepository.saveAndFlush(GR2);
        player2.addPowerup(GR2);

        AbstractPowerUp RBHP3 = new PowerupRobinHood(p3ID);
        RBHP3 = this.powerupRepository.saveAndFlush(RBHP3);
        player3.addPowerup(RBHP3);

        AbstractPowerUp GR3 = new PowerupGuardian(p3ID);
        GR3 = this.powerupRepository.saveAndFlush(GR3);
        player3.addPowerup(GR3);
    }

    void add_Hacker_and_CyberSecurity() {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();
        Long p3ID = player3.getPlayerID();

        AbstractPowerUp RBHP1 = new PowerupHacker(p1ID);
        RBHP1 = this.powerupRepository.saveAndFlush(RBHP1);
        player1.addPowerup(RBHP1);

        AbstractPowerUp GR1 = new PowerupCyberSecurity(p1ID);
        GR1 = this.powerupRepository.saveAndFlush(GR1);
        player1.addPowerup(GR1);

        AbstractPowerUp RBHP2 = new PowerupHacker(p2ID);
        RBHP2 = this.powerupRepository.saveAndFlush(RBHP2);
        player2.addPowerup(RBHP2);

        AbstractPowerUp GR2 = new PowerupCyberSecurity(p2ID);
        GR2 = this.powerupRepository.saveAndFlush(GR2);
        player2.addPowerup(GR2);

        AbstractPowerUp RBHP3 = new PowerupHacker(p3ID);
        RBHP3 = this.powerupRepository.saveAndFlush(RBHP3);
        player3.addPowerup(RBHP3);

        AbstractPowerUp GR3 = new PowerupCyberSecurity(p3ID);
        GR3 = this.powerupRepository.saveAndFlush(GR3);
        player3.addPowerup(GR3);
    }

    void add_RiskInsurance() {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();
        Long p3ID = player3.getPlayerID();

        AbstractPowerUp RiskIn1 = new PowerupRiskInsurance(p1ID);
        RiskIn1 = this.powerupRepository.saveAndFlush(RiskIn1);
        player1.addPowerup(RiskIn1);

        AbstractPowerUp RiskIn2 = new PowerupRiskInsurance(p2ID);
        RiskIn2 = this.powerupRepository.saveAndFlush(RiskIn2);
        player2.addPowerup(RiskIn2);

        AbstractPowerUp RiskIn3 = new PowerupRiskInsurance(p3ID);
        RiskIn3 = this.powerupRepository.saveAndFlush(RiskIn3);
        player3.addPowerup(RiskIn3);
    }

    @AfterEach
    void teardown() {
        gameRepository.deleteAll();
        gameRepository.flush();
        userRepository.deleteAll();
        userRepository.flush();
        powerupRepository.deleteAll();
        powerupRepository.flush();
        playerRepository.deleteAll();
        playerRepository.flush();
    }

    @Test
    void powerupsBasic() throws FailedToPlaceBetException, StartException, endRoundException, PowerupNotFoundException, nextRoundException {
        addPowerupsBasic();
        game.start();

        placeBetsAllWinning();
        actiavatePowerups();

        game.endRound();

        int ratio = 1;

        assertEquals(100 + 1000 + ratio * 100 * 2, player1.getBalance());
        assertEquals(1000 + ratio * 100 * 2 * 5, player2.getBalance());
        assertEquals(1000, player3.getBalance());

    }


    @Test
    void powerup_RobinHood_without_Guardian() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_RobinHood_and_Guardian();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp RobinHoodP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RobinHood, RobinHoodP3.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(RobinHoodP3);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount() - 240, player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount() + 120, player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount() + 120, player3.getBalance());

    }

    @Test
    void powerup_RobinHood_with_Guardian() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_RobinHood_and_Guardian();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp RobinHoodP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RobinHood, RobinHoodP3.getPowerupType());

        AbstractPowerUp GuardianP2 = player2.getAvailablePowerups().get(1);
        assertEquals(PowerupType.Guardian, GuardianP2.getPowerupType());

        AbstractPowerUp GuardianP1 = player1.getAvailablePowerups().get(1);
        assertEquals(PowerupType.Guardian, GuardianP1.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(RobinHoodP3);
        player2.activatePowerup(GuardianP2);
        player1.activatePowerup(GuardianP1);

        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

    }

    @Test
    void powerup_two_RobinHood_without_Guardian() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_RobinHood_and_Guardian();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp RobinHoodP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RobinHood, RobinHoodP3.getPowerupType());

        AbstractPowerUp RobinHoodP2 = player2.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RobinHood, RobinHoodP2.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(RobinHoodP3);
        player2.activatePowerup(RobinHoodP2);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount() - 240 - 240, player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount() + 120 + 120, player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount() + 120 + 120, player3.getBalance());
    }

    @Test
    void powerup_RobinHood_without_Guardian_activated_as_bestPlayer() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_RobinHood_and_Guardian();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp RobinHoodP1 = player1.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RobinHood, RobinHoodP1.getPowerupType());

        game.nextRound();

        playFirstRound();
        player1.activatePowerup(RobinHoodP1);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

    }

    @Test
    void powerup_RiskInsurance() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_RiskInsurance();
        game.start();
        playFirstRound();

        AbstractPowerUp RiskInsuranceP1 = player1.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RiskInsurance, RiskInsuranceP1.getPowerupType());

        AbstractPowerUp RiskInsuranceP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RiskInsurance, RiskInsuranceP3.getPowerupType());

        player1.activatePowerup(RiskInsuranceP1);
        player3.activatePowerup(RiskInsuranceP3);

        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 , player3.getBalance());
    }

    @Test
    void powerup_Hacker_without_cyberSecurity() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_Hacker_and_CyberSecurity();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp hackerP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.Hacker, hackerP3.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(hackerP3);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount() - 100, player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount() , player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount() + 100, player3.getBalance());
    }

    @Test
    void powerup_Hacker_with_cyberSecurity() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_Hacker_and_CyberSecurity();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp hackerP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.Hacker, hackerP3.getPowerupType());

        AbstractPowerUp cyberP1 = player1.getAvailablePowerups().get(1);
        assertEquals(PowerupType.CyberSecurity, cyberP1.getPowerupType());

        AbstractPowerUp cyberP2 = player2.getAvailablePowerups().get(1);
        assertEquals(PowerupType.CyberSecurity, cyberP2.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(hackerP3);
        player2.activatePowerup(cyberP2);
        player1.activatePowerup(cyberP1);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount() , player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount() , player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount() , player3.getBalance());
    }

    @Test
    void powerup_two_Hacker_without_cyberSecurity() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        add_Hacker_and_CyberSecurity();
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        int balanceP1 = player1.getBalance();
        int balanceP2 = player2.getBalance();
        int balanceP3 = player3.getBalance();

        AbstractPowerUp hackerP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.Hacker, hackerP3.getPowerupType());


        AbstractPowerUp hackerP2 = player2.getAvailablePowerups().get(0);
        assertEquals(PowerupType.Hacker, hackerP2.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(hackerP3);
        player2.activatePowerup(hackerP2);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount()-100-100 , player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount() +100, player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount() +100, player3.getBalance());
    }
}
