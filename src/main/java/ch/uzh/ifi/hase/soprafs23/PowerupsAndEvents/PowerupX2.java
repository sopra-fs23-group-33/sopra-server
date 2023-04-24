package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupX2 extends AbstractPowerUp{

    public PowerupX2(){}
    public PowerupX2(Long ownerID, String ownerName){
        super(ownerID,ownerName , PowerupType.X2.getDescription(), PowerupType.X2.getName(), PowerupType.X2);
    }

    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.A2, 2.0);
        instructions.add(instruction);
        return instructions;
    }
}
