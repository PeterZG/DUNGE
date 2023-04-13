package dungeonmania.entities.logicSwitches.LogicalRules;

import java.util.List;

import dungeonmania.entities.logicSwitches.Conductor.Conductor;

public abstract class LogicRule {

    public int getActivedConductorNum(List<Conductor> conductors) {
        int activedConductorNum = 0;

        for (Conductor conductor : conductors) {
            if (conductor.isActivated()) {
                activedConductorNum += 1;
            }
        }

        return activedConductorNum;
    }

    public abstract boolean checkActivateAble(List<Conductor> conductors);
}
