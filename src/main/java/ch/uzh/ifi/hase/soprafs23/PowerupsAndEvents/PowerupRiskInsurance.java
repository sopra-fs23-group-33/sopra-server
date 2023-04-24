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
        super(ownerID, ownerName,  PowerupType.RISK_INSURANCE.getDescription(),  PowerupType.RISK_INSURANCE.getName(), PowerupType.RISK_INSURANCE);
    }


    @Override
    public ArrayList<Instruction> generateInstructions(Game game) {
        ArrayList<Instruction> instructions = new ArrayList<>();

        instructions.add(new Instruction(this.ownerID, InstructionType.A4, 1.0));
        return instructions;
    }
}

