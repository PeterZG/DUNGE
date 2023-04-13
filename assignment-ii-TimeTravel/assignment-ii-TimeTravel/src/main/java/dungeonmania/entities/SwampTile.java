package dungeonmania.entities;

import dungeonmania.entities.enemies.Enemy;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwampTile extends Entity implements OverlapAble {
    public static final int DEFAULT_SWAMP_DURATION = 2;

    private int stuckNum = DEFAULT_SWAMP_DURATION;

    public SwampTile(Position position, int stuckNum) {
        super(position.asLayer(Entity.FLOOR_LAYER));
        this.stuckNum = stuckNum;
    }

    public int getSutckNum() {
        return stuckNum;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Enemy) {
            Enemy enemy = (Enemy) entity;
            enemy.setStuckNum(stuckNum);
        }
    }

    public void addStuckNum(int num) {
        stuckNum += num;
    }
}
