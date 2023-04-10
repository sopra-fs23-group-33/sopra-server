package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToPlaceBetExceptionBecauseNegative extends FailedToPlaceBetException{
    public FailedToPlaceBetExceptionBecauseNegative(){
        super("because bet amount is zero or negative");
    }
}
