package ch.uzh.ifi.hase.soprafs23.constant;

public enum GameType {
    SINGLEPLAYER, MULTIPLAYER;
    
    public boolean validNumberOfPlayers(int n){
        if(this.equals(SINGLEPLAYER)){
           return n == 1;
        }

        else if(this.equals(MULTIPLAYER)){
            return n >= 2;
        }

        else
            return false;
    }
}
