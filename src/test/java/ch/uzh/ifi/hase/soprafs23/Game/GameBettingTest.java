package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.*;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class GameBettingTest {
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
    void state_betting() throws StartException, endRoundException, nextRoundException {
        add_gameRounds(2);
        assertEquals(0, game.getCurrentRoundPlayed());
        game.start();

        assertEquals(GameState.BETTING, game.getState());
        assertEquals(1, game.getCurrentRoundPlayed());
        game.endRound();
        game.nextRound();
        assertEquals(GameState.BETTING, game.getState());
        assertEquals(2, game.getCurrentRoundPlayed());
    }
    @Test
    void join_betting() throws StartException {
        add_gameRounds(2);
        game.start();

        User third = new User("third", "password");
        assertThrows(FailedToJoinException.class, () -> game.join(third));
    }

    @Test
    void chart_betting() throws StartException, ChartException {
        add_gameRounds(1);
        game.start();

        Chart chart = game.chart();
        assertEquals(chart.getValues().size(), chart.getDates().size());
        assertEquals(0.0, chart.getValues().get(0));
        assertEquals(3.0, chart.getValues().get(3));

        assertEquals("Date0", chart.getDates().get(0));
        assertEquals("Date3", chart.getDates().get(3));
    }

    @Test
    void leave_betting() throws StartException, PlayerNotFoundException, endRoundException {
        add_gameRounds(2);

        assertEquals(2, game.getNumberOfPlayersInLobby());
        assertEquals(2, game.players.size());

        game.start();
        assertEquals(GameState.BETTING, game.getState());

        assertEquals(UserState.PLAYING, second.getState());
        game.leave(second);
        assertEquals(UserState.PLAYING, second.getState());

        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(2, game.players.size());

        game.endRound();
        assertEquals(UserState.ONLINE, second.getState());

        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(1, game.players.size());
    }

    @Test
    void leave_logout_betting() throws StartException, PlayerNotFoundException, endRoundException {
        add_gameRounds(2);

        game.start();

        assertEquals(UserState.PLAYING, second.getState());
        second.setStatus(UserState.OFFLINE);
        game.leave(second);
        assertEquals(UserState.OFFLINE, second.getState());

        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(2, game.players.size());


        game.endRound();
        assertEquals(UserState.OFFLINE, second.getState());

        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(1, game.players.size());
    }


}
