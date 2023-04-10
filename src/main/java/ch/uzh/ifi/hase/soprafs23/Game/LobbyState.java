package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserState;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;
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
    public Player join(User user) throws FailedToJoinException {

        try{
            return this.game.findPlayerByUser(user);
        }

        catch (PlayerNotFoundException e) {
            if(!user.getState().equals(UserState.ONLINE)) {
                throw new FailedToJoinExceptionBecauseOffline();
            }

            else if(this.game.getNumberOfPlayersInLobby() < this.game.totalLobbySize) {
                Player newPlayer = new Player(user);
                newPlayer.init();
                this.game.players.add(newPlayer);
                return newPlayer;
            }
            else if(this.game.getNumberOfPlayersInLobby() >= this.game.totalLobbySize){
                throw new FailedToJoinExceptionBecauseLobbyFull();
            }
            else {
                throw new FailedToJoinException();
            }
        }
    }
    @Override
    public void start() throws StartException {
        if(this.game.canStart())
            this.game.setGameStatus(new BettingState(this.game));
        else
            throw new StartException();
    }

    @Override
    public void leave(User user) throws PlayerNotFoundException {
        Player player = this.game.findPlayerByUser(user);
        player.setState(PlayerState.INACTIVE);
        this.game.remove(player);

        if(user.equals(this.game.creator)) {
            this.game.setGameStatus(new CorruptedState(this.game));
        }

    }


}
