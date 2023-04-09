package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;
import ch.uzh.ifi.hase.soprafs23.Runner.GameRunner;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import ch.uzh.ifi.hase.soprafs23.exceptions.FailedToPlaceBetException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameStatusRepository;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional
public class PlayerService {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(@Qualifier("gameRepository") GameRepository gameRepository,
                       UserService userService,
                       PlayerRepository playerRepository,
                       GameStatusRepository gameStatusRepository,
                       GameRunner gameRunner) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    public Player getPlayerByPlayerID(Long playerID) {
        Player playerByID = this.playerRepository.findByPlayerID(playerID);

        if (playerByID != null)
            return playerByID;
        else {
            String ErrorMessage = "Player with playerId " + playerID + " was not found";
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ErrorMessage);
        }
    }

    public void placeBet(Bet betToPlace, Long playerID){
        Player playerByID = this.getPlayerByPlayerID(playerID);

        try{
            playerByID.placeBet(betToPlace);
            this.playerRepository.saveAndFlush(playerByID);
        }
        catch (FailedToPlaceBetException e) {
            String ErrorMessage = e.getMessage();
            throw new ResponseStatusException(HttpStatus.CONFLICT, ErrorMessage);
        }
    }

    public Result getResult(Long playerID){
        Player playerByID = this.getPlayerByPlayerID(playerID);
        return playerByID.getResult();
    }
}
