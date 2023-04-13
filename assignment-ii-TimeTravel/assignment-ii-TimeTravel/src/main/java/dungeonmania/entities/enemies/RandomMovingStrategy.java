package dungeonmania.entities.enemies;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;
import dungeonmania.entities.Entity;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

public class RandomMovingStrategy implements Moving {
    @Override
    public Position getNextPosition(GameMap map, Entity entity) {
        List<Position> pos = entity.getPosition().getCardinallyAdjacentPositions();
        pos = pos.stream().filter(p -> map.canMoveTo(entity, p)).collect(Collectors.toList());
        return (pos.isEmpty()) ? entity.getPosition() : pos.get(new Random().nextInt(pos.size()));
    }
}
