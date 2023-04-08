package ch.uzh.ifi.hase.soprafs23.exceptions;

public class FailedToJoinExceptionBecauseLobbyFull extends FailedToJoinException{
    public FailedToJoinExceptionBecauseLobbyFull(){
        super("because Lobby is full");
    }
}
