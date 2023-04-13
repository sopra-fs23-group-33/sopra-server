package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToJoinException;
import ch.uzh.ifi.hase.soprafs23.exceptions.StartException;
import ch.uzh.ifi.hase.soprafs23.exceptions.endRoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.nextRoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;


public class GameOverviewTest {
    private Game game;
    private GameData gameData;
    private User creator;

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

        User second = new User("second", "password");
        Player secondPlayer = game.join(second);

    }

    private void add_gameRounds(int n){
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

        for(int i = 0; i < 10; i++){
            numbers.add(1.0);
            dates.add("Date" + i);
        }

        GameRound gameRound = new GameRound(new Chart(numbers, dates, currencyPair));

        for(int i = 0; i < n; i++){
            game.addGameRound(gameRound);
        }
    }


    @Test
    void endOfGame() throws nextRoundException, StartException, endRoundException {
        add_gameRounds(2);
        game.start();
        game.endRound();
        game.nextRound();
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        game.nextRound();
        assertEquals(GameState.OVERVIEW, game.getState());

        assertThrows(endRoundException.class, () -> game.endRound());
        assertThrows(nextRoundException.class, () -> game.nextRound());
        assertThrows(ChartException.class, () -> game.chart());
        assertThrows(StartException.class, () -> game.start());
    }

    @Test
    void leave_and_join_Overview() throws PlayerNotFoundException, StartException, endRoundException, nextRoundException {
        add_gameRounds(2);

        game.start();
        game.endRound();
        game.nextRound();
        game.endRound();
        game.nextRound();

        assertEquals(GameState.OVERVIEW, game.getState());

        User third = new User("third", "password");
        assertThrows(FailedToJoinException.class, () -> game.join(third));

        this.game.leave(creator);
        assertEquals(1, this.game.getNumberOfPlayersInLobby());
        assertEquals(1, this.game.getPlayers().size());
    }

    @Test
    void insufficient_number_of_gameRounds() throws StartException, endRoundException, nextRoundException {
        add_gameRounds(1);

        game.start();
        game.endRound();
        game.nextRound();

        assertEquals(GameState.OVERVIEW, game.getState());
        assertThrows(endRoundException.class, () -> game.endRound());
        assertThrows(nextRoundException.class, () -> game.nextRound());
    }


}
