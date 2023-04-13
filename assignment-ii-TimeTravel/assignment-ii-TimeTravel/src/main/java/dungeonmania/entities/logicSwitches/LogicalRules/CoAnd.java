package dungeonmania.entities.logicSwitches.LogicalRules;

import java.util.List;

import dungeonmania.entities.logicSwitches.Conductor.Conductor;

public class CoAnd extends LogicRule {

    @Override
    public boolean checkActivateAble(List<Conductor> list) {
        int activedConductorNum = 0;

        for (Conductor conductor : list) {
            if (conductor.isActivated() && !conductor.isPrevActived()) {
                activedConductorNum += 1;
            }
        }
        return activedConductorNum >= 2;
    }
}
