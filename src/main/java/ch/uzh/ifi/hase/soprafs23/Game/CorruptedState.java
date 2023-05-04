package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;

import javax.persistence.Entity;

@Entity
public class CorruptedState extends GameStatus{
    public CorruptedState(Game game) {
        super(game, GameState.CORRUPTED);
    }
    public CorruptedState(){}

    @Override
    public void leave(User user) throws PlayerNotFoundException {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
        //this.game.remove(player);
    }

}


