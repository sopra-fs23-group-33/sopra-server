package ch.uzh.ifi.hase.soprafs23.entity;



import static java.lang.Math.round;
import static org.junit.jupiter.api.Assertions.*;

import ch.uzh.ifi.hase.soprafs23.Betting.Bet;
import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Betting.Result;

import ch.uzh.ifi.hase.soprafs23.Data.PlayerData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Powerups.*;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;

import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.constant.PlayerState;

import ch.uzh.ifi.hase.soprafs23.exceptions.*;
import ch.uzh.ifi.hase.soprafs23.repository.PlayerRepository;

import ch.uzh.ifi.hase.soprafs23.repository.PowerupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;


@DataJpaTest
class PlayerTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PowerupRepository powerupRepository;

    private User user;

    private Player player;

    @BeforeEach
    void setup() {
        user = new User("creator", "password");
        player = new Player(user);
        player.init();
        playerRepository.saveAndFlush(player);
    }

    @AfterEach
    void teardown(){
        this.playerRepository.deleteAll();
        this.powerupRepository.deleteAll();
    }


    @Test
    void initialization(){
        assertEquals(user, player.getUser());

        Bet bet = player.getCurrentBet();
        Result result = player.getResult();

        assertEquals(Direction.NONE, bet.getDirection());
        assertEquals(0, bet.getAmount());
        assertEquals(0, result.getBettingAmount());
        assertEquals(Direction.NONE, result.getOutcome());
        assertEquals(0, result.getProfit());

        assertEquals(1000, player.getBalance());
        assertEquals(PlayerState.ACTIVE, player.getState());
    }

    @Test
    void initial_playerData(){
        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(1000, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(0, playerData.getNumberOfLostRounds());
        assertEquals(Direction.NONE, playerData.getTypeOfCurrentBet());
    }

    @Test
    void place_bet() throws FailedToPlaceBetException {
        Bet negativeBet = new Bet(Direction.DOWN, -10);
        Bet zeroBet = new Bet(Direction.DOWN, 0);
        Bet noneBet = new  Bet(Direction.NONE, 10);
        Bet highVolumeBet =  new Bet(Direction.UP, 100000);
        Bet validBet = new Bet(Direction.UP, 100);

        assertThrows(FailedToPlaceBetExceptionBecauseNegative.class, () -> player.placeBet(negativeBet));
        assertThrows(FailedToPlaceBetExceptionBecauseNegative.class, () -> player.placeBet(zeroBet));
        assertThrows(FailedToPlaceBetExceptionBecauseDirection.class, () -> player.placeBet(noneBet));
        assertThrows(FailedToPlaceBetExceptionBecauseBalance.class, () -> player.placeBet(highVolumeBet));

        player.placeBet(validBet);

        assertEquals(validBet.getDirection(), player.getCurrentBet().getDirection());
        assertEquals(validBet.getAmount(), player.getCurrentBet().getAmount());

        player.setState(PlayerState.INACTIVE);
        assertThrows(FailedToPlaceBetExceptionBecauseInactive.class, () -> player.placeBet(validBet));
    }

    @Test
    void reset_bet() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        assertEquals(validBet.getDirection(), player.getCurrentBet().getDirection());
        assertEquals(validBet.getAmount(), player.getCurrentBet().getAmount());

        player.resetBet();

        assertEquals(Direction.NONE, player.getCurrentBet().getDirection());
        assertEquals(0, player.getCurrentBet().getAmount());

    }

    @Test
    void equals(){
        Player otherPlayer = new Player(user);
        otherPlayer.init();

        User anotherUser = new User("test", "pwd");

        Player anotherPlayer = new Player(anotherUser);
        anotherPlayer.init();

        assertTrue(otherPlayer.equals(player));
        assertFalse(anotherPlayer.equals(player));
        assertFalse(player.equals(null));
        assertTrue(player.equals(player));
        assertFalse(player.equals(new User()));

        assertEquals(otherPlayer.hashCode(), player.hashCode());
    }

    @Test
    void endRound_win() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);

        player.placeBet(validBet);
        player.endRound(Direction.UP, 1.01);
        Result result = player.getResult();

        assertEquals(100, result.getBettingAmount());
        assertEquals(Direction.UP, result.getOutcome());
        assertEquals(200, result.getProfit());

        assertEquals(1200, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(1200, playerData.getAccountBalance());
        assertEquals(1, playerData.getNumberOfWonRounds());
        assertEquals(0, playerData.getNumberOfLostRounds());
        assertEquals(Direction.UP, playerData.getTypeOfCurrentBet());

        User user = player.getUser();

        assertEquals(1, user.getNumberOfBetsWon());
        assertEquals(0, user.getNumberOfBetsLost());
        assertEquals(1, user.getWinRate());
    }


    @Test
    void endRound_lost() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 100);

        player.placeBet(validBet);
        player.endRound(Direction.DOWN, 1.01);
        Result result = player.getResult();

        assertEquals(100, result.getBettingAmount());
        assertEquals(Direction.DOWN, result.getOutcome());
        assertEquals(-200, result.getProfit());

        assertEquals(800, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(800, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(1, playerData.getNumberOfLostRounds());
        assertEquals(Direction.UP, playerData.getTypeOfCurrentBet());

        User user = player.getUser();

        assertEquals(0, user.getNumberOfBetsWon());
        assertEquals(1, user.getNumberOfBetsLost());
        assertEquals(0, user.getWinRate());
    }

    @Test
    void endRound_lost_more_than_zero() throws FailedToPlaceBetException {
        Bet validBet = new Bet(Direction.UP, 1000);

        player.placeBet(validBet);
        player.endRound(Direction.DOWN, 1.01);
        Result result = player.getResult();

        assertEquals(1000, result.getBettingAmount());
        assertEquals(Direction.DOWN, result.getOutcome());
        assertEquals(-1000, result.getProfit());

        assertEquals(0, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(0, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(1, playerData.getNumberOfLostRounds());
        assertEquals(Direction.UP, playerData.getTypeOfCurrentBet());

        User user = player.getUser();

        assertEquals(0, user.getNumberOfBetsWon());
        assertEquals(1, user.getNumberOfBetsLost());
        assertEquals(0, user.getWinRate());
    }

    @Test
    void endRound_zeroBet() throws FailedToPlaceBetException {
        player.endRound(Direction.DOWN, 1.01);
        Result result = player.getResult();

        assertEquals(0, result.getBettingAmount());
        assertEquals(Direction.DOWN, result.getOutcome());
        assertEquals(0, result.getProfit());

        assertEquals(1000, player.getBalance());

        PlayerData playerData = player.status();

        assertEquals(user.getUsername(), playerData.getUsername());
        assertEquals(1000, playerData.getAccountBalance());
        assertEquals(0, playerData.getNumberOfWonRounds());
        assertEquals(0, playerData.getNumberOfLostRounds());
        assertEquals(Direction.NONE, playerData.getTypeOfCurrentBet());

        User user = player.getUser();

        assertEquals(0, user.getNumberOfBetsWon());
        assertEquals(0, user.getNumberOfBetsLost());
        assertEquals(0, user.getWinRate());
    }

    @Test
    void simple_instructions_bet_won() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A0 = new Instruction(playerID, InstructionType.a0, 100);
        Instruction A1 = new Instruction(playerID, InstructionType.a1, 2);
        Instruction A2 = new Instruction(playerID, InstructionType.a2, 3);
        Instruction A3 = new Instruction(playerID, InstructionType.a3, 200);

        player.addInstruction(A0);
        player.addInstruction(A1);
        player.addInstruction(A2);
        player.addInstruction(A3);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);


        player.endRound(Direction.UP, 1.01);

        assertEquals(round((100+2*1000+1*(((1.01-1)*100+1)*100*3 + 200))), player.getBalance());

        player.resetBet();
        player.placeBet(validBet);
        player.endRound(Direction.UP, 1.01);

        assertEquals(round(2900 + 2*100), player.getBalance());
    }

    @Test
    void simple_instructions_bet_lost() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A0 = new Instruction(playerID, InstructionType.a0, 100);
        Instruction A1 = new Instruction(playerID, InstructionType.a1, 2);
        Instruction A2 = new Instruction(playerID, InstructionType.a2, 3);
        Instruction A3 = new Instruction(playerID, InstructionType.a3, 200);

        player.addInstruction(A0);
        player.addInstruction(A1);
        player.addInstruction(A2);
        player.addInstruction(A3);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);


        player.endRound(Direction.DOWN, 1.01);

        assertEquals(round((100+2*1000-1*(((1.01-1)*100+1)*100*3 + 200)))   , player.getBalance());
    }


    @Test
    void simple_instructions_zero_bet() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A0 = new Instruction(playerID, InstructionType.a0, 100);
        Instruction A1 = new Instruction(playerID, InstructionType.a1, 2);
        Instruction A2 = new Instruction(playerID, InstructionType.a2, 3);
        Instruction A3 = new Instruction(playerID, InstructionType.a3, 200);

        player.addInstruction(A0);
        player.addInstruction(A1);
        player.addInstruction(A2);
        player.addInstruction(A3);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(100+2*1000)   , player.getBalance());
    }

    @Test
    void more_complex_instructions() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A0 = new Instruction(playerID, InstructionType.a0, 100);
        Instruction A1 = new Instruction(playerID, InstructionType.a1, 2);
        Instruction A2 = new Instruction(playerID, InstructionType.a2, 4);
        Instruction A3 = new Instruction(playerID, InstructionType.a3, 200);

        Instruction A0R = new Instruction(playerID, InstructionType.a0, -100);
        Instruction A1R = new Instruction(playerID, InstructionType.a1, 0.5);
        Instruction A2R = new Instruction(playerID, InstructionType.a2, 0.25);
        Instruction A3R = new Instruction(playerID, InstructionType.a3, -200);

        player.addInstruction(A1);
        player.addInstruction(A2R);
        player.addInstruction(A0);
        player.addInstruction(A0R);
        player.addInstruction(A3R);
        player.addInstruction(A2);
        player.addInstruction(A1R);
        player.addInstruction(A3);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000+2*100)   , player.getBalance());
    }

    @Test
    void instructions_RiskInsurance() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A4 = new Instruction(playerID, InstructionType.a4, 1);

        player.addInstruction(A4);
        player.addInstruction(A4); //place two to check that they don't stack

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.DOWN, 1.01);

        assertEquals(round(1000)   , player.getBalance());
    }

    @Test
    void instructions_RiskInsurance_wih_winning_bet() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A4 = new Instruction(playerID, InstructionType.a4, 1);

        player.addInstruction(A4);
        player.addInstruction(A4); //place two to check that they don't stack

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000+2*100)   , player.getBalance());
    }

    @Test
    void instructions_RobinHood_steal_without_Guardian() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A7 = new Instruction(playerID, InstructionType.a7, 100);

        player.addInstruction(A7);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100+100)   , player.getBalance());
    }

    @Test
    void instructions_RobinHood_steal_with_Guardian() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A7 = new Instruction(playerID, InstructionType.a7, 100);
        Instruction A8 = new Instruction(playerID, InstructionType.a8, 0);

        player.addInstruction(A7);
        player.addInstruction(A8);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100)   , player.getBalance());
    }

    @Test
    void instructions_RobinHood_stolen_with_Guardian() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A5 = new Instruction(playerID, InstructionType.a5, 100);
        Instruction A6 = new Instruction(playerID, InstructionType.a6, 0);

        player.addInstruction(A5);
        player.addInstruction(A6);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100)   , player.getBalance());
    }

    @Test
    void instructions_RobinHood_stolen_without_Guardian() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A5 = new Instruction(playerID, InstructionType.a5, 100);
        player.addInstruction(A5);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100-100)   , player.getBalance());
    }

    @Test
    void instructions_Hacker_receiver_without_Cybersecurity() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A9 = new Instruction(playerID, InstructionType.a9, 100);
        player.addInstruction(A9);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100-100)   , player.getBalance());
    }

    @Test
    void instructions_Hacker_receiver_with_Cybersecurity() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A9 = new Instruction(playerID, InstructionType.a9, 100);
        player.addInstruction(A9);

        Instruction A10 = new Instruction(playerID, InstructionType.a10, 0);
        player.addInstruction(A10);


        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100)   , player.getBalance());
    }

    @Test
    void instructions_Hacker_activator_with_Cybersecurity() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A10 = new Instruction(playerID, InstructionType.a10, 100);
        player.addInstruction(A10);

        Instruction A11 = new Instruction(playerID, InstructionType.a11, 0);
        player.addInstruction(A11);


        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100)   , player.getBalance());
    }

    @Test
    void instructions_Hacker_activator_without_Cybersecurity() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A11 = new Instruction(playerID, InstructionType.a11, 100);
        player.addInstruction(A11);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000 + 2*100+100)   , player.getBalance());
    }

    @Test
    void foreign_instruction() throws FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        Instruction A0 = new Instruction(playerID+1, InstructionType.a0, 100);
        player.addInstruction(A0);

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);
        player.endRound(Direction.UP, 1.01);

        assertEquals(round(1000+2*100), player.getBalance());
    }



    @Test
    void placeBet_with_powerups_simple() throws FailedToPlaceBetException, PowerupNotFoundException {
        Long playerID = player.getPlayerID();

        AbstractPowerUp x2 = new PowerupX2(playerID);
        x2 = this.powerupRepository.saveAndFlush(x2);

        player.addPowerup(x2);

        AbstractPowerUp Plus100 = new PowerupPlus100(playerID);
        Plus100 = this.powerupRepository.saveAndFlush(Plus100);

        player.addPowerup(Plus100);

        assertEquals(2, player.getAvailablePowerups().size());
        assertEquals(0, player.getActivePowerups().size());

        AbstractPowerUp finalX2 = x2;
        AbstractPowerUp finalPlus10 = Plus100;

        assertDoesNotThrow(() -> player.activatePowerup(finalX2));
        assertEquals(1, player.getActivePowerups().size());
        assertDoesNotThrow(() -> player.activatePowerup(finalPlus10));

        assertEquals(2, player.getAvailablePowerups().size());
        assertEquals(2, player.getActivePowerups().size());

        List<AbstractPowerUp> activatedPowerups = player.getActivePowerups();

        Game game = new Game();

        for(AbstractPowerUp powerUp: activatedPowerups){
            ArrayList<Instruction> instructions = powerUp.generateInstructions(game);

            for(Instruction instruction: instructions){
                player.addInstruction(instruction);
            }
        }

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(100+1000+2*100*2), player.getBalance());

        assertEquals(0, player.getAvailablePowerups().size());
        assertEquals(0, player.getActivePowerups().size());
    }

    @Test
    void foreign_powerup(){
        Long playerID = player.getPlayerID();

        AbstractPowerUp x2 = new PowerupX2(playerID+1);
        x2 = this.powerupRepository.saveAndFlush(x2);
        AbstractPowerUp finalX = x2;

        assertDoesNotThrow(() -> player.addPowerup(finalX));
        assertThrows(PowerupNotFoundException.class, () -> player.activatePowerup(finalX));
    }

    @Test
    void more_complex_powerups() throws PowerupNotFoundException, FailedToPlaceBetException {
        Long playerID = player.getPlayerID();

        AbstractPowerUp x2 = new PowerupX2(playerID);
        x2 = this.powerupRepository.saveAndFlush(x2);
        player.addPowerup(x2);

        AbstractPowerUp Plus100 = new PowerupPlus100(playerID);
        Plus100 = this.powerupRepository.saveAndFlush(Plus100);
        player.addPowerup(Plus100);

        AbstractPowerUp x5 = new PowerupX5(playerID);
        x5 = this.powerupRepository.saveAndFlush(x5);
        player.addPowerup(x5);

        AbstractPowerUp Plus200 = new PowerupPlus200(playerID);
        Plus200 = this.powerupRepository.saveAndFlush(Plus200);
        player.addPowerup(Plus200);

        AbstractPowerUp Plus500 = new PowerupPlus500(playerID);
        Plus500 = this.powerupRepository.saveAndFlush(Plus500);
        player.addPowerup(Plus500);

        AbstractPowerUp x10 = new PowerupX10(playerID);
        x10 = this.powerupRepository.saveAndFlush(x10);
        player.addPowerup(x10);

        assertEquals(6, player.getAvailablePowerups().size());
        assertEquals(0, player.getActivePowerups().size());

        player.activatePowerup(x2);
        player.activatePowerup(x5);
        player.activatePowerup(Plus100);
        player.activatePowerup(Plus200);

        assertEquals(6, player.getAvailablePowerups().size());
        assertEquals(4, player.getActivePowerups().size());

        Game game = new Game();
        List<AbstractPowerUp> activatedPowerups = player.getActivePowerups();

        for(AbstractPowerUp powerUp: activatedPowerups){
            ArrayList<Instruction> instructions = powerUp.generateInstructions(game);

            for(Instruction instruction: instructions){
                player.addInstruction(instruction);
            }
        }

        Bet validBet = new Bet(Direction.UP, 100);
        player.placeBet(validBet);

        player.endRound(Direction.UP, 1.01);

        assertEquals(round(100+200+1000+2*100*2*5), player.getBalance());

        assertEquals(2, player.getAvailablePowerups().size());
        assertEquals(0, player.getActivePowerups().size());
    }

}
