package ch.uzh.ifi.hase.soprafs23.Powerups;

import ch.uzh.ifi.hase.soprafs23.constant.Currency;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public enum PowerupType {
    X2(100, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID){
            return new PowerupX2(ownerID);
        }
    },
    X5(50, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupX5(ownerID);
        }
    },
    X10(10, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupX10(ownerID);
        }
    },
    Plus100(100, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupPlus100(ownerID);
        }
    },
    Plus200(50, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupPlus200(ownerID);
        }
    },
    Plus500(20, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupPlus500(ownerID);
        }
    },
    Plus1000(10, Arrays.asList(GameType.values())) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupPlus1000(ownerID);
        }
    },
    RiskInsurance(100,Arrays.asList(GameType.values() )){
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupRiskInsurance(ownerID);
        }
    },
    RobinHood(50, List.of(GameType.MULTIPLAYER)){
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupRobinHood(ownerID);
        }
    },
    Guardian(50, List.of(GameType.MULTIPLAYER)){
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupGuardian(ownerID);
        }
    },
    Hacker(50, List.of(GameType.MULTIPLAYER)) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupHacker(ownerID);
        }
    },
    CyberSecurity(50, List.of(GameType.MULTIPLAYER)) {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID) {
            return new PowerupCyberSecurity(ownerID);
        }
    };

    private final int relativeProbability;

    private final List<GameType> gameTypes;

    PowerupType(int relativeProbability, List<GameType> gameTypes){
        this.relativeProbability = relativeProbability;
        this.gameTypes = gameTypes;
    }
    abstract AbstractPowerUp generatePowerup(Long ownerID);

    public static ArrayList<AbstractPowerUp> generatePowerups(int number, Long ownerID, GameType gameType){
        PowerupType[] types = PowerupType.values();
        int total = 0;

        for(PowerupType type: types){
            if(type.getGameTypes().contains(gameType))
                total += type.getRelativeProbability();
        }

        ArrayList<AbstractPowerUp> powerUps = new ArrayList<>();

        for(int i = 0; i < number; i++){
            powerUps.add(randomPowerup(total, ownerID, gameType));
        }

        return powerUps;
    }

    private static AbstractPowerUp randomPowerup(int total, Long ownerID, GameType gameType){
        int random = new Random().nextInt(total);
        List<PowerupType> types = List.of(PowerupType.values());

        int sum = 0;

        for(PowerupType type: types){
            if(!type.getGameTypes().contains(gameType))
                continue;

            sum += type.getRelativeProbability();

            if (sum >= random)
                return type.generatePowerup(ownerID);
        }

        return new PowerupX2(ownerID);
    }

    public  int getRelativeProbability() {
        return relativeProbability;
    }

    public List<GameType> getGameTypes(){
        return this.gameTypes;
    }
}
