package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;

import javax.persistence.Entity;

@Entity
public class CorruptedState extends GameStatus{
    public CorruptedState(Game game) {
        super(game, GameState.CORRUPTED);
    }
    public CorruptedState(){}

}


