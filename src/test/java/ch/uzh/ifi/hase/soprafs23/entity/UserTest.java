package ch.uzh.ifi.hase.soprafs23.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class UserTest {
    @Test
    void equalHashCodes(){
        User user1 = new User("username", "pwd");
        User user2 = new User("username", "pwd");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void unequalHashCodes(){
        User user1 = new User("username", "pwd");
        User user2 = new User("username2", "pwd");

        assertNotEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    void equalUsers(){
        User user1 = new User("username", "pwd");
        User user2 = new User("username", "pwd");

        assertEquals(user1, user2);
    }

    @Test
    void unequalUsers(){
        User user1 = new User("username", "pwd");
        User user2 = new User("username2", "pwd");

        assertNotEquals(user1, user2);
    }


    @Test
    void incrementLost(){
        User user = new User();
        user.setNumberOfBetsLost(0);
        user.incrementNumberOfBetsLost();

        assertEquals(user.getNumberOfBetsLost(), 1);
    }

    @Test
    void incrementWon(){
        User user = new User();
        user.setNumberOfBetsWon(0);
        user.incrementNumberOfBetsWon();

        assertEquals(user.getNumberOfBetsWon(), 1);
    }

    @Test
    void incrementPlayed(){
        User user = new User();
        user.setTotalRoundsPlayed(0);
        user.incrementTotalRoundsPlayed();

        assertEquals(user.getTotalRoundsPlayed(), 1);
    }
}

