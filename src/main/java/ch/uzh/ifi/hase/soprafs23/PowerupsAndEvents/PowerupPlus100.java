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
        super(ownerID, ownerName, PowerupType.PLUS100.getDescription(), PowerupType.PLUS100.getName(), PowerupType.PLUS100);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.A0, 100);
        instructions.add(instruction);
        return instructions;
    }
}