package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Entity;
import java.util.ArrayList;

@Entity
public class PowerupRiskInsurance extends AbstractPowerUp{
    public PowerupRiskInsurance(){}
    public PowerupRiskInsurance(Long ownerID, String ownerName){
        super(ownerID, ownerName,  PowerupType.RiskInsurance.getDescription(),  PowerupType.RiskInsurance.getName(), PowerupType.RiskInsurance);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new Instruction(this.ownerID, InstructionType.a4, 1.0));
        return instructions;
    }
}

