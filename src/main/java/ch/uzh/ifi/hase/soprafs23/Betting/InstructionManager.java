package ch.uzh.ifi.hase.soprafs23.Betting;

import ch.uzh.ifi.hase.soprafs23.Forex.Chart;
import ch.uzh.ifi.hase.soprafs23.constant.Direction;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;


@Entity(name = "PlayerManager")
@Table(name = "player_manager")
public class InstructionManager {

    @Id
    @GeneratedValue
    private Long InstructionManagerID;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Instruction> instructions;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_playerID")
    private Player player;


    public InstructionManager(){
    }

    public void init(Player player) {
        this.player = player;
        this.instructions = new ArrayList<>();

    }

    public void addInstruction(Instruction instruction){
        if(this.player.getPlayerID() == instruction.getOwnerID())
            this.instructions.add(instruction);
    }

    public int computeNewBalance(Direction direction, double ratio){

        double outcome = 0.0;
        Direction betDirection = this.player.getCurrentBet().getDirection();
        double betAmount = this.player.getCurrentBet().getAmount();

        if(betDirection.equals(direction))
            outcome = 1.0;
        else if(!betDirection.equals(Direction.NONE))
            outcome = -1.0;

        HashMap<InstructionType, Double> temp = this.getMap();

        for(Instruction instruction: this.instructions){
            InstructionType type = instruction.getType();
            temp.put(type, instruction.compute(temp.get(type)));
        }

        double newBalance = temp.get(InstructionType.a0) +
                            temp.get(InstructionType.a1)*this.player.getBalance() +
                            outcome*(((ratio-1.0)*100+1)*betAmount*temp.get(InstructionType.a2) + temp.get(InstructionType.a3));

        return max((int) newBalance, 0);
    }

    private HashMap<InstructionType, Double> getMap(){
        HashMap<InstructionType, Double> temp = new HashMap<>();

        for(InstructionType type: InstructionType.values()){
            temp.put(type, type.getDefaultValue());
        }
        return temp;
    }

}
