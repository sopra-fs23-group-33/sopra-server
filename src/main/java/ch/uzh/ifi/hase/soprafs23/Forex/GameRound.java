package ch.uzh.ifi.hase.soprafs23.Forex;

import ch.uzh.ifi.hase.soprafs23.constant.Direction;


import javax.persistence.*;

import java.util.ArrayList;

import static java.lang.Math.max;
import static java.lang.Math.min;

@Entity
public class GameRound  {


    @AttributeOverride(name="numbers",column=@Column(name="numbers_second"))
    @AttributeOverride(name="dates",column=@Column(name="dates_second"))
    @AttributeOverride(name="currencyPair.from",column=@Column(name="currencyPair_from_second"))
    @AttributeOverride(name="currencyPair.to",column=@Column(name="currencyPair_to_second"))
    @Embedded
    private Chart secondChart;
    @Enumerated(EnumType.STRING)
    private Direction outcome;
    @Column(name = "ratio")
    private double ratio;
    @Id
    @GeneratedValue
    private Long roundID;
    @Column(name = "usage")
    private boolean usage;

    public GameRound(){

    }

    public GameRound(Chart chart) {
        this.secondChart = chart;
        Chart firstChart = this.splitChart(this.secondChart);

        Double firstClose = firstChart.getValues().get(firstChart.getValues().size() -1);
        Double secondClose = this.secondChart.getValues().get(secondChart.getValues().size() -1);

        this.ratio = this.computeRatio(firstClose, secondClose);
        this.outcome = this.computeOutcome(firstClose, secondClose);
        this.usage = false;
    }

    private Chart splitChart(Chart chart){
        int length = chart.getValues().size()-1;

        int cutOff = length /2;

        if(length >= 12*24*2) {
            cutOff = length-24*12;
        }

        return  new Chart(new ArrayList<>(chart.getValues().subList(0, cutOff)), new ArrayList<>(chart.getDates().subList(0, cutOff)), chart.getCurrencyPair());
    }

    private Direction computeOutcome(Double firstClose, Double secondClose){
        if(firstClose > secondClose)
            return Direction.DOWN;
        else
            return Direction.UP;
    }

    private double computeRatio(Double firstClose, Double secondClose){
        return max(firstClose, secondClose)/ min(firstClose, secondClose);

    }

    public Chart getFirstChart() {
        return this.splitChart(this.secondChart);
    }

    public Chart getSecondChart() {
        return secondChart;
    }

    public Direction getOutcome() {
        return outcome;
    }

    public double getRatio() {
        return ratio;
    }

    public Long getRoundID() {
        return roundID;
    }

    public void activate(){
        this.usage= true;
    }


}
