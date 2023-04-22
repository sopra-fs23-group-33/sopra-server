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

    public int getRounds(Long gameID) throws NotFoundException{
            Game game = this.findGame(gameID);
            return game.getNumberOfRoundsToPlay();
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
        this.gameRoundRepository.saveAndFlush(gameRound);
    }

    public void startGame(Long gameID) throws  StartException {
        try {
            Game game = this.findGame(gameID);
            game.start();
            gameRepository.saveAndFlush(game);
        }
        catch (NotFoundException ignored){
            ;
        }
    }

    public void endRoundGame(Long gameID) throws endRoundException{
        try {
            Game game = this.findGame(gameID);
            game.endRound();
            gameRepository.saveAndFlush(game);
        }
        catch (NotFoundException ignored){
            ;
        }
    }

    public void nextRoundGame(Long gameID) throws nextRoundException{
        try {
            Game game = this.findGame(gameID);
            game.nextRound();
            gameRepository.saveAndFlush(game);
        }
        catch (NotFoundException ignored){
            ;
        }
    }

    public void setTimerGame(Long gameID, int timer) {
        try {
            Game game = this.findGame(gameID);
            game.setTimer(timer);
            gameRepository.saveAndFlush(game);
        }
        catch (NotFoundException ignored){
            ;
        }
    }


    public void corruptGame(long gameID)  {
        try {
            Game game = this.findGame(gameID);
            game.setGameStatus(new CorruptedState(game));
            gameRepository.saveAndFlush(game);
        }
        catch (NotFoundException ignored){
            ;
        }
    }

    public boolean getAbort(Long gameID){
        try {
            Game game = this.findGame(gameID);
            return (game.getState().equals(GameState.OVERVIEW) || game.getState().equals(GameState.CORRUPTED));
        }
        catch (NotFoundException e1) {
            return true;
        }
    }

    public Game findGame(Long gameID) throws NotFoundException {
        Game game = this.gameRepository.findByGameID(gameID);

        if (game != null)
            return game;
        else
            throw new NotFoundException();
    }

    public boolean allBetsPlaced(Long gameID){
        try {
            Game game = this.findGame(gameID);
            return game.allBetsPlaced();
        }
        catch (NotFoundException ignored){
            return false;
        }
    }

    public void updateGame(Long gameID){
        try {
            Game game = this.findGame(gameID);
            game.update();
            gameRepository.saveAndFlush(game);
        }
        catch (Exception ignored){
            ;
        }
    }

    public int numberOfRounds(){
        return (int) gameRoundRepository.countByUsageFalse();
    }

}
