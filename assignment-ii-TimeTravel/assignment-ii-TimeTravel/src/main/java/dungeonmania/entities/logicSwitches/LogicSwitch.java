package dungeonmania.entities.logicSwitches;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.entities.logicSwitches.Conductor.Conductor;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public abstract class LogicSwitch extends Entity {
    private boolean activated = false;

    public LogicSwitch(Position position) {
        super(position.asLayer(Entity.ITEM_LAYER));
    }

    public boolean isActivated() {
        return activated;
    }

    public void setActive(boolean activated) {
        this.activated = activated;
    }

    public List<Conductor> getAdjacentConductors(GameMap map) {
        List<Position> adjacentPositionList = super.getPosition().getCardinallyAdjacentPositions();
        return adjacentPositionList.stream()
                .flatMap(position -> map.getEntities(position).stream())
                .filter(e -> e instanceof Conductor)
                .map(e -> (Conductor) e).collect(Collectors.toList());
    }
}
