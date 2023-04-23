package ch.uzh.ifi.hase.soprafs23.Powerups;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;
@Entity
public class PowerupX10 extends AbstractPowerUp{

    public PowerupX10(){}
    public PowerupX10(Long ownerID){
        super(ownerID, "this powerup multiplies your gain or loss by 10", PowerupType.X10);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a2, 10.0);
        instructions.add(instruction);
        return instructions;
    }
}
