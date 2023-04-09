package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToPlaceBetExceptionBecauseBalance extends FailedToPlaceBetException{
    public FailedToPlaceBetExceptionBecauseBalance(){
        super("because balance is too low");
    }
}
