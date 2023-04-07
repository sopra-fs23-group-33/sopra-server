package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.*;
import java.lang.reflect.Type;

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

    public GameStatus(Game game, GameState gameState){
        this.game = game;
        this.gameState = gameState;
    }

    public GameStatus(){}

    public GameState getGameState() {
        return this.gameState;
    }

    public Player join(User user){
        String ErrorMessage = "User failed to join game";
        throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }

    public void leave(User user) {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
        this.game.remove(player);
    }

    public void start(){
        String ErrorMessage = "Failed to start game because game is not in lobby state";
        throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }

    public Chart chart(){
        String ErrorMessage = "Failed to fetch chart";
        throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }

    public void endRound(){
        String ErrorMessage = "Failed to end round";
        throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }

    public void nextRound(){
        String ErrorMessage = "Failed to go to next round";
        throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
    }

}
