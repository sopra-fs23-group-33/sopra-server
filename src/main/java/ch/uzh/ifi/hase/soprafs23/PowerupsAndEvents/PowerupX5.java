package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupX5 extends AbstractPowerUp{

    public PowerupX5(){}
    public PowerupX5(Long ownerID, String ownerName){
        super(ownerID, ownerName, PowerupType.X5.getDescription(), PowerupType.X5.getName(), PowerupType.X5);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a2, 5.0);
        instructions.add(instruction);
        return instructions;
    }
}
