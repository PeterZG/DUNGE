package dungeonmania.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.Boulder;
import dungeonmania.entities.DestoryAble;
import dungeonmania.entities.Entity;
import dungeonmania.entities.MoveAwayAble;
import dungeonmania.entities.OverlapAble;
import dungeonmania.entities.Player;
import dungeonmania.entities.Portal;
import dungeonmania.entities.SwampTile;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.ZombieToastSpawner;
import dungeonmania.entities.logicSwitches.Conductor.Conductor;
import dungeonmania.entities.logicSwitches.Conductor.Switch;
import dungeonmania.entities.logicSwitches.Conductor.Wire;
import dungeonmania.entities.logicSwitches.LogicalEntities.LogicalEntity;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.entities.TimeTravelPortal;


public class GameMap {
    private Game game;
    private Map<Position, GraphNode> nodes = new HashMap<>();
    private Player player;

    /**
     * Initialise the game map
     * 1. pair up portals
     * 2. register all movables
     * 3. register all spawners
     * 4. register bombs and switches
     * 5. more...
     */
    public void init() {
        initPairPortals();
        initRegisterMovables();
        initRegisterSpawners();
        initRegisterBombsAndSwitches();
        initLogicEntities();
        initConductors();
        initOpenUsingBoulder();
        iniOverlap();
    }

    private void initRegisterBombsAndSwitches() {
        List<Bomb> bombs = getEntities(Bomb.class);
        List<Switch> switchs = getEntities(Switch.class);
        for (Bomb b : bombs) {
            for (Switch s : switchs) {
                if (Position.isAdjacent(b.getPosition(), s.getPosition())) {
                    b.subscribe(s);
                    s.subscribe(b);
                }
            }
        }
    }

    // Pair up portals if there's any
    private void initPairPortals() {
        Map<String, Portal> portalsMap = new HashMap<>();
        nodes.forEach((k, v) -> {
            v.getEntities().stream().filter(Portal.class::isInstance).map(Portal.class::cast).forEach(portal -> {
                String color = portal.getColor();
                if (portalsMap.containsKey(color)) {
                    portal.bind(portalsMap.get(color));
                } else {
                    portalsMap.put(color, portal);
                }
            });
        });
    }

    private void initRegisterMovables() {
        List<Enemy> enemies = getEntities(Enemy.class);
        enemies.forEach(e -> {
                game.register(() -> e.move(game), Game.AI_MOVEMENT, e.getId());
        });
    }

    private void initRegisterSpawners() {
        List<ZombieToastSpawner> zts = getEntities(ZombieToastSpawner.class);
        zts.forEach(e -> {
            game.register(() -> e.spawn(game), Game.AI_MOVEMENT, e.getId());
        });
        game.register(() -> game.getEntityFactory().spawnSpider(game), Game.AI_MOVEMENT, "spawnSpiders");
    }

    private void initLogicEntities() {
        List<Conductor> conductors = getEntities(Conductor.class);
        List<LogicalEntity> logicEntities = getEntities(LogicalEntity.class);
        for (LogicalEntity e : logicEntities) {
            for (Conductor conductor : conductors) {
                if (e.getAdjacentConductors(game.getMap()).contains(conductor)) {
                    conductor.subscribe(e);
                }
            }
        }
    }

    public void initOpenUsingBoulder() {
        List<Boulder> boulders = getEntities(Boulder.class);
        List<Entity> overlapEntities = getEntities().stream().filter(entity -> entity instanceof OverlapAble)
                .map(entity -> (Entity) entity).collect(Collectors.toList());
        for (Boulder boulder : boulders) {
            for (Entity entity : overlapEntities) {
                if (boulder.getPosition().equals(entity.getPosition())) {
                    ((OverlapAble) entity).onOverlap(game.getMap(), boulder);
                }
            }
        }
    }

