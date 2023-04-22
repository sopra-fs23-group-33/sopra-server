package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.exceptions.nextRoundException;


import javax.persistence.Entity;

@Entity
public class ResultState extends GameStatus{
    public ResultState(Game game) {
        super(game, GameState.RESULT);
        this.game.setTimerForResult();
    }
    public ResultState(){}

    @Override
    public Chart chart() {
        int round = this.game.currentRoundPlayed;
        return this.game.getGameRounds().get(round-1).getSecondChart();
    }


    @Override
    public void nextRound(){
        for(Player player: game.players)
            player.resetBet();

        if(this.game.currentRoundPlayed >= this.game.numberOfRoundsToPlay || !this.game.checkIntegrity()) {
            this.game.setTimer(0);
            this.game.setGameStatus(new OverviewState(this.game));
        }
        else
            this.game.setGameStatus(new BettingState(this.game));
    }

    @Override
    public void update() throws nextRoundException {
        this.game.decrementTimer();

        if(this.game.getTimer() <= 0 )
            this.game.nextRound();
    }

}
