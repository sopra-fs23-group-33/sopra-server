package ch.uzh.ifi.hase.soprafs23.entity;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UserTest {
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

        assertEquals(1, user.getNumberOfBetsLost());
    }

    @Test
    void incrementWon(){
        User user = new User();
        user.setNumberOfBetsWon(0);
        user.incrementNumberOfBetsWon();

        assertEquals(1, user.getNumberOfBetsWon());
    }

    @Test
    void incrementPlayed(){
        User user = new User();
        user.setTotalRoundsPlayed(0);
        user.incrementTotalRoundsPlayed();

        assertEquals(1, user.getTotalRoundsPlayed());
    }
}

