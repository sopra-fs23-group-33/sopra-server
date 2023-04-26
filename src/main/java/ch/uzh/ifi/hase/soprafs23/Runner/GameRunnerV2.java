package ch.uzh.ifi.hase.soprafs23.Runner;

import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

@Component
public class GameRunnerV2 {

    private final GameRepository gameRepository;
    private final AsyncTransactionManager asyncTransactionManager;

    @Autowired
    private ThreadPoolTaskScheduler scheduler;

    @Autowired
    public GameRunnerV2(@Qualifier("gameRepository") GameRepository gameRepository, AsyncTransactionManager asyncTransactionManager) {
        this.gameRepository = gameRepository;
        this.asyncTransactionManager = asyncTransactionManager;
    }

    @Async
    public void startGame(Long gameID){
        GameUpdater gameUpdater = new GameUpdater(gameID);

        int sleepTime = 0;

        try {
            Game game = asyncTransactionManager.findGame(gameID);
            sleepTime = game.getNumberOfRoundsToPlay()*(game.getBettingTime()+game.getResultTime())+20;
        }
        catch (Exception e) {
            return;
        }

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(gameUpdater, 1000);

        GameStopper gameStopper = new GameStopper(scheduledFuture, gameID);
        Instant now = Instant.now();
        now = now.plusSeconds(sleepTime);

        scheduler.schedule(gameStopper, now);

    }


    private class GameUpdater implements Runnable {
        private final Long gameID;

        public GameUpdater(Long gameID) {
            this.gameID = gameID;
        }

        @Override
        public void run() {
            asyncTransactionManager.updateGame(gameID);
            System.out.println("runnable executed for game " + gameID + ": " + Thread.currentThread().getId());
        }
    }

    private class GameStopper implements Runnable {
        private final ScheduledFuture<?> scheduledFuture;
        private final Long gameID;

        public GameStopper(ScheduledFuture<?> scheduledFuture, Long gameID) {
            this.scheduledFuture = scheduledFuture;
            this.gameID = gameID;
        }

        @Override
        public void run() {
            this.scheduledFuture.cancel(true);
            System.out.println("runnable stopped " + this.gameID + ": " + Thread.currentThread().getId());
        }
    }
}

