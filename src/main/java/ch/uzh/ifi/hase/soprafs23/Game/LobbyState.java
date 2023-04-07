package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.Entity;

@Entity
public class LobbyState extends GameStatus{

    public LobbyState(Game game) {
        super(game, GameState.LOBBY);
    }
    public LobbyState(){}

    @Override
    public Player join(User user) {

        try{
            Player player = this.game.findPlayerByUser(user);
            return player;
        }

        catch (Exception e) {
            if(!user.getState().equals(UserState.ONLINE)) {
                String ErrorMessage = "Failed to join game because user is not online";
                throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
            }

            if(this.game.getNumberOfPlayersInLobby() < this.game.totalLobbySize) {
                Player newPlayer = new Player(user);
                newPlayer.init();
                this.game.players.add(newPlayer);
                return newPlayer;
            }
            else {
                String ErrorMessage = "Failed to join game because lobby is full";
                throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
            }
        }
    }
    @Override
    public void start(){
        if(this.game.canStart()){
            this.game.setGameStatus(new BettingState(this.game));
        }
        else{
            String ErrorMessage = "game cannot be started because conditions for start are not satisfied";
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
    }

    @Override
    public void leave(User user) {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
        this.game.remove(player);

        if(user.equals(this.game.creator)) {
            this.game.setGameStatus(new CorruptedState(this.game));
        }

    }


}
