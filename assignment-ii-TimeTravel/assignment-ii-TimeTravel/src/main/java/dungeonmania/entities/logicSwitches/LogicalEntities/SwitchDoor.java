package dungeonmania.entities.logicSwitches.LogicalEntities;

import dungeonmania.entities.Entity;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.logicSwitches.LogicalRules.LogicRule;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class SwitchDoor extends LogicalEntity {
    public SwitchDoor(Position position, LogicRule logicRule) {
        super(position, logicRule);
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        if (super.isActivated() || entity instanceof Spider) return true;
        return false;
    }
}
