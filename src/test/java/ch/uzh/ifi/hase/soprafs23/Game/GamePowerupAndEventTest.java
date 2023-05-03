package ch.uzh.ifi.hase.soprafs23.Game;


import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.*;
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
class GamePowerupAndEventTest {

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

        AbstractPowerUp x2P1 = new PowerupX2(p1ID, "test");
        x2P1 = this.powerupRepository.saveAndFlush(x2P1);
        player1.addPowerup(x2P1);

        AbstractPowerUp Plus100P1 = new PowerupPlus100(p1ID, "test");
        Plus100P1 = this.powerupRepository.saveAndFlush(Plus100P1);
        player1.addPowerup(Plus100P1);

        player1 = playerRepository.saveAndFlush(player1);

        AbstractPowerUp x2P2 = new PowerupX2(p2ID, "test");
        x2P2 = this.powerupRepository.saveAndFlush(x2P2);
        player2.addPowerup(x2P2);

        AbstractPowerUp X5P2 = new PowerupX5(p2ID, "test");
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

        AbstractPowerUp RBHP1 = new PowerupRobinHood(p1ID, "test");
        RBHP1 = this.powerupRepository.saveAndFlush(RBHP1);
        player1.addPowerup(RBHP1);

        AbstractPowerUp GR1 = new PowerupGuardian(p1ID, "test");
        GR1 = this.powerupRepository.saveAndFlush(GR1);
        player1.addPowerup(GR1);

        AbstractPowerUp RBHP2 = new PowerupRobinHood(p2ID, "test");
        RBHP2 = this.powerupRepository.saveAndFlush(RBHP2);
        player2.addPowerup(RBHP2);

        AbstractPowerUp GR2 = new PowerupGuardian(p2ID, "test");
        GR2 = this.powerupRepository.saveAndFlush(GR2);
        player2.addPowerup(GR2);

        AbstractPowerUp RBHP3 = new PowerupRobinHood(p3ID, "test");
        RBHP3 = this.powerupRepository.saveAndFlush(RBHP3);
        player3.addPowerup(RBHP3);

