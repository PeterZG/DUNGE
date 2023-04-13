package dungeonmania.entities.logicSwitches.Conductor;

import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Wire extends Conductor {
    private int activatedSwitchesNum = 0;

    public Wire(Position position) {
        super(position);
    }

    public void setActived(boolean activated, GameMap map) {
        super.setActive(activated);
        super.activeAllLogicEntities(map);
    }

    public int getNumActivatedSwitches() {
        return activatedSwitchesNum;
    }

    public void setNumActivatedSwitches(int numActivatedSwitches) {
        this.activatedSwitchesNum = numActivatedSwitches;
    }

    // Happened when Conductor is onOverlap
    public void onOverlap(GameMap map) {
        setActived(true, map);
        setNumActivatedSwitches(activatedSwitchesNum + 1);
        setPrevActive(true);
    }

}
