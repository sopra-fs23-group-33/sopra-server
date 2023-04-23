package ch.uzh.ifi.hase.soprafs23.exceptions;

public class PowerupNotFoundException extends Exception{
    public PowerupNotFoundException(){
        super("powerup was not found");
    }
}
