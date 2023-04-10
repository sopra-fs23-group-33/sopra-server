package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;

@Entity
public class ComputeState extends GameStatus{
    public ComputeState(Game game) {
        super(game, GameState.COMPUTE);
    }
    public ComputeState(){}

    @Override
    public void leave(User user) throws PlayerNotFoundException {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
    }
}
