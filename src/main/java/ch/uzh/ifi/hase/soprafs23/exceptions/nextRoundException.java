package ch.uzh.ifi.hase.soprafs23.exceptions;

public class nextRoundException extends Exception{
    public nextRoundException(){
        super("failed to go to next round");
    }
}
