import ch.uzh.ifi.hase.soprafs23.entity.User;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    public void incrementLost(){
        User user = new User();
        user.setNumberOfBetsLost(0);
        user.incrementNumberOfBetsLost();

        assertEquals(user.getNumberOfBetsLost(), 1);
    }

    @Test
    public void incrementWon(){
        User user = new User();
        user.setNumberOfBetsWon(0);
        user.incrementNumberOfBetsWon();

        assertEquals(user.getNumberOfBetsWon(), 1);
    }

    @Test
    public void incrementPlayed(){
        User user = new User();
        user.setTotalRoundsPlayed(0);
        user.incrementTotalRoundsPlayed();

        assertEquals(user.getTotalRoundsPlayed(), 1);
    }
}

