package dungeonmania.entities.collectables;

import dungeonmania.util.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.bombState.BombState;
import dungeonmania.entities.collectables.bombState.SpawnedState;
import dungeonmania.entities.logicSwitches.Conductor.Conductor;
import dungeonmania.map.GameMap;

public class Bomb extends CollectableEntity {
    private BombState state;

    public static final int DEFAULT_RADIUS = 1;
    private int radius;

    private List<Conductor> subs = new ArrayList<>();

    public Bomb(Position position, int radius) {
        super(position);
        state = new SpawnedState(this);
        this.radius = radius;
    }

    public void subscribe(Conductor s) {
        this.subs.add(s);
    }

    public void notify(GameMap map) {
        explode(map);
    }

    public void onOverlap(GameMap map, Entity entity) {
        if (!(state instanceof SpawnedState))
            return;
        if (entity instanceof Player) {
            if (!((Player) entity).pickUp(this))
                return;
            subs.stream().forEach(s -> s.unsubscribe(this));
            map.destroyEntity(this);
        }
        state.onOverlap();
    }

    public void onPutDown(GameMap map, Position p) {
        translate(Position.calculatePositionBetween(getPosition(), p));
        map.addEntity(this);
        List<Position> adjPosList = getPosition().getCardinallyAdjacentPositions();
        adjPosList.stream().forEach(node -> {
            List<Entity> entities = map.getEntities(node).stream().filter(e -> (e instanceof Conductor))
                    .collect(Collectors.toList());
            entities.stream().map(Conductor.class::cast).forEach(s -> s.subscribe(this, map));
            entities.stream().map(Conductor.class::cast).forEach(s -> this.subscribe(s));
        });
        state.onPutDown();
    }

    public void explode(GameMap map) {
        int x = getPosition().getX();
        int y = getPosition().getY();
        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                List<Entity> entities = map.getEntities(new Position(i, j));
                entities = entities.stream().filter(e -> !(e instanceof Player)).collect(Collectors.toList());
                for (Entity e : entities)
                    map.destroyEntity(e);
            }
        }
    }

    public void changeState(BombState state) {
        this.state = state;
    }

    public List<Conductor> getSubs() {
        return subs;
    }
}
