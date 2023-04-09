package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToPlaceBetExceptionBecauseInactive extends FailedToPlaceBetException{
    public FailedToPlaceBetExceptionBecauseInactive(){
        super("because player already left game");
    }
}
