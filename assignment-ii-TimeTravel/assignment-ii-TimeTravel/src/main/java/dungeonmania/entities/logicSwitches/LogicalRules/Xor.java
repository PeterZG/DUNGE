package dungeonmania.entities.logicSwitches.LogicalRules;

import java.util.List;

import dungeonmania.entities.logicSwitches.Conductor.Conductor;

public class Xor extends LogicRule {
    @Override
    public boolean checkActivateAble(List<Conductor> list) {
        return getActivedConductorNum(list) == 1;
    }
}
