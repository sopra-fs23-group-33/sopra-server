package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupPlus500 extends AbstractPowerUp{

    public PowerupPlus500(){}
    public PowerupPlus500(Long ownerID, String ownerName){
        super(ownerID,ownerName,  PowerupType.Plus500.getDescription(), PowerupType.Plus500.getName(), PowerupType.Plus500);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a0, 500);
        instructions.add(instruction);
        return instructions;
    }
}