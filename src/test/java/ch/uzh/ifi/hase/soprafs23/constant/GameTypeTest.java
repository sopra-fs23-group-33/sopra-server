package ch.uzh.ifi.hase.soprafs23.constant;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GameTypeTest {

    @Test
    void playerCount(){
        assertTrue(GameType.SINGLEPLAYER.validNumberOfPlayers(1));
        assertTrue(GameType.MULTIPLAYER.validNumberOfPlayers(3));
        assertFalse(GameType.MULTIPLAYER.validNumberOfPlayers(0));
    }
}
