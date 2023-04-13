package dungeonmania.entities.enemies;

import dungeonmania.util.Position;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public interface Moving {
    public Position getNextPosition(GameMap map, Entity entity);
}
