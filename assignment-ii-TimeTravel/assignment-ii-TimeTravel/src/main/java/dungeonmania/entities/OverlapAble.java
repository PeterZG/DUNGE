package dungeonmania.entities;

import dungeonmania.map.GameMap;

public interface OverlapAble {

    public void onOverlap(GameMap map, Entity entity);

}
