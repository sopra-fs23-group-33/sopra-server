package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;

import javax.persistence.*;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class GameStatus {

    @Id
    @GeneratedValue
    Long gameStatusID;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_gameID")
    Game game;

    @Enumerated(EnumType.STRING)
    GameState gameState;

    protected GameStatus(Game game, GameState gameState){
        this.game = game;
        this.gameState = gameState;
    }

    protected GameStatus(){}

    public GameState getGameState() {
        return this.gameState;
    }

    public Player join(User user) throws FailedToJoinException {
        throw new FailedToJoinException();
    }

    public void leave(User user) throws PlayerNotFoundException {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
        this.game.remove(player);
    }

    public void start() throws StartException {
        throw new StartException();
    }

    public Chart chart() throws ChartException {
        throw new ChartException();
    }

    public void endRound() throws endRoundException {
        throw new endRoundException();
    }

    public void nextRound() throws nextRoundException {
        throw new nextRoundException();
    }

    public void update() throws StartException, endRoundException, nextRoundException {}

}
