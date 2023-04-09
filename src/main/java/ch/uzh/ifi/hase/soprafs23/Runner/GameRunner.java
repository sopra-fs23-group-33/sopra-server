package ch.uzh.ifi.hase.soprafs23.Runner;


import ch.uzh.ifi.hase.soprafs23.Game.CorruptedState;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.GameState;
import ch.uzh.ifi.hase.soprafs23.exceptions.NotFoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.StartException;
import ch.uzh.ifi.hase.soprafs23.exceptions.endRoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.nextRoundException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class GameRunner {

    private final  GameRepository gameRepository;

    @Autowired
    public GameRunner(@Qualifier("gameRepository") GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @Async
    public void run(Long gameID) {

        int waitTime = 35;

        try {
            Game game = this.findGame(gameID);
            try {
                game.start();
                gameRepository.saveAndFlush(game);
            }
            catch (StartException e){
                game.setGameStatus(new CorruptedState(game));
                gameRepository.saveAndFlush(game);
            }
        }
        catch (NotFoundException e1) {
            return;
        }

        this.wait(waitTime);

        boolean abort = false;

        while(!abort){

            try {
                Game game = this.findGame(gameID);
                try {
                    game.endRound();
                    gameRepository.saveAndFlush(game);
                }
                catch (endRoundException e){
                    game.setGameStatus(new CorruptedState(game));
                    gameRepository.saveAndFlush(game);
                }
            }
            catch (NotFoundException e1) {
                return;
            }

            try {
                Game game = this.findGame(gameID);
                if(game.getState().equals(GameState.OVERVIEW) || game.getState().equals(GameState.CORRUPTED) )
                    abort = true;
            }
            catch (NotFoundException e1) {
                return;
            }

            this.wait(waitTime);

            try {
                Game game = this.findGame(gameID);
                try {
                    game.nextRound();
                    gameRepository.saveAndFlush(game);
                }
                catch (nextRoundException e){
                    game.setGameStatus(new CorruptedState(game));
                    gameRepository.saveAndFlush(game);
                }
            }
            catch (NotFoundException  e1) {
                return;
            }

            try {
                Game game = this.findGame(gameID);
                if(game.getState().equals(GameState.OVERVIEW) || game.getState().equals(GameState.CORRUPTED) )
                    abort = true;
            }
            catch (NotFoundException e1) {
                return;
            }

            this.wait(waitTime);
        }

        /*

        for (int i = 0; i < 100; i++) {
            Long t1 = System.currentTimeMillis();

            game.setName("LOBBY "+ Thread.currentThread().getId());
            this.gameRepository.saveAndFlush(game);
            this.wait(2);
            game.setName("BETTING "+ Thread.currentThread().getId());
            this.gameRepository.saveAndFlush(game);
            this.wait(2);
            game.setName("RESULT " + Thread.currentThread().getId());
            this.gameRepository.saveAndFlush(game);
            this.wait(2);

            int t2 = (int) (System.currentTimeMillis() - t1);
            game.setTimer(t2);
            this.gameRepository.saveAndFlush(game);
            System.out.println("current time " + t2);
            System.out.println("Runner with game: " + game.getGameID() + " is at iteration: " + i);
            System.out.println("Current Thread ID: " + Thread.currentThread().getId());
            
        }
        
         */


    }



    public void wait(int n){
        n = n*1000;
        try {
            Thread.sleep(n);
        }
        catch (InterruptedException e) {
        }
    }

    private Game findGame(Long gameID) throws NotFoundException {
        Game game = this.gameRepository.findByGameID(gameID);

        if (game != null)
            return game;
        else
            throw new NotFoundException();
    }
    
}
