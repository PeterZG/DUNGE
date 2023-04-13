package dungeonmania.entities;

import dungeonmania.map.GameMap;

import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.util.Position;

public class Door extends Entity implements OverlapAble {
    private boolean open = false;
    private int number;

    public Door(Position position, int number) {
        super(position.asLayer(Entity.DOOR_LAYER));
        this.number = number;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (open || entity instanceof Spider) {
            return true;
        }
        return (entity instanceof Player && hasKey((Player) entity));
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (!(entity instanceof Player))
            return;

        Player player = (Player) entity;
        Key key = player.getKey();

        if (hasKey(player)) {
            player.remove(key);
            open();
        }
    }

    private boolean hasKey(Player player) {
        if (player.getKey() != null) return player.getKeyNum() == number;
        return false;
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }
}
