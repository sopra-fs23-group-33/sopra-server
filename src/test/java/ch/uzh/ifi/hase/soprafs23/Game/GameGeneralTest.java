package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

class GameGeneralTest {
    private Game game;
    private GameData gameData;
    private User creator;

    private User second;
    private Player secondPlayer;

    @BeforeEach
    void setup_for_Overview_State() throws FailedToJoinException, StartException, endRoundException, nextRoundException {
        creator = new User("creator", "password");
        gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(2);
        gameData.setTypeOfGame(GameType.MULTIPLAYER);
        gameData.setPowerupsActive(false);
        gameData.setEventsActive(false);
        gameData.setName("GameRoom");
        gameData.setTotalLobbySize(3);

        game = new Game(creator, gameData);
        game.init();

        second = new User("second", "password");
        secondPlayer = game.join(second);

    }

    private void add_gameRounds(int n){
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

        for(int i = 0; i < 10; i++){
            numbers.add((double) i);
            dates.add("Date" + i);
        }

        GameRound gameRound = new GameRound(new Chart(numbers, dates, currencyPair));

        for(int i = 0; i < n; i++){
            game.addGameRound(gameRound);
        }
    }

    @Test
    void add_too_many_gameRounds(){

        assertEquals(0, game.getGameRounds().size());
        add_gameRounds(2);
        assertEquals(2, game.getGameRounds().size());
        add_gameRounds(1);
        assertEquals(2, game.getGameRounds().size());
    }

    @Test
    void all_bets_placed() throws StartException, PlayerNotFoundException, endRoundException, FailedToPlaceBetException {
        add_gameRounds(2);

        assertFalse(game.allBetsPlaced());

        game.start();

        Bet validBet = new Bet(Direction.UP, 100);
        secondPlayer.placeBet(validBet);

        assertFalse(game.allBetsPlaced());

        Player creator = game.creator();
        creator.placeBet(validBet);

        assertTrue(game.allBetsPlaced());
    }

    @Test
    void all_bets_placed_leave() throws StartException, PlayerNotFoundException, endRoundException, FailedToPlaceBetException {
        add_gameRounds(2);

        assertFalse(game.allBetsPlaced());

        game.start();

        Bet validBet = new Bet(Direction.UP, 100);
        secondPlayer.placeBet(validBet);

        assertFalse(game.allBetsPlaced());

        game.leave(game.getCreator());

        assertTrue(game.allBetsPlaced());
    }

    @Test
    void getters_and_setters(){
        Game game = new Game();

        game.setGameStatus(new LobbyState(game));
        game.setGameID(1L);
        game.setTimer(10);
        game.setName("Name");
        game.setCreator(new User("Test", "pwd"));
        game.setEventsActive(false);
        game.setNumberOfRoundsToPlay(10);
        game.setPlayers(new ArrayList<>());
        game.setGameRounds(new ArrayList<>());
        game.setPowerupsActive(false);
        game.setTotalLobbySize(10);
        game.setType(GameType.MULTIPLAYER);

        assertEquals(1L, game.getGameID());
        assertEquals(10, game.getTimer());
        assertEquals("Name", game.getName());
        assertEquals("Test", game.getCreator().getUsername());
        assertFalse(game.isEventsActive());
        assertFalse(game.isPowerupsActive());
        assertEquals(10, game.getNumberOfRoundsToPlay());
        assertTrue(game.getPlayers().isEmpty());
        assertTrue(game.getGameRounds().isEmpty());
        assertEquals(GameType.MULTIPLAYER, game.getType());
    }

}