    private void initConductors() {
        List<Switch> switches = getEntities(Switch.class);
        List<Wire> wires = getEntities(Wire.class);
        switches.forEach(s -> wires.stream()
                .filter(w -> s.getAdjacentConductors(game.getMap()).contains(w) && !s.getWires().contains(w))
                .flatMap(w -> addLinkedWires(w).stream()).distinct().filter(w2 -> !s.getWires().contains(w2))
                .forEach(s::subscribe));
        List<Conductor> conductors = getEntities(Conductor.class);
        for (Conductor conductor1 : conductors) {
            for (Conductor conductor2 : conductors) {
                if (conductor1.getAdjacentConductors(game.getMap()).contains(conductor2)) {
                    conductor1.subscribe(conductor2);
                    conductor2.subscribe(conductor1);
                }
            }
        }
    }

    public void iniOverlap() {
        List<SwampTile> swampTiles = getEntities(SwampTile.class);
        List<Enemy> enemies = getEntities(Enemy.class);
        for (SwampTile swampTile : swampTiles) {
            for (Enemy enemy : enemies) {
                if (swampTile.getPosition().equals(enemy.getPosition())) {
                    ((OverlapAble) swampTile).onOverlap(this, enemy);
                }
            }

        }
    }

    private List<Wire> addLinkedWires(Wire w) {
        List<Wire> prevWires = new ArrayList<>();
        return addLinkedWiresHelper(w, prevWires);
    }

    private List<Wire> addLinkedWiresHelper(Wire w, List<Wire> prevWires) {
        List<Wire> wires = getEntities(Wire.class);
        wires.stream().filter(w2 -> w.getAdjacentConductors(game.getMap()).contains(w2) && !prevWires.contains(w2))
                .forEach(w2 -> {
                    prevWires.add(w2);
                    addLinkedWiresHelper(w2, prevWires);
                });
        return prevWires;
    }

