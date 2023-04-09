package ch.uzh.ifi.hase.soprafs23.exceptions;

public class NotFoundException extends  Exception{
    public NotFoundException(){
        super("entity was not found");
    }
}
