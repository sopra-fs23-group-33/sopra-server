package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToPlaceBetException extends Exception{
    FailedToPlaceBetException(){
        super("Failed to place bet");
    }

    FailedToPlaceBetException(String reason){
        super("Failed to place bet " + reason);
    }
}
