package ch.uzh.ifi.hase.soprafs23.Powerups;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupRiskInsurance extends AbstractPowerUp{
    public PowerupRiskInsurance(){}
    public PowerupRiskInsurance(Long ownerID){
        super(ownerID, "this powerup protects you from losses in case of a lost bet", PowerupType.RiskInsurance);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        Instruction instruction = new Instruction(this.ownerID, InstructionType.a4, 1.0);
        instructions.add(instruction);
        return instructions;
    }
}

