package ch.uzh.ifi.hase.soprafs23.entity;



import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class PlayerTest {

    private User user;

    private Player player;

    @BeforeEach
    void setup() {
        user = new User("creator", "password");
        player = new Player(user);
        player.init();
    }


    @Test
    void initialization(){
        assertEquals(user, player.getUser());

        Bet bet = player.getCurrentBet();
        Result result = player.getResult();

        assertEquals(Direction.NONE, bet.getDirection());
        assertEquals(0, bet.getAmount());
        assertEquals(0, result.getBettingAmount());
        assertEquals(Direction.NONE, result.getOutcome());
        assertEquals(0, result.getProfit());

        assertEquals(1000, player.getBalance());
        assertEquals(PlayerState.ACTIVE, player.getState());
    }

    @Test
    void initial_playerData(){
        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(1000, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(0, playerData.getNumberOfLostRounds());
        assertEquals(Direction.NONE, playerData.getTypeOfCurrentBet());
    }

    @Test
    void place_bet() throws FailedToPlaceBetException {
        Bet negativeBet = new Bet(Direction.DOWN, -10);
        Bet zeroBet = new Bet(Direction.DOWN, 0);
        Bet noneBet = new  Bet(Direction.NONE, 10);
        Bet highVolumeBet =  new Bet(Direction.UP, 100000);
        Bet validBet = new Bet(Direction.UP, 100);

        assertThrows(FailedToPlaceBetExceptionBecauseNegative.class, () -> player.placeBet(negativeBet));
        assertThrows(FailedToPlaceBetExceptionBecauseNegative.class, () -> player.placeBet(zeroBet));
        assertThrows(FailedToPlaceBetExceptionBecauseDirection.class, () -> player.placeBet(noneBet));
        assertThrows(FailedToPlaceBetExceptionBecauseBalance.class, () -> player.placeBet(highVolumeBet));

        player.placeBet(validBet);

        assertEquals(validBet.getDirection(), player.getCurrentBet().getDirection());
        assertEquals(validBet.getAmount(), player.getCurrentBet().getAmount());

        player.setState(PlayerState.INACTIVE);
        assertThrows(FailedToPlaceBetExceptionBecauseInactive.class, () -> player.placeBet(validBet));
    }

    @Test
    void reset_bet() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        assertEquals(validBet.getDirection(), player.getCurrentBet().getDirection());
        assertEquals(validBet.getAmount(), player.getCurrentBet().getAmount());

        player.resetBet();

        assertEquals(Direction.NONE, player.getCurrentBet().getDirection());
        assertEquals(0, player.getCurrentBet().getAmount());

    }

    @Test
    void equals(){
        Player otherPlayer = new Player(user);
        otherPlayer.init();

        User anotherUser = new User("test", "pwd");

        Player anotherPlayer = new Player(anotherUser);
        anotherPlayer.init();

        assertEquals(otherPlayer, player);
        assertNotEquals(anotherPlayer, player);
        assertNotEquals(null, player);
        assertEquals(player, player);
        assertNotEquals(new User(), player);

        assertEquals(otherPlayer.hashCode(), player.hashCode());
    }

    @Test
    void endRound_win() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);

        player.placeBet(validBet);
        player.endRound(Direction.UP, 1.01);
        Result result = player.getResult();

        assertEquals(100, result.getBettingAmount());
        assertEquals(Direction.UP, result.getOutcome());
        assertEquals(200, result.getProfit());

        assertEquals(1200, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(1200, playerData.getAccountBalance());
        assertEquals(1, playerData.getNumberOfWonRounds());
        assertEquals(0, playerData.getNumberOfLostRounds());
        assertEquals(Direction.UP, playerData.getTypeOfCurrentBet());
    }


    @Test
    void endRound_lost() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);

        player.placeBet(validBet);
        player.endRound(Direction.DOWN, 1.01);
        Result result = player.getResult();

        assertEquals(100, result.getBettingAmount());
        assertEquals(Direction.DOWN, result.getOutcome());
        assertEquals(-200, result.getProfit());

        assertEquals(800, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(800, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(1, playerData.getNumberOfLostRounds());
        assertEquals(Direction.UP, playerData.getTypeOfCurrentBet());
    }

    @Test
    void endRound_lost_more_than_zero() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 1000);

        player.placeBet(validBet);
        player.endRound(Direction.DOWN, 1.01);
        Result result = player.getResult();

        assertEquals(1000, result.getBettingAmount());
        assertEquals(Direction.DOWN, result.getOutcome());
        assertEquals(-1000, result.getProfit());

        assertEquals(0, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(0, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(1, playerData.getNumberOfLostRounds());
        assertEquals(Direction.UP, playerData.getTypeOfCurrentBet());
    }

    @Test
    void endRound_zeroBet() throws FailedToPlaceBetException {

        player.endRound(Direction.DOWN, 1.01);
        Result result = player.getResult();

        assertEquals(0, result.getBettingAmount());
        assertEquals(Direction.DOWN, result.getOutcome());
        assertEquals(0, result.getProfit());

        assertEquals(1000, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(1000, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(0, playerData.getNumberOfLostRounds());
        assertEquals(Direction.NONE, playerData.getTypeOfCurrentBet());
    }
}
