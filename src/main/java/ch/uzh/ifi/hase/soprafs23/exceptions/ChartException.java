package ch.uzh.ifi.hase.soprafs23.exceptions;

public class ChartException extends Exception{
    public ChartException(){
        super("Fetching chart failed");
    }
}
