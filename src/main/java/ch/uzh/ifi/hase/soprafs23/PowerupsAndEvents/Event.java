package ch.uzh.ifi.hase.soprafs23.PowerupsAndEvents;

import ch.uzh.ifi.hase.soprafs23.Betting.Instruction;
import ch.uzh.ifi.hase.soprafs23.Data.EventData;
import ch.uzh.ifi.hase.soprafs23.Game.Game;
import ch.uzh.ifi.hase.soprafs23.constant.GameType;
import ch.uzh.ifi.hase.soprafs23.constant.InstructionType;
import ch.uzh.ifi.hase.soprafs23.entity.Player;

import java.util.*;

public enum Event {

    NO_EVENT(400, "none", List.of(GameType.values()), "none"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            return  new ArrayList<>();
        }
    },
    TAX(100, "Wealth tax", List.of(GameType.values()), "Every player gets 10% deducted from his balance"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for(Player player: players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A1, 0.9));

            return instructions;
        }
    },

    INTEREST(100, "Interest", List.of(GameType.values()), "Every player gets 10% interest on his balance"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for(Player player: players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A1, 1.1));

            return instructions;
        }
    },

    STIMULUS(100, "Stimulus", List.of(GameType.values()), "Every player receives 200 coins"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for(Player player: players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A0, 200));

            return instructions;
        }
    },

    ROBBER(100, "Robber", List.of(GameType.values()), "Every player looses 200 coins"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for(Player player: players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A0, -200));

            return instructions;
        }
    },

    BAIL_OUT(100, "Bailout", List.of(GameType.values()), "players who lost their bet don't take a loss from it"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for(Player player: players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A4, 1.0));

            return instructions;
        }
    },

    WINNERS_WIN_MORE(100, "Winners win more", List.of(GameType.values()), "winning players win twice as much with their bet"){
        @Override
        public ArrayList<Instruction> generateInstructions(Game game){
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for(Player player: players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A16, 1.0));

            return instructions;
        }
    },
    LOOSERS_LOOSE_MORE(100, "Losers loose more", List.of(GameType.values()), "loosing players loose twice as much with their bet") {
        @Override
        public ArrayList<Instruction> generateInstructions(Game game) {
            ArrayList<Instruction> instructions = new ArrayList<>();

            List<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            for (Player player : players)
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A17, 1.0));

            return instructions;
        }
    },

    TOHUWABOHU(200, "Tohuwabohu", List.of(GameType.MULTIPLAYER), "the account balances get inverted and all active powerups and bets are ignored") {
        @Override
        public ArrayList<Instruction> generateInstructions(Game game) {
            ArrayList<Instruction> instructions = new ArrayList<>();

            ArrayList<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            if(players.size() < 2)
                return instructions;

            players.sort(Comparator.comparingDouble(Player::getBalance).reversed().thenComparing(Player::getPlayerID));

            ArrayList<Player> playersReversed = new ArrayList<>();
            playersReversed.addAll(players); //create deep copy
            Collections.reverse(playersReversed);


            int i = 0;
            for (Player player : players){
                if(i >= playersReversed.size())
                    i = playersReversed.size()-1;

                Player revserse = playersReversed.get(i);

                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A18, revserse.getBalance()));
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A19,1));

                i++;
            }

            return instructions;
        }
    },

    BACKTOROOTS(100, "Back to the roots", List.of(GameType.MULTIPLAYER), "all balances get reset to 1000 coins") {
        @Override
        public ArrayList<Instruction> generateInstructions(Game game) {
            ArrayList<Instruction> instructions = new ArrayList<>();

            ArrayList<Player> players = new ArrayList<>();
            players.addAll(game.getPlayers()); //create deep copy

            if(players.size() < 2)
                return instructions;

            for (Player player : players){
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A18, 1000));
                instructions.add(new Instruction(player.getPlayerID(), InstructionType.A19,1));
            }

            return instructions;
        }
    };

    private final int relativeProbability;
    private final String name;
    private final String description;
    private final List<GameType> gameTypes;

    Event(int relativeProbability, String name, List<GameType> gameTypes, String description) {
        this.relativeProbability = relativeProbability;
        this.gameTypes = gameTypes;
        this.name = name;
        this.description = description;
    }

    public abstract ArrayList<Instruction> generateInstructions(Game game);

    private static Random randomGenerator = new Random();

    public static Event generateRandomEvent(GameType gameType){
        int total = Event.totalProbability(gameType);
        int sum = 0;
        int random = randomGenerator.nextInt(total);

        for(Event type: Event.values()){
            if(!type.getGameTypes().contains(gameType))
                continue;

            sum += type.getRelativeProbability();

            if (sum >= random)
                return type;
        }

        return Event.TAX;
    }

    private static int totalProbability(GameType gameType){
        int total = 0;

        for(Event type: Event.values()){
            if(type.getGameTypes().contains(gameType))
                total += type.getRelativeProbability();
        }
        return total;
    }

    public int getRelativeProbability() {
        return relativeProbability;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<GameType> getGameTypes() {
        return gameTypes;
    }

    public EventData getEventData(){
        EventData eventData = new EventData();

        eventData.setDescription(this.getDescription());
        eventData.setName(this.getName());
        return eventData;
    }
}
