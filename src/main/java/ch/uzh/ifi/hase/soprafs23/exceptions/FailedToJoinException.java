package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToJoinException extends Exception{
    public FailedToJoinException(){
        super("Failed to join game");
    }

    public FailedToJoinException(String reason){
        super("Failed to join game" + " " + reason);
    }
}
