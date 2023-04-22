package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameResultTest {
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
        game.join(second);

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
    void leave_and_join_Result() throws PlayerNotFoundException, StartException, endRoundException, nextRoundException {
        add_gameRounds(2);

        game.start();
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());

        User third = new User("third", "password");
        assertThrows(FailedToJoinException.class, () -> game.join(third));

        this.game.leave(creator);
        assertEquals(1, this.game.getNumberOfPlayersInLobby());
        assertEquals(1, this.game.getPlayers().size());
    }

    @Test
    void chart_result()throws StartException, endRoundException, ChartException {
        add_gameRounds(2);

        game.start();
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());

        Chart chart = game.chart();
        assertEquals(chart.getValues().size(), chart.getDates().size());
        assertEquals(0.0, chart.getValues().get(0));
        assertEquals(9.0, chart.getValues().get(9));

        assertEquals("Date0", chart.getDates().get(0));
        assertEquals("Date9", chart.getDates().get(9));
    }

    @Test
    void next_round_simple() throws StartException, endRoundException, nextRoundException {
        add_gameRounds(2);

        game.start();
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        game.nextRound();
        assertEquals(GameState.BETTING, game.getState());
    }

    @Test
    void next_round_insufficient_rounds() throws StartException, endRoundException, nextRoundException {
        add_gameRounds(1);

        game.start();
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        game.nextRound();
        assertEquals(GameState.OVERVIEW, game.getState());
    }

    @Test
    void next_round_insufficient_players() throws StartException, endRoundException, nextRoundException, PlayerNotFoundException {
        add_gameRounds(2);

        game.start();
        game.endRound();

        assertEquals(GameState.RESULT, game.getState());
        game.leave(creator);
        game.nextRound();
        assertEquals(GameState.OVERVIEW, game.getState());
    }

    @Test
    void next_round_reset_bets() throws StartException, endRoundException, nextRoundException, PlayerNotFoundException, FailedToPlaceBetException {
        add_gameRounds(2);

        game.start();
        Player player = game.creator();

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);
        assertEquals(validBet, player.getCurrentBet());
        game.endRound();
        assertEquals(validBet, player.getCurrentBet());
        game.nextRound();
        assertEquals(0, player.getCurrentBet().getAmount());
        assertEquals(Direction.NONE, player.getCurrentBet().getDirection());
    }
    @Test
    void update_timer_ends_Result() throws endRoundException, nextRoundException, StartException {
        add_gameRounds(2);
        game.start();
        game.endRound();
        assertEquals(GameState.RESULT, game.getState());


        int resultTime = game.getResultTime();
        for (int i = 0; i < resultTime; i++) {
            assertEquals(resultTime - i, game.getTimer());
            assertEquals(GameState.RESULT, game.getState());
            game.update();
        }
        assertEquals(GameState.BETTING, game.getState());
        assertEquals(game.getBettingTime(), game.getTimer());
    }

}
