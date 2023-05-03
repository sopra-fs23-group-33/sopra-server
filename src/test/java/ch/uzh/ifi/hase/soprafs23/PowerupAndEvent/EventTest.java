package ch.uzh.ifi.hase.soprafs23.PowerupAndEvent;

import ch.uzh.ifi.hase.soprafs23.Data.EventData;
import ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents.Event;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EventTest {

    @Test
    void generateEventForSinglePlayer(){
        for(int i = 0; i<100; i++){
            Event event = Event.generateRandomEvent(GameType.SINGLEPLAYER);
            assertTrue(event.getGameTypes().contains(GameType.SINGLEPLAYER));
        }
    }

    @Test
    void generateEventForMultiPlayer(){
        for(int i = 0; i<100; i++){
            Event event = Event.generateRandomEvent(GameType.MULTIPLAYER);
            assertTrue(event.getGameTypes().contains(GameType.MULTIPLAYER));
        }
    }

    @Test
    void eventData(){
        Event event = Event.STIMULUS;
        EventData data = event.getEventData();
        assertEquals(data.getDescription(), event.getDescription());
        assertEquals(data.getName(), event.getName());;
    }
}
