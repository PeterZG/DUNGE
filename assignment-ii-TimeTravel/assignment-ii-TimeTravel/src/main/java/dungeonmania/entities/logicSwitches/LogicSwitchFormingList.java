package dungeonmania.entities.logicSwitches;

import java.util.List;

public class LogicSwitchFormingList<T extends LogicSwitch> {

    public List<T> subscribe(T entity, List<T> lists) {
        lists.add(entity);
        return lists;
    }
}
