package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Data.GameData;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToJoinException;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToJoinExceptionBecauseLobbyFull;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameLobbyTestSingleplayer {
    private Game game;

    private GameData gameData;

    private User creator;

    @BeforeEach
    void setup_Singleplayer(){
        creator = new User("creator", "password");
        gameData = new GameData();
        gameData.setNumberOfRoundsToPlay(2);
        gameData.setTypeOfGame(GameType.SINGLEPLAYER);
        gameData.setPowerupsActive(false);
        gameData.setEventsActive(false);
        gameData.setName("GameRoom");
        gameData.setTotalLobbySize(1);

        game = new Game(creator, gameData);
        game.init();
    }

    @Test
    void initialization(){
        GameData gameData = this.game.status();

        assertEquals(creator, game.getCreator());
        assertEquals(GameState.LOBBY, game.getState());
        assertEquals("GameRoom", gameData.getName());
        assertEquals(GameType.SINGLEPLAYER, gameData.getTypeOfGame());
        assertFalse(gameData.isEventsActive());
        assertFalse(gameData.isPowerupsActive());
        assertEquals(0, gameData.getTimer());
        assertEquals(1, gameData.getTotalLobbySize());
        assertEquals(1, gameData.getNumberOfPlayersInLobby());
        assertEquals(0, gameData.getCurrentRoundPlayed());
        assertNull(gameData.getEvent());
    }

    @Test
    void leave_and_join_Lobby_Singleplayer() throws FailedToJoinException, PlayerNotFoundException {

        User userToJoin = new User("test", "pwd");

        assertEquals(1, game.getNumberOfPlayersInLobby());

        assertThrows(FailedToJoinExceptionBecauseLobbyFull.class, () -> game.join(userToJoin));

        assertEquals(1, game.getNumberOfPlayersInLobby());

        game.leave(creator);

        assertEquals(0, game.getNumberOfPlayersInLobby());
        assertEquals(GameState.CORRUPTED, game.getState());

        assertThrows(FailedToJoinException.class, () -> game.join(userToJoin));
    }

}
