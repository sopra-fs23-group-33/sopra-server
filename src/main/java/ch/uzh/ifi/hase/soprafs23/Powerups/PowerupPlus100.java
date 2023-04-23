package ch.uzh.ifi.hase.soprafs23.Powerups;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import org.springframework.scheduling.annotation.Async;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupPlus100 extends AbstractPowerUp{

    public PowerupPlus100(){}
    public PowerupPlus100(Long ownerID){
        super(ownerID, "this powerup adds 100 coins to your balance", PowerupType.Plus100);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a0, 100);
        instructions.add(instruction);
        return instructions;
    }
}