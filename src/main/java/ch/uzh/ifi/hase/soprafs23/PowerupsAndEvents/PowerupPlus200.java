package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupPlus200 extends AbstractPowerUp{

    public PowerupPlus200(){}
    public PowerupPlus200(Long ownerID, String ownerName){
        super(ownerID, ownerName,  PowerupType.PLUS200.getDescription(),  PowerupType.PLUS200.getName(), PowerupType.PLUS200);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.A0, 200);
        instructions.add(instruction);
        return instructions;
    }
}