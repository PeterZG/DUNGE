package dungeonmania.entities;

import dungeonmania.util.Position;
import dungeonmania.map.GameMap;

public class TimeTravelPortal extends Entity {

    private boolean isPortalActive = false;
    private boolean isPortalBroken = false;

    public TimeTravelPortal(Position position) {
        super(position);
    }

    public boolean isActive() {
        return isPortalActive;
    }

    @Override
    public boolean canMoveOnto(GameMap map, Entity entity) {
        return isPlayer(entity);
    }


    public void onEntityOverlap(GameMap map, Entity entity) {
        if (isPlayer(entity) && !isPortalBroken) {
            isPortalActive = true;
            isPortalBroken = true;
        }
    }


    public void onEntityMovedAway(GameMap map, Entity entity) {
        if (isPlayer(entity)) {
            isPortalActive = false;
        }
    }

    private boolean isPlayer(Entity entity) {
        return entity instanceof Player;
    }
}
