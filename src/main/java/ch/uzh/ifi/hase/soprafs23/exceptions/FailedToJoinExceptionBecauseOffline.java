package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToJoinExceptionBecauseOffline extends FailedToJoinException{
    public FailedToJoinExceptionBecauseOffline(){
        super("because user is offline or already playing a game");
    }
}
