package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface OverlapAndDepart {
    public void onOverlap(GameMap map, Entity entity);
    public void onMovedAway(GameMap map, Entity entity);
}
