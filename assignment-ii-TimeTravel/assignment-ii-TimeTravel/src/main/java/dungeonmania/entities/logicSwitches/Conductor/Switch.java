package dungeonmania.entities.logicSwitches.Conductor;

import java.util.ArrayList;
import java.util.List;

import dungeonmania.entities.Boulder;
import dungeonmania.entities.DestoryAble;
import dungeonmania.entities.Entity;
import dungeonmania.entities.MoveAwayAble;
import dungeonmania.entities.OverlapAble;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class Switch extends Conductor implements MoveAwayAble, OverlapAble, DestoryAble {
    private List<Bomb> bombs = new ArrayList<>();

    public Switch(Position position) {
        super(position);
    }

    public void subscribe(Bomb b) {
        bombs.add(b);
    }

    public void subscribe(Bomb bomb, GameMap map) {
        bombs.add(bomb);
        if (super.isActivated()) {
            bombs.stream().forEach(b -> b.notify(map));
        }
    }

    public void unsubscribe(Bomb b) {
        bombs.remove(b);
    }

    @Override
    public void onOverlap(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            setActive(true);
            bombs.stream().forEach(b -> b.notify(map));
            getWires().stream().forEach(w -> {
                w.setActived(true, map);
                w.setNumActivatedSwitches(w.getNumActivatedSwitches() + 1);
            });
            getLogicEntity().stream().forEach(e -> e.activate(map));
            getWires().stream().forEach(w -> w.setPrevActive(true));
            setPrevActive(true);
        }
    }

    @Override
    public void onMovedAway(GameMap map, Entity entity) {
        if (entity instanceof Boulder) {
            onDestroy(map);
        }
    }

    public void onDestroy(GameMap map) {
        setActive(false);
        getWires().stream().forEach(w -> {
            w.setNumActivatedSwitches(w.getNumActivatedSwitches() - 1);
            if (w.getNumActivatedSwitches() == 0) {
                w.setActived(false, map);
            }
        });
        getLogicEntity().stream().forEach(e -> e.activate(map));
        getWires().stream().forEach(w -> w.setPrevActive(w.isActivated()));
        setPrevActive(false);
    }

    public void activateConnectedWires(GameMap map) {
        for (Conductor conductor : getAdjacentConductors(map)) {
            if (conductor instanceof Wire) {
                ((Wire) conductor).setActived(true, map);
            }
        }
    }
}
