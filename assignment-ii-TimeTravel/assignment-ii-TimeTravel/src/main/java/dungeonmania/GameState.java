package dungeonmania;

import java.io.*;

import dungeonmania.response.models.DungeonResponse;

public class GameState {
    private static final String SAVED_GAMES_PATH = "src/main/java/dungeonmania/SavedTheGames/";
    private static final String SAVED_DUNGEON_RESPONSES_PATH = "src/main/java/dungeonmania/SavedDungeonResponses/";

    public static void save(String name, Game game, DungeonResponse dungeonResponse) throws IOException {
        try (ObjectOutputStream gameOutputStream = new ObjectOutputStream(new FileOutputStream(SAVED_GAMES_PATH + name));
             ObjectOutputStream dungeonResponseOutputStream = new ObjectOutputStream(new FileOutputStream(SAVED_DUNGEON_RESPONSES_PATH + name))) {
            gameOutputStream.writeObject(game);
            dungeonResponseOutputStream.writeObject(dungeonResponse);
        } catch (IOException e) {
            throw e;
        }
    }

    public static Game loadGame(String name) throws IOException, ClassNotFoundException {
        try (ObjectInputStream gameInputStream = new ObjectInputStream(new FileInputStream(SAVED_GAMES_PATH + name))) {
            return (Game) gameInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }

    public static DungeonResponse loadDungeonResponse(String name) throws IOException, ClassNotFoundException {
        try (ObjectInputStream dungeonResponseInputStream = new ObjectInputStream(new FileInputStream(SAVED_DUNGEON_RESPONSES_PATH + name))) {
            return (DungeonResponse) dungeonResponseInputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw e;
        }
    }
}
