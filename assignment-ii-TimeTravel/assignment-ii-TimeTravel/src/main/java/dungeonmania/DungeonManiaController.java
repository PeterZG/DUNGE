package dungeonmania;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.IOException;

import org.json.JSONException;

import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.Direction;
import dungeonmania.util.FileLoader;

/**
 * DO NOT CHANGE METHOD SIGNITURES OF THIS FILE
 * */
public class DungeonManiaController {
    private Game game = null;
    private DungeonResponse dR = null;
    private List<Game> SnapShotsForGame = new ArrayList<>();
    private List<DungeonResponse> dRSnapShots = new ArrayList<>();

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    public Game giveGame() {
        return game;
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * /game/new
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException {
        if (!dungeons().contains(dungeonName)) {
            throw new IllegalArgumentException(dungeonName + " is not a dungeon that exists");
        }

        if (!configs().contains(configName)) {
            throw new IllegalArgumentException(configName + " is not a configuration that exists");
        }

        try {
            GameBuilder builder = new GameBuilder();
            game = builder.setConfigName(configName).setDungeonName(dungeonName).buildGame();
            dR = ResponseBuilder.getDungeonResponse(game);
            SnapShotsForGame.clear();
            dRSnapShots.clear();
            return dR;
        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return dR;
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        saveSnapshot();
        dR = ResponseBuilder.getDungeonResponse(game.tick(itemUsedId));
        return dR;
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        saveSnapshot();
        dR = ResponseBuilder.getDungeonResponse(game.tick(movementDirection));
        if (game.TimeTravelPortalForPlayer()) {
            dR = rewind(30);
        }
        return dR;
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        saveSnapshot();
        List<String> validBuildables = List.of("bow", "shield", "midnight_armour", "sceptre");
        if (!validBuildables.contains(buildable)) {
            throw new IllegalArgumentException("Only bow, shield, midnight_armour and sceptre can be built");
        }
        return ResponseBuilder.getDungeonResponse(game.build(buildable));
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        saveSnapshot();
        dR = ResponseBuilder.getDungeonResponse(game.interact(entityId));
        return dR;
    }

    public DungeonResponse saveGame(String name) {
        try {
            GameState.save(name, game, dR);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dR;
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        try {
            Game loadedGame = GameState.loadGame(name);
            DungeonResponse loadedResponse = GameState.loadDungeonResponse(name);
            if (loadedGame != null && loadedResponse != null) {
                game = loadedGame;
                dR = loadedResponse;
                SnapShotsForGame.clear();
                dRSnapShots.clear();
                return dR;
            } else {
                throw new IllegalArgumentException("Failed to load game with name: " + name);
            }
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
            return null; // or handle the exception in some other way
        }
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        File savedGamesDir = new File("src/main/java/dungeonmania/savedGames/");
        if (!savedGamesDir.exists()) {
            savedGamesDir.mkdirs();
        }
        return Stream.of(savedGamesDir.listFiles())
            .map(File::getName)
            .collect(Collectors.toList());
    }

    /**
     * /game/new/generate
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName)
            throws IllegalArgumentException {
            return null;
    }

    /**
     * /game/dungeonResponseModel
     */
    private void saveSnapshot() {
        try {
            addGameSnapshot(SnapShotsForGame, game);
            addDungeonResponseSnapshot(dRSnapShots, dR);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addGameSnapshot(List<Game> list, Game object) throws Exception {
        Game copiedObject = (Game) ObjectCopier.copy(object);
        list.add(copiedObject);
    }

    private void addDungeonResponseSnapshot(List<DungeonResponse> list, DungeonResponse object) throws Exception {
        DungeonResponse copiedObject = (DungeonResponse) ObjectCopier.copy(object);
        list.add(copiedObject);
    }

    /**
     * /game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        int tickToRewind = game.calculateRewindTick(ticks);
        if (tickToRewind < 0) {
            throw new IllegalArgumentException("Cannot rewind more than the available history.");
        }

        game = SnapShotsForGame.get(tickToRewind);
        dR = dRSnapShots.get(tickToRewind);
        SnapShotsForGame.subList(tickToRewind, SnapShotsForGame.size()).clear();
        dRSnapShots.subList(tickToRewind, dRSnapShots.size()).clear();

        return dR;
    }

    /**
     * get health of player
     */
    public double getHealth() {
        return game.getPlayer().getBattleStatistics().getHealth();
    }
}

