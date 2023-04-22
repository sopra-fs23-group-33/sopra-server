package ch.uzh.ifi.hase.soprafs23.Game;

import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;
import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.exceptions.*;


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
            if(!user.getStatus().equals(UserStatus.ONLINE)) {
                throw new FailedToJoinExceptionBecauseOffline();
            }

            else if(this.game.getNumberOfPlayersInLobby() < this.game.getTotalLobbySize()) {
                Player newPlayer = new Player(user);
                newPlayer.init();
                this.game.players.add(newPlayer);
                return newPlayer;
            }
            else if(this.game.getNumberOfPlayersInLobby() >= this.game.getTotalLobbySize()){
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

        if(user.equals(this.game.getCreator())) {
            this.game.setGameStatus(new CorruptedState(this.game));
        }

    }

    @Override
    public void update() throws StartException {
        this.start();
    }


}
