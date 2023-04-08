package ch.uzh.ifi.hase.soprafs23.Runner;


import ch.uzh.ifi.hase.soprafs23.Game.Game;
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
    public void run(Game game) {
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
    }



    public void wait(int n){
        n = n*1000;
        try {
            Thread.sleep(n);
        }
        catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
