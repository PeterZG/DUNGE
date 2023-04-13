package dungeonmania.entities.collectables.potions;

import dungeonmania.entities.collectables.CollectableEntity;
import dungeonmania.util.Position;

public abstract class Potion extends CollectableEntity {
    private int duration;

    public Potion(Position position, int duration) {
        super(position);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }
}