        AbstractPowerUp GR3 = new PowerupGuardian(p3ID, "test");
        GR3 = this.powerupRepository.saveAndFlush(GR3);
        player3.addPowerup(GR3);
    }

    void add_Hacker_and_CyberSecurity() {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();
        Long p3ID = player3.getPlayerID();

        AbstractPowerUp RBHP1 = new PowerupHacker(p1ID, "test");
        RBHP1 = this.powerupRepository.saveAndFlush(RBHP1);
        player1.addPowerup(RBHP1);

        AbstractPowerUp GR1 = new PowerupCyberSecurity(p1ID, "test");
        GR1 = this.powerupRepository.saveAndFlush(GR1);
        player1.addPowerup(GR1);

        AbstractPowerUp RBHP2 = new PowerupHacker(p2ID, "test");
        RBHP2 = this.powerupRepository.saveAndFlush(RBHP2);
        player2.addPowerup(RBHP2);

        AbstractPowerUp GR2 = new PowerupCyberSecurity(p2ID, "test");
        GR2 = this.powerupRepository.saveAndFlush(GR2);
        player2.addPowerup(GR2);

        AbstractPowerUp RBHP3 = new PowerupHacker(p3ID, "test");
        RBHP3 = this.powerupRepository.saveAndFlush(RBHP3);
        player3.addPowerup(RBHP3);

        AbstractPowerUp GR3 = new PowerupCyberSecurity(p3ID, "test");
        GR3 = this.powerupRepository.saveAndFlush(GR3);
        player3.addPowerup(GR3);
    }

    void add_RiskInsurance() {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();
        Long p3ID = player3.getPlayerID();

        AbstractPowerUp RiskIn1 = new PowerupRiskInsurance(p1ID, "test");
        RiskIn1 = this.powerupRepository.saveAndFlush(RiskIn1);
        player1.addPowerup(RiskIn1);

        AbstractPowerUp RiskIn2 = new PowerupRiskInsurance(p2ID, "test");
        RiskIn2 = this.powerupRepository.saveAndFlush(RiskIn2);
        player2.addPowerup(RiskIn2);

        AbstractPowerUp RiskIn3 = new PowerupRiskInsurance(p3ID, "test");
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
        assertEquals(PowerupType.ROBIN_HOOD, RobinHoodP3.getPowerupType());

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
        assertEquals(PowerupType.ROBIN_HOOD, RobinHoodP3.getPowerupType());

        AbstractPowerUp GuardianP2 = player2.getAvailablePowerups().get(1);
        assertEquals(PowerupType.GUARDIAN, GuardianP2.getPowerupType());

        AbstractPowerUp GuardianP1 = player1.getAvailablePowerups().get(1);
        assertEquals(PowerupType.GUARDIAN, GuardianP1.getPowerupType());

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
        assertEquals(PowerupType.ROBIN_HOOD, RobinHoodP3.getPowerupType());

        AbstractPowerUp RobinHoodP2 = player2.getAvailablePowerups().get(0);
        assertEquals(PowerupType.ROBIN_HOOD, RobinHoodP2.getPowerupType());

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
        assertEquals(PowerupType.ROBIN_HOOD, RobinHoodP1.getPowerupType());

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
        assertEquals(PowerupType.RISK_INSURANCE, RiskInsuranceP1.getPowerupType());

        AbstractPowerUp RiskInsuranceP3 = player3.getAvailablePowerups().get(0);
        assertEquals(PowerupType.RISK_INSURANCE, RiskInsuranceP3.getPowerupType());

        player1.activatePowerup(RiskInsuranceP1);
        player3.activatePowerup(RiskInsuranceP3);

        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000, player3.getBalance());
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
        assertEquals(PowerupType.HACKER, hackerP3.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(hackerP3);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount() - 100, player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
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
        assertEquals(PowerupType.HACKER, hackerP3.getPowerupType());

        AbstractPowerUp cyberP1 = player1.getAvailablePowerups().get(1);
        assertEquals(PowerupType.CYBER_SECURITY, cyberP1.getPowerupType());

        AbstractPowerUp cyberP2 = player2.getAvailablePowerups().get(1);
        assertEquals(PowerupType.CYBER_SECURITY, cyberP2.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(hackerP3);
        player2.activatePowerup(cyberP2);
        player1.activatePowerup(cyberP1);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());
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
        assertEquals(PowerupType.HACKER, hackerP3.getPowerupType());


        AbstractPowerUp hackerP2 = player2.getAvailablePowerups().get(0);
        assertEquals(PowerupType.HACKER, hackerP2.getPowerupType());

        game.nextRound();

        playFirstRound();
        player3.activatePowerup(hackerP3);
        player2.activatePowerup(hackerP2);
        assertEquals(GameState.BETTING, game.getState());

        game.endRound();
        assertEquals(GameState.RESULT, game.getState());

        assertEquals(balanceP1 + ratio * player1.getCurrentBet().getAmount() - 100 - 100, player1.getBalance());
        assertEquals(balanceP2 + ratio * player2.getCurrentBet().getAmount() + 100, player2.getBalance());
        assertEquals(balanceP3 - ratio * player3.getCurrentBet().getAmount() + 100, player3.getBalance());
    }


    @Test
    void powerup_INCENDIARY() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        Long p1ID = player1.getPlayerID();

        AbstractPowerUp Incend1 = new PowerupIncendiary(p1ID, "test");
        Incend1 = this.powerupRepository.saveAndFlush(Incend1);
        player1.addPowerup(Incend1);

        game.start();
        playFirstRound();

        AbstractPowerUp incendiary = player1.getAvailablePowerups().get(0);
        player1.activatePowerup(incendiary);

        game.endRound();

        List<Player> players = game.getPlayers();
        Player bankruptPlayer = game.getPlayers().get(0);

        for (Player player : players) {
            if (player.getBalance() < 1000) {
                bankruptPlayer = player;
                break;
            }
        }

        assertEquals(0, bankruptPlayer.getBalance());

    }

    @Test
    void powerup_LIFE_LINE() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        Long p1ID = player1.getPlayerID();
        Long p2ID = player2.getPlayerID();

        AbstractPowerUp lifelineP1 = new PowerupLifeLine(p1ID, "test");
        lifelineP1 = this.powerupRepository.saveAndFlush(lifelineP1);
        player1.addPowerup(lifelineP1);

        AbstractPowerUp lifelineP1V2 = new PowerupLifeLine(p1ID, "test");
        lifelineP1V2 = this.powerupRepository.saveAndFlush(lifelineP1V2);
        player1.addPowerup(lifelineP1V2);

        AbstractPowerUp lifelineP2 = new PowerupLifeLine(p2ID, "test");
        lifelineP2 = this.powerupRepository.saveAndFlush(lifelineP2);
        player2.addPowerup(lifelineP2);

        game.start();

        Bet validBet = new Bet(Direction.DOWN, 1000);
        player1.placeBet(validBet);

        assertEquals(GameState.BETTING, game.getState());
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        assertEquals(0, player1.getBalance());
        assertEquals(1000, player2.getBalance());

        game.nextRound();

        AbstractPowerUp powerup1 = player1.getAvailablePowerups().get(0);
        player1.activatePowerup(powerup1);

        AbstractPowerUp powerup2 = player1.getAvailablePowerups().get(1);
        player1.activatePowerup(powerup2);

        AbstractPowerUp powerup3 = player2.getAvailablePowerups().get(0);
        player2.activatePowerup(powerup3);

        game.endRound();

        assertEquals(500, player1.getBalance());
        assertEquals(1000, player2.getBalance());
    }

    @Test
    void event_tax() throws StartException, endRoundException {
        game.start();
        game.setEvent(Event.TAX);
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        assertEquals(Event.TAX, game.getEvent());

        assertEquals(1000 * 0.9, player1.getBalance());
        assertEquals(1000 * 0.9, player2.getBalance());
        assertEquals(1000 * 0.9, player3.getBalance());

    }

    @Test
    void event_inetrest() throws StartException, endRoundException {
        game.start();
        game.setEvent(Event.INTEREST);
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        assertEquals(Event.INTEREST, game.getEvent());

        assertEquals(1000 * 1.1, player1.getBalance());
        assertEquals(1000 * 1.1, player2.getBalance());
        assertEquals(1000 * 1.1, player3.getBalance());
    }

    @Test
    void event_stimulus() throws StartException, endRoundException {
        game.start();
        game.setEvent(Event.STIMULUS);
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        assertEquals(Event.STIMULUS, game.getEvent());

        assertEquals(1000 + 200, player1.getBalance());
        assertEquals(1000 + 200, player2.getBalance());
        assertEquals(1000 + 200, player3.getBalance());
    }

    @Test
    void event_tohuwabohu() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException {
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

        game.nextRound();
        playFirstRound();
        game.setEvent(Event.TOHUWABOHU);
        game.endRound();

        assertEquals(Event.TOHUWABOHU, game.getEvent());

        assertEquals(balanceP3, player1.getBalance());
        assertEquals(balanceP2, player2.getBalance());
        assertEquals(balanceP1, player3.getBalance());
    }
    @Test
    void event_Bailout() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {
        game.start();
        game.setEvent(Event.BAIL_OUT);

        playFirstRound();
        game.endRound();

        assertEquals(Event.BAIL_OUT, game.getEvent());

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000, player3.getBalance());
    }

    @Test
    void event_Bailout_with_Insurance() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException, PowerupNotFoundException {

        AbstractPowerUp Insurance = new PowerupRiskInsurance(player1.getPlayerID(), "test");
        player1.addPowerup(Insurance);

        game.start();
        game.setEvent(Event.BAIL_OUT);

        playFirstRound();
        AbstractPowerUp powerup = player1.getAvailablePowerups().get(0);
        player1.activatePowerup(powerup);

        game.endRound();

        assertEquals(Event.BAIL_OUT, game.getEvent());

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000, player3.getBalance());
    }

    @Test
    void event_WinnersWinMore() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException {
        game.start();
        game.setEvent(Event.WINNERS_WIN_MORE);
        playFirstRound();

        game.endRound();

        assertEquals(Event.WINNERS_WIN_MORE, game.getEvent());

        int ratio = 1;

        assertEquals(1000 + 2*ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + 2*ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());
    }

    @Test
    void event_LosersLooseMore() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException {
        game.start();
        game.setEvent(Event.LOOSERS_LOOSE_MORE);
        playFirstRound();

        game.endRound();

        assertEquals(Event.LOOSERS_LOOSE_MORE, game.getEvent());

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - 2*ratio * player3.getCurrentBet().getAmount(), player3.getBalance());
    }

    @Test
    void event_robber() throws StartException, endRoundException {
        game.start();
        game.setEvent(Event.ROBBER);
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        assertEquals(Event.ROBBER, game.getEvent());

        assertEquals(1000 - 200, player1.getBalance());
        assertEquals(1000 - 200, player2.getBalance());
        assertEquals(1000 - 200, player3.getBalance());
    }

    @Test
    void event_back_to_roots() throws StartException, endRoundException, FailedToPlaceBetException, nextRoundException {
        game.start();
        playFirstRound();
        game.endRound();

        int ratio = 1;

        assertEquals(1000 + ratio * player1.getCurrentBet().getAmount(), player1.getBalance());
        assertEquals(1000 + ratio * player2.getCurrentBet().getAmount(), player2.getBalance());
        assertEquals(1000 - ratio * player3.getCurrentBet().getAmount(), player3.getBalance());

        game.nextRound();
        playFirstRound();
        game.setEvent(Event.BACKTOROOTS);
        game.endRound();

        assertEquals(Event.BACKTOROOTS, game.getEvent());

        assertEquals(1000, player1.getBalance());
        assertEquals(1000, player2.getBalance());
        assertEquals(1000, player3.getBalance());
    }

    @Test
    void powerupRespawn() throws StartException, endRoundException, nextRoundException {
        assertEquals(0, player1.getAvailablePowerups().size());
        game.start();
        game.endRound();
        game.nextRound();
        assertEquals(2, player1.getAvailablePowerups().size());

    }

    @Test
    void powerupRespawnOff() throws StartException, endRoundException, nextRoundException {
        game.setPowerupsActive(false);

        assertEquals(0, player1.getAvailablePowerups().size());
        game.start();
        game.endRound();
        game.nextRound();
        assertEquals(0, player1.getAvailablePowerups().size());

    }


}
