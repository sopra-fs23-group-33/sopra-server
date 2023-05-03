package ch.uzh.ifi.hase.soprafs23.Runner;

import ch.uzh.ifi.hase.soprafs23.Forex.GameRound;
import ch.uzh.ifi.hase.soprafs23.Game.CorruptedState;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.exceptions.NotFoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.StartException;
import ch.uzh.ifi.hase.soprafs23.exceptions.endRoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.nextRoundException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import ch.uzh.ifi.hase.soprafs23.repository.GameRoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;


@Component
@Transactional
public class AsyncTransactionManager {

    private final GameRoundRepository gameRoundRepository;
    private final GameRepository gameRepository;

    @Autowired
    public AsyncTransactionManager(@Qualifier("gameRoundRepository") GameRoundRepository gameRoundRepository,@Qualifier("gameRepository")  GameRepository gameRepository) {
        this.gameRoundRepository = gameRoundRepository;
        this.gameRepository = gameRepository;
    }

    public void addGameRound(GameRound gameRound, Long gameID) throws NotFoundException {

        Long t1 = System.currentTimeMillis();

        this.gameRoundRepository.saveAndFlush(gameRound);
        Game game = this.findGame(gameID);
        game.addGameRound(gameRound);
        this.gameRepository.saveAndFlush(game);

        Long t2 = System.currentTimeMillis()-t1;
        System.out.println("time needed for fetching: "+t2);

    }

    public void addSingleGameRound(GameRound gameRound) {
        try {
            this.gameRoundRepository.saveAndFlush(gameRound);
        }
        catch (Exception | Error ignored){

        }
    }


    public Game findGame(Long gameID) throws NotFoundException {
        Game game = this.gameRepository.findByGameID(gameID);

        if (game != null)
            return game;
        else
            throw new NotFoundException();
    }


    public void updateGame(Long gameID){
        try {
            Game game = this.findGame(gameID);
            game.update();
            gameRepository.saveAndFlush(game);
        }
        catch (Exception | Error ignored){

        }
    }

    public void deleteGame(Long gameID){
        try {
            Game game = this.findGame(gameID);
            gameRepository.deleteByGameID(game.getGameID());
        }
        catch (Exception | Error ignored){

        }
    }

}
