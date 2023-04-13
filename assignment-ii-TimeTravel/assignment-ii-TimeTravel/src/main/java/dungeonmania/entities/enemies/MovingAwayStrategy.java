package dungeonmania.entities.enemies;

import dungeonmania.util.Position;
import dungeonmania.util.Direction;
import dungeonmania.entities.Entity;
import dungeonmania.map.GameMap;

public class MovingAwayStrategy implements Moving {
    @Override
    public Position getNextPosition(GameMap map, Entity entity) {
        Position currPos = entity.getPosition();
        Position plrDiff = Position.calculatePositionBetween(map.getPlayer().getPosition(), currPos);
        Position moveX = (plrDiff.getX() >= 0) ? Position.translateBy(currPos, Direction.RIGHT)
                : Position.translateBy(currPos, Direction.LEFT);
        Position moveY = (plrDiff.getY() >= 0) ? Position.translateBy(currPos, Direction.UP)
                : Position.translateBy(currPos, Direction.DOWN);

        return (Math.abs(plrDiff.getX()) >= Math.abs(plrDiff.getY()))
                    ? (map.canMoveTo(entity, moveX) ? moveX : (map.canMoveTo(entity, moveY) ? moveY : currPos))
                    : (map.canMoveTo(entity, moveY) ? moveY : (map.canMoveTo(entity, moveX) ? moveX : currPos));
    }

}

