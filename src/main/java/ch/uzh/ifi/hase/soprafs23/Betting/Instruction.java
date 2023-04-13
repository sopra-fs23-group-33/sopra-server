package ch.uzh.ifi.hase.soprafs23.Betting;

import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Instruction {

    @Column(name = "ownerID")
    private long ownerID;
    @Enumerated(EnumType.STRING)
    private InstructionType type;
    @Column(name = "number")
    private double number;

    public Instruction(Long ownerID, InstructionType type, double number){
        this.number = number;
        this.ownerID = ownerID;
        this.type = type;
    }

    public Instruction(){}

    public InstructionType getType() {
        return type;
    }

    public double getNumber() {
        return number;
    }

    public long getOwnerID() {
        return ownerID;
    }

    public Double compute(Double a){
        return this.type.compute(a, this.number);
    }
}
