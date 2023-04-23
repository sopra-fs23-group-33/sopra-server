package ch.uzh.ifi.hase.soprafs23.Powerup;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.Powerups.*;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PowerupTest {

    @Test
    void generate_powerup_for_Singleplayer() {
        List<AbstractPowerUp> powerUps = PowerupType.generatePowerups(1000, 1L, GameType.SINGLEPLAYER);
        assertEquals(1000, powerUps.size());


        for (AbstractPowerUp abstractPowerUp : powerUps) {
            assertTrue(abstractPowerUp.getPowerupType().getGameTypes().contains(GameType.SINGLEPLAYER));
            assertEquals(1L, abstractPowerUp.getOwnerID());
        }
    }

    @Test
    void generate_powerup_for_Multiplayer() {
        List<AbstractPowerUp> powerUps = PowerupType.generatePowerups(1000, 1L, GameType.MULTIPLAYER);
        assertEquals(1000, powerUps.size());

        for (AbstractPowerUp abstractPowerUp : powerUps) {
            assertTrue(abstractPowerUp.getPowerupType().getGameTypes().contains(GameType.MULTIPLAYER));
            assertEquals(1L, abstractPowerUp.getOwnerID());
        }
    }

    @Test
    void powerupX2() {
        AbstractPowerUp powerUp = new PowerupX2(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.X2, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a2, createdInstruction.getType());
        assertEquals(2.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void powerupX5() {
        AbstractPowerUp powerUp = new PowerupX5(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.X5, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a2, createdInstruction.getType());
        assertEquals(5.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void powerupX10() {
        AbstractPowerUp powerUp = new PowerupX10(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.X10, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a2, createdInstruction.getType());
        assertEquals(10.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void powerupPlus100() {
        AbstractPowerUp powerUp = new PowerupPlus100(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.Plus100, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a0, createdInstruction.getType());
        assertEquals(100.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void powerupPlus200() {
        AbstractPowerUp powerUp = new PowerupPlus200(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.Plus200, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a0, createdInstruction.getType());
        assertEquals(200.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void powerupPlus500() {
        AbstractPowerUp powerUp = new PowerupPlus500(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.Plus500, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a0, createdInstruction.getType());
        assertEquals(500.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void powerupPlus1000() {
        AbstractPowerUp powerUp = new PowerupPlus1000(1L);

        assertEquals(1L, powerUp.getOwnerID());
        assertEquals(PowerupType.Plus1000, powerUp.getPowerupType());
        assertFalse(powerUp.getDescription().isEmpty());

        Game game = new Game();

        List<Instruction> instructions = powerUp.generateInstructions(game);

        assertEquals(1, instructions.size());

        Instruction createdInstruction = instructions.get(0);

        assertEquals(InstructionType.a0, createdInstruction.getType());
        assertEquals(1000.0, createdInstruction.getNumber());
        assertEquals(1L, createdInstruction.getOwnerID());
    }

    @Test
    void equals_powerups(){
        PowerupX2 powerupX2 = new PowerupX2(1L);
        powerupX2.setPowerupID(2L);

        PowerupX2 anotherPowerupX2 = new PowerupX2(1L);
        anotherPowerupX2.setPowerupID(2L);

        PowerupX2 aThirdPowerupX2 = new PowerupX2(1L);
        aThirdPowerupX2.setPowerupID(3L);

        assertTrue(powerupX2.equals(powerupX2));
        assertTrue(powerupX2.equals(anotherPowerupX2));
        assertFalse(powerupX2.equals(null));
        assertFalse(powerupX2.equals(new User()));
        assertFalse(powerupX2.equals(aThirdPowerupX2));

        assertEquals(powerupX2.hashCode(), anotherPowerupX2.hashCode());
    }

    @Test
    void activation_of_powerup(){
        PowerupX2 powerupX2 = new PowerupX2(1L);
        assertFalse(powerupX2.isActive());

        powerupX2.activate();

        assertTrue(powerupX2.isActive());
        powerupX2.activate();
        assertTrue(powerupX2.isActive());
    }

}
