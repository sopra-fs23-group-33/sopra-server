package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.PlayerNotFoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.endRoundException;


import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class BettingState extends GameStatus{
    public BettingState(Game game) {
        super(game, GameState.BETTING);
        this.game.incrementRoundsPlayed();
        this.game.setTimerForBetting();
    }

    public BettingState(){}

    @Override
    public void leave(User user) throws PlayerNotFoundException {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
    }

    @Override
    public Chart chart() {
        int round = this.game.getCurrentRoundPlayed();
        return this.game.getGameRounds().get(round-1).getFirstChart();
    }


    @Override
    public void endRound(){
        GameRound gameRound = this.game.getGameRounds().get(this.game.getCurrentRoundPlayed()-1);
        ArrayList<Player> playersToRemove = new ArrayList<>();

        this.game.generateEvent();

        //if(this.game.isPowerupsActive() || this.game.isEventsActive())
        this.game.collectAndDistributeInstructions();

        for(Player player: this.game.getPlayers()){
            player.endRound(gameRound.getOutcome(), gameRound.getRatio());

            if(player.getState().equals(PlayerState.INACTIVE))
                playersToRemove.add(player);
        }

        for(Player player: playersToRemove){
            this.game.remove(player);
        }

        this.game.setGameStatus(new ResultState(this.game));
    }

    @Override
    public void update() throws endRoundException {
        this.game.decrementTimer();

        if((this.game.getTimer() <= 0 || this.game.allBetsPlaced()))
            this.game.endRound();
    }

}
