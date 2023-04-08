package ch.uzh.ifi.hase.soprafs23.exceptions;

public class PlayerNotFoundException extends Exception{
    public PlayerNotFoundException(){
        super("Player was not found");
    }
}
