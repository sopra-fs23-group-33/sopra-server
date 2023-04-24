package ch.uzh.ifi.hase.soprafs23.Game;

import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.CurrencyPair;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class GameLobbyTestMultiplayer {


    private Game game;

    private GameData gameData;

    private User creator;

    @BeforeEach
    void setup_Multiplayer(){
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
    }

    @Test
    void initialization(){
        GameData gameData = this.game.status();

        assertEquals(creator, game.getCreator());
        assertEquals(GameState.LOBBY, game.getState());
        assertEquals("GameRoom", gameData.getName());
        assertEquals(GameType.MULTIPLAYER, gameData.getTypeOfGame());
        assertFalse(gameData.isEventsActive());
        assertFalse(gameData.isPowerupsActive());
        assertEquals(0, gameData.getTimer());
        assertEquals(3, gameData.getTotalLobbySize());
        assertEquals(1, gameData.getNumberOfPlayersInLobby());
        assertEquals(0, gameData.getCurrentRoundPlayed());
        assertEquals("none", gameData.getEvent());

    }

    @Test
    void leave_and_join_Lobby() throws FailedToJoinException, PlayerNotFoundException {
        User userToJoin = new User("test", "pwd");
        Player newPlayer = game.join(userToJoin);

        assertEquals(newPlayer, game.getPlayers().get(1));

        game.leave(userToJoin);

        assertEquals(creator, game.getPlayers().get(0).getUser());
        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(1, game.getPlayers().size());

        assertEquals(UserStatus.ONLINE, userToJoin.getStatus());
    }

    @Test
    void leave_and_join__offline_Lobby() throws FailedToJoinException, PlayerNotFoundException {
        User userToJoin = new User("test", "pwd");
        Player newPlayer = game.join(userToJoin);

        assertEquals(newPlayer, game.getPlayers().get(1));
        assertEquals(UserStatus.PLAYING, userToJoin.getStatus());


        userToJoin.setStatus(UserStatus.OFFLINE);
        game.leave(userToJoin);
        assertEquals(UserStatus.OFFLINE, userToJoin.getStatus());

        assertEquals(creator, game.getPlayers().get(0).getUser());
        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(1, game.getPlayers().size());
    }

    @Test
    void failed_to_join() throws FailedToJoinException {
        User userToJoin = new User("test", "pwd");
        User userToJoin2 = new User("test2", "pwd");
        User userToJoin3 = new User("test3", "pwd");
        User userToJoin4 = new User("test4", "pwd");


        game.join(userToJoin);
        game.join(userToJoin2);
        assertEquals(3, game.getPlayers().size());

        assertThrows(FailedToJoinExceptionBecauseLobbyFull.class, () -> game.join(userToJoin3));

        userToJoin4.setStatus(UserStatus.PLAYING);
        assertThrows(FailedToJoinExceptionBecauseOffline.class, () -> game.join(userToJoin4));

        userToJoin4.setStatus(UserStatus.OFFLINE);
        assertThrows(FailedToJoinExceptionBecauseOffline.class, () -> game.join(userToJoin4));
    }

    @Test
    void leave_host_Lobby() throws PlayerNotFoundException, FailedToJoinException {
        User userToJoin = new User("test", "pwd");
        game.join(userToJoin);

        assertEquals(2, game.getNumberOfPlayersInLobby());
        assertEquals(2, game.getPlayers().size());

        game.leave(creator);

        assertEquals(1, game.getNumberOfPlayersInLobby());
        assertEquals(1, game.getPlayers().size());
        assertEquals(GameState.CORRUPTED, game.getState());
    }

    @Test
    void failed_to_start_Lobby() throws FailedToJoinException, StartException {
        ArrayList<Double> numbers = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();
        CurrencyPair currencyPair = new CurrencyPair(Currency.CHF,Currency.EUR);

        for(int i = 0; i < 10; i++){
            numbers.add(1.0);
            dates.add("Date" + i);
        }

        GameRound gameRound = new GameRound(new Chart(numbers, dates, currencyPair));

        assertThrows(StartException.class, () -> game.start());

        User userToJoin = new User("test", "pwd");
        Player newPlayer = game.join(userToJoin);
        assertEquals(newPlayer, game.getPlayers().get(1));

        assertThrows(StartException.class, () -> game.start());

        game.addGameRound(gameRound);
        game.start();

        assertEquals(GameState.BETTING, game.getState());
    }

}