    public void moveTo(Entity entity, Position position) {
        if (!canMoveTo(entity, position))
            return;

        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.setPosition(position);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    public void moveTo(Entity entity, Direction direction) {
        if (!canMoveTo(entity, Position.translateBy(entity.getPosition(), direction)))
            return;
        triggerMovingAwayEvent(entity);
        removeNode(entity);
        entity.translate(direction);
        addEntity(entity);
        triggerOverlapEvent(entity);
    }

    private void triggerMovingAwayEvent(Entity entity) {
        List<Runnable> callbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity && e instanceof MoveAwayAble)
                callbacks.add(() -> ((MoveAwayAble) e).onMovedAway(this, entity));
        });
        callbacks.forEach(callback -> {
            callback.run();
        });
    }

    private void triggerOverlapEvent(Entity entity) {
        List<Runnable> overlapCallbacks = new ArrayList<>();
        getEntities(entity.getPosition()).forEach(e -> {
            if (e != entity && e instanceof OverlapAble)
                overlapCallbacks.add(() -> ((OverlapAble) e).onOverlap(this, entity));
        });
        overlapCallbacks.forEach(callback -> {
            callback.run();
        });
    }

    public boolean canMoveTo(Entity entity, Position position) {
        return !nodes.containsKey(position) || nodes.get(position).canMoveOnto(this, entity);
    }

    public Position dijkstraPathFind(Position src, Position dest, Entity entity) {
        // if inputs are invalid, don't move
        if (!nodes.containsKey(src) || !nodes.containsKey(dest))
            return src;

        Map<Position, Integer> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Boolean> visited = new HashMap<>();

        prev.put(src, null);
        dist.put(src, 0);

        PriorityQueue<Position> q = new PriorityQueue<>((x, y) -> Integer
                .compare(dist.getOrDefault(x, Integer.MAX_VALUE), dist.getOrDefault(y, Integer.MAX_VALUE)));
        q.add(src);

        while (!q.isEmpty()) {
            Position curr = q.poll();
            if (curr.equals(dest) || dist.get(curr) > 200)
                break;
            // check portal
            if (nodes.containsKey(curr) && nodes.get(curr).getEntities().stream().anyMatch(Portal.class::isInstance)) {
                Portal portal = nodes.get(curr).getEntities().stream().filter(Portal.class::isInstance)
                        .map(Portal.class::cast).collect(Collectors.toList()).get(0);
                List<Position> teleportDest = portal.getDestPositions(this, entity);
                teleportDest.stream().filter(p -> !visited.containsKey(p)).forEach(p -> {
                    dist.put(p, dist.get(curr));
                    prev.put(p, prev.get(curr));
                    q.add(p);
                });
                continue;
            }
            visited.put(curr, true);
            List<Position> neighbours = curr.getCardinallyAdjacentPositions().stream()
                    .filter(p -> !visited.containsKey(p))
                    .filter(p -> !nodes.containsKey(p) || nodes.get(p).canMoveOnto(this, entity))
                    .collect(Collectors.toList());

            neighbours.forEach(n -> {
                int newDist = dist.get(curr) + (nodes.containsKey(n) ? nodes.get(n).getWeight() : 1);
                List<Entity> entities = new ArrayList<>();
                if (nodes.get(n) != null) {
                    entities = nodes.get(n).getEntities();
                }
                for (Entity e : entities) {
                    if (e instanceof SwampTile) {
                        SwampTile swampTile = (SwampTile) e;
                        newDist += swampTile.getSutckNum();
                    }
                }
                if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
                    q.remove(n);
                    dist.put(n, newDist);
                    prev.put(n, curr);
                    q.add(n);
                }
            });
        }
        Position ret = dest;
        if (prev.get(ret) == null || ret.equals(src))
            return src;
        while (!prev.get(ret).equals(src)) {
            ret = prev.get(ret);
        }
        return ret;
    }

    public void removeNode(Entity entity) {
        Position p = entity.getPosition();
        if (nodes.containsKey(p)) {
            nodes.get(p).removeEntity(entity);
            if (nodes.get(p).size() == 0) {
                nodes.remove(p);
            }
        }
    }

    public void destroyEntity(Entity entity) {
        removeNode(entity);
        if (entity instanceof DestoryAble) {
            ((DestoryAble) entity).onDestroy(this);
        }
    }

    public void addEntity(Entity entity) {
        addNode(new GraphNode(entity));
    }

    public void addNode(GraphNode node) {
        Position p = node.getPosition();

        if (!nodes.containsKey(p))
            nodes.put(p, node);
        else {
            GraphNode curr = nodes.get(p);
            curr.mergeNode(node);
            nodes.put(p, curr);
        }
    }

    public Entity getEntity(String id) {
        Entity res = null;
        for (Map.Entry<Position, GraphNode> entry : nodes.entrySet()) {
            List<Entity> es = entry.getValue().getEntities().stream().filter(e -> e.getId().equals(id))
                    .collect(Collectors.toList());
            if (es != null && es.size() > 0) {
                res = es.get(0);
                break;
            }
        }
        return res;
    }

    public List<Entity> getEntities(Position p) {
        GraphNode node = nodes.get(p);
        return (node != null) ? node.getEntities() : new ArrayList<>();
    }

    public List<Entity> getEntities() {
        List<Entity> entities = new ArrayList<>();
        nodes.forEach((k, v) -> entities.addAll(v.getEntities()));
        return entities;
    }

    public <T extends Entity> List<T> getEntities(Class<T> type) {
        return getEntities().stream().filter(type::isInstance).map(type::cast).collect(Collectors.toList());
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }
        /**
     * Check if the player is on an active TimeTravellingPortal.
     *
     * @return true if the player is on an active TimeTravellingPortal, false otherwise.
     */
    public boolean TimeTravelPortalForPlayer() {
        // Get all TimeTravellingPortal entities in the game map
        List<TimeTravelPortal> portals = getEntities(TimeTravelPortal.class);

        // Iterate through each portal
        for (TimeTravelPortal portal : portals) {
            // If the portal is active, return true
            if (portal.isActive()) {
                return true;
            }
        }
        // If no active portal is found, return false
        return false;
    }

}
