package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupPlus100 extends AbstractPowerUp{

    public PowerupPlus100(){}
    public PowerupPlus100(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.Plus100.getDescription(), PowerupType.Plus100.getName(), PowerupType.Plus100);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a0, 100);
        instructions.add(instruction);
        return instructions;
    }
}