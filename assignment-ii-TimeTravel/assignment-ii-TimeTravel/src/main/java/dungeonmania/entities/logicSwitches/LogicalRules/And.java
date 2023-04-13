package dungeonmania.entities.logicSwitches.LogicalRules;

import java.util.List;

import dungeonmania.entities.logicSwitches.Conductor.Conductor;

public class And extends LogicRule {

    @Override
    public boolean checkActivateAble(List<Conductor> list) {
        return getActivedConductorNum(list) >= 2
        && getActivedConductorNum(list) == list.size();
    }
}
