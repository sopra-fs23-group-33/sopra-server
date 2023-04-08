package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToJoinExceptionBecauseOffline extends FailedToJoinException{
    public FailedToJoinExceptionBecauseOffline(){
        super("because User is offline");
    }
}
