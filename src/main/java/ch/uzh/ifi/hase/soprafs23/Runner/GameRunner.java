package ch.uzh.ifi.hase.soprafs23.Runner;



import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.exceptions.NotFoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.StartException;
import ch.uzh.ifi.hase.soprafs23.exceptions.endRoundException;
import ch.uzh.ifi.hase.soprafs23.exceptions.nextRoundException;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Component
public class GameRunner {

    private final  GameRepository gameRepository;
    private final AsyncTransactionManager asyncTransactionManager;
    @Autowired
    public GameRunner(@Qualifier("gameRepository") GameRepository gameRepository, AsyncTransactionManager asyncTransactionManager) {
        this.gameRepository = gameRepository;
        this.asyncTransactionManager = asyncTransactionManager;
    }

    //@Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void run(Long gameID, int bettingTime, int resultTime) {

        int waitTimeBetting = bettingTime;
        int waitTimeResult = resultTime;


        try {
            asyncTransactionManager.startGame(gameID);
        }
        catch (StartException e1){
            asyncTransactionManager.corruptGame(gameID);
        }


        asyncTransactionManager.setTimerGame(gameID,waitTimeBetting);
        this.wait_interrupt(waitTimeBetting, gameID);

        boolean abort = false;

        while(!abort){

            try {
                asyncTransactionManager.endRoundGame(gameID);
            }
            catch (endRoundException e2){
                asyncTransactionManager.corruptGame(gameID);
            }

            asyncTransactionManager.setTimerGame(gameID,waitTimeResult);
            this.wait(waitTimeResult);

            try {
                asyncTransactionManager.nextRoundGame(gameID);
            }
            catch (nextRoundException e3){
                asyncTransactionManager.corruptGame(gameID);
            }

            abort = asyncTransactionManager.getAbort(gameID);

            asyncTransactionManager.setTimerGame(gameID,waitTimeBetting);
            this.wait_interrupt(waitTimeBetting, gameID);
        }

    }

    private void wait(int n){
        n = n*1000;
        try{
            Thread.sleep(n);
        }
        catch (InterruptedException e) {
            return;
        }
    }

    private void wait_interrupt(int n, Long gameID){
        n = n*1000;
        long t = System.currentTimeMillis();

        while(System.currentTimeMillis() - t < n){
            try{
                boolean allBetsPlaced = asyncTransactionManager.allBetsPlaced(gameID);
                if(allBetsPlaced) {
                    System.out.println("All bets Placed");
                    return;
                }
                Thread.sleep(500);
            }
            catch ( InterruptedException e) {
                return;
            }
        }
    }

}
