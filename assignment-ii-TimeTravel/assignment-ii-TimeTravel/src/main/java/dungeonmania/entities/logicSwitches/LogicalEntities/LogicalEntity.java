package dungeonmania.entities.logicSwitches.LogicalEntities;

import java.util.List;

import dungeonmania.entities.logicSwitches.LogicSwitch;
import dungeonmania.entities.logicSwitches.Conductor.Conductor;
import dungeonmania.entities.logicSwitches.LogicalRules.LogicRule;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class LogicalEntity extends LogicSwitch {
    private LogicRule logicRule;

    public LogicalEntity(Position position, LogicRule logicRule) {
        super(position);
        this.logicRule = logicRule;
    }

    public LogicRule getLogicRule() {
        return logicRule;
    }


    public void activate(GameMap map) {
        List<Conductor> adjConductors = getAdjacentConductors(map);

        setActive(getLogicRule().checkActivateAble(adjConductors));
    }

    public void disconnect() {
        super.setActive(false);
    }

}
