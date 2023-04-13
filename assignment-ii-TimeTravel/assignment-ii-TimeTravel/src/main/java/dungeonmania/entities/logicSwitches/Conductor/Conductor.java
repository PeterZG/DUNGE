package dungeonmania.entities.logicSwitches.Conductor;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Entity;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.logicSwitches.LogicSwitch;
import dungeonmania.entities.logicSwitches.LogicSwitchFormingList;
import dungeonmania.entities.logicSwitches.LogicalEntities.LogicalEntity;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class Conductor extends LogicSwitch  {
    private List<Bomb> subscribers = new ArrayList<>();
    private boolean prevActivated = false;
    private List<Wire> wires = new ArrayList<>();
    private List<LogicalEntity> logicEntities = new ArrayList<>();

    public Conductor(Position position) {
        super(position);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return true;
    }

    public void subscribe(LogicSwitch entity) {
        if (entity instanceof Wire) {
            wires = new LogicSwitchFormingList<Wire>().subscribe((Wire) entity, wires);
        } else if (entity instanceof LogicalEntity) {
            logicEntities = new LogicSwitchFormingList<LogicalEntity>().
                            subscribe((LogicalEntity) entity, logicEntities);
        }
    }

    public void setPrevActive(boolean prevActivated) {
        this.prevActivated = prevActivated;
    }

    public boolean isPrevActived() {
        return prevActivated;
    }

    public List<Conductor> getAdjacentConductors(GameMap map) {
        return super.getAdjacentConductors(map);
    }

    public void activeAllLogicEntities(GameMap map) {
        logicEntities.stream().forEach(e -> e.activate(map));
    }

    public List<Wire> getWires() {
        return wires;
    }

    public List<LogicalEntity> getLogicEntity() {
        return logicEntities;
    }

    public void updateForRemove() {
        deactivate();
        for (Wire wire : this.wires) {
            wire.getWires().remove(this);
        }
        for (LogicalEntity entity : this.logicEntities) {
            entity.disconnect();
        }
    }

    public void deactivate() {
        setActive(false);
        for (Wire wire : this.wires) {
            if (wire.isActivated()) {
                wire.deactivate();
            }
        }
    }

    public void unsubscribe(Bomb bomb) {
        subscribers.remove(bomb);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        this.subscribers.add(bomb);
        bomb.subscribe(this);
        this.activeAllLogicEntities(map);
    }

}
