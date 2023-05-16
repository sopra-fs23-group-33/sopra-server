package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.constant.GameType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public enum PowerupType {
    X2(150, "X2", Arrays.asList(GameType.values()), "this powerup doubles your gain or loss") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName){
            return new PowerupX2(ownerID, ownerName);
        }
    },
    X5(100, "X5", Arrays.asList(GameType.values()), "this powerup multiplies your gain or loss by 5") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupX5(ownerID, ownerName);
        }
    },
    X10(50, "X10",Arrays.asList(GameType.values()), "this powerup multiplies your gain or loss by 10") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupX10(ownerID, ownerName);
        }
    },
    PLUS100(100,"Plus 100", Arrays.asList(GameType.values()), "this powerup adds 100 coins to your balance") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupPlus100(ownerID, ownerName);
        }
    },
    PLUS200(50,"Plus 200", Arrays.asList(GameType.values()), "this powerup adds 200 coins to your balance") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupPlus200(ownerID,ownerName);
        }
    },
    PLUS500(20, "Plus 500", Arrays.asList(GameType.values()), "this powerup adds 500 coins to your balance") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupPlus500(ownerID, ownerName);
        }
    },
    PLUS1000(20,"Plus 1000", Arrays.asList(GameType.values()), "this powerup adds 1000 coins to your balance") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupPlus1000(ownerID, ownerName);
        }
    },
    RISK_INSURANCE(150,"Risk-insurance", Arrays.asList(GameType.values()),"this powerup protects you from losses in case of a lost bet"){
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupRiskInsurance(ownerID, ownerName);
        }
    },
    ROBIN_HOOD(100,"Robin Hood", List.of(GameType.MULTIPLAYER), "this powerup allows you to steal form the leading player and distribute among the others"){
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupRobinHood(ownerID, ownerName);
        }
    },
    GUARDIAN(100, "Guardian", List.of(GameType.MULTIPLAYER), "this powerup protects you from Robin Hood"){
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupGuardian(ownerID, ownerName);
        }
    },
    HACKER(100, "Hacker", List.of(GameType.MULTIPLAYER), "this powerup allows you to steal from the leading player") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupHacker(ownerID, ownerName);
        }
    },
    CYBER_SECURITY(100, "Cyber Security", List.of(GameType.MULTIPLAYER),"this powerup protects you from the hacker") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupCyberSecurity(ownerID, ownerName);
        }
    },

    RISK_REWARD100(100, "High Risk High Reward 100", Arrays.asList(GameType.values()),"this powerup gives you 100 coins if you win and takes 100 coins if you loose") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupHighRiskHighReward100(ownerID,ownerName);
        }
    },

    RISK_REWARD200(50, "High Risk High Reward 200", Arrays.asList(GameType.values()),"this powerup gives you 200 coins if you win and takes 200 coins if you loose") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupHighRiskHighReward200(ownerID,ownerName);
        }
    },
    RISK_REWARD500(20, "High Risk High Reward 500", Arrays.asList(GameType.values()),"this powerup gives you 500 coins if you win and takes 500 coins if you loose") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupHighRiskHighReward500(ownerID, ownerName);
        }
    },
    LIFE_LINE(100, "Lifeline", List.of(GameType.MULTIPLAYER), "this powerup restores half of your initial balance if you are bankrupt") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID, String ownerName) {
            return new PowerupLifeLine(ownerID, ownerName);
        }
    },
    INCENDIARY(100, "Incendiary", List.of(GameType.MULTIPLAYER), "this powerup sets a random players holdings on fire!") {
        @Override
        public AbstractPowerUp generatePowerup(Long ownerID,String ownerName) {
            return new PowerupIncendiary(ownerID, ownerName);
        }
    };

    private final int relativeProbability;

    private final String name;

    private final String description;

    private final List<GameType> gameTypes;

    PowerupType(int relativeProbability, String name, List<GameType> gameTypes, String description){
        this.relativeProbability = relativeProbability;
        this.gameTypes = gameTypes;
        this.name = name;
        this.description = description;

    }
    abstract AbstractPowerUp generatePowerup(Long ownerID, String ownerName);

    public static ArrayList<AbstractPowerUp> generatePowerups(int number, Long ownerID, String ownerName, GameType gameType){
        int total = 0;

        for(PowerupType type: PowerupType.values()){
            if(type.getGameTypes().contains(gameType))
                total += type.getRelativeProbability();
        }


        ArrayList<AbstractPowerUp> powerUps = new ArrayList<>();

        for(int i = 0; i < number; i++){
            powerUps.add(randomPowerup(total, ownerID, ownerName, gameType));
        }

        return powerUps;
    }

    private static Random randomGenerator = new Random();
    private static AbstractPowerUp randomPowerup(int total, Long ownerID,String ownerName,  GameType gameType){
        int random = randomGenerator.nextInt(total);
        int sum = 0;

        for(PowerupType type: PowerupType.values()){
            if(!type.getGameTypes().contains(gameType))
                continue;

            sum += type.getRelativeProbability();

            if (sum >= random)
                return type.generatePowerup(ownerID, ownerName);
        }

        return new PowerupX2(ownerID, ownerName);
    }

    public  int getRelativeProbability() {
        return relativeProbability;
    }

    public List<GameType> getGameTypes(){
        return this.gameTypes;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
