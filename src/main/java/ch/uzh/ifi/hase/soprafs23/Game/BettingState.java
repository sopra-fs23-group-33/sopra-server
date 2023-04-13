package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class BettingState extends GameStatus{
    public BettingState(Game game) {
        super(game, GameState.BETTING);
        this.game.currentRoundPlayed++;
    }

    public BettingState(){}

    @Override
    public void leave(User user) throws PlayerNotFoundException {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
    }

    @Override
    public Chart chart() {
        int round = this.game.currentRoundPlayed;
        return this.game.getGameRounds().get(round-1).getFirstChart();
    }


    @Override
    public void endRound(){
        GameRound gameRound = this.game.getGameRounds().get(this.game.currentRoundPlayed-1);
        ArrayList<Player> playersToRemove = new ArrayList<>();

        for(Player player: this.game.players){
            player.endRound(gameRound.getOutcome(), gameRound.getRatio());

            if(player.getState().equals(PlayerState.INACTIVE))
                playersToRemove.add(player);
        }

        for(Player player: playersToRemove){
            this.game.remove(player);
        }

        this.game.setGameStatus(new ResultState(this.game));
    }


}
