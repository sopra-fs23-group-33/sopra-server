package ch.uzh.ifi.hase.soprafs23.Runner;

import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Random;
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
            sleepTime = game.getNumberOfRoundsToPlay()*(game.getBettingTime()+game.getResultTime())+30;
        }
        catch (Exception e) {
            return;
        }

        ScheduledFuture<?> scheduledFuture = scheduler.scheduleWithFixedDelay(gameUpdater, 1000);

        GameStopper gameStopper = new GameStopper(scheduledFuture, gameID);
        Instant stop = Instant.now();
        stop = stop.plusSeconds(sleepTime);

        scheduler.schedule(gameStopper, stop);

        GameDeleter gameDeleter = new GameDeleter(gameID);
        Instant delete = stop.plusSeconds(180);
        scheduler.schedule(gameDeleter, delete);
    }

    @Async
    public void deleteGameAfterCreation(Long gameID){
        Instant del = Instant.now();
        del = del.plusSeconds(60*10);

        GameDeleterAfterCreation deleterAfterCreation = new GameDeleterAfterCreation(gameID);

        scheduler.schedule(deleterAfterCreation, del);
    }

    @Async
    public void deleteGameAfterLeaving(Long gameID){
        Instant del = Instant.now();
        Random random = new Random();
        int time = random.nextInt(30)+1;
        del = del.plusSeconds(time);

        GameDeleterNoPlayers deleterNoPlayers = new GameDeleterNoPlayers(gameID);

        scheduler.schedule(deleterNoPlayers, del);
    }

    private class GameDeleterAfterCreation implements Runnable {
        private final Long gameID;

        public GameDeleterAfterCreation(Long gameID) {
            this.gameID = gameID;
        }

        @Override
        public void run() {
            asyncTransactionManager.deleteGameInLobby(gameID);
            System.out.println("deleted game after Lobby with ID " + gameID + ": " + Thread.currentThread().getId());
        }
    }

    private class GameDeleterNoPlayers implements Runnable {
        private final Long gameID;

        public GameDeleterNoPlayers(Long gameID) {
            this.gameID = gameID;
        }

        @Override
        public void run() {
            asyncTransactionManager.deleteGameNoPlayers(gameID);
            System.out.println("deleted game because no players left with ID " + gameID + ": " + Thread.currentThread().getId());
        }
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

    private class GameDeleter implements Runnable {
        private final Long gameID;

        public GameDeleter(Long gameID) {
            this.gameID = gameID;
        }

        @Override
        public void run() {
            asyncTransactionManager.deleteGame(this.gameID);
            System.out.println("game deleted " + this.gameID + ": " + Thread.currentThread().getId());
        }
    }
}

