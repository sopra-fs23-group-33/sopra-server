package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupPlus1000 extends AbstractPowerUp{

    public PowerupPlus1000(){}
    public PowerupPlus1000(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.PLUS1000.getDescription(), PowerupType.PLUS1000.getName(), PowerupType.PLUS1000);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.A0, 1000);
        instructions.add(instruction);
        return instructions;
    }
}

