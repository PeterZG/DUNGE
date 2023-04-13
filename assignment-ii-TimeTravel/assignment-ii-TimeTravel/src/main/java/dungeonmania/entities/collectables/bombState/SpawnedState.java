package dungeonmania.entities.collectables.bombState;

import dungeonmania.entities.collectables.Bomb;

public class SpawnedState extends BombState {
    public SpawnedState(Bomb bomb) {
        super(bomb);
    }

    @Override
    public void onOverlap() {
        super.toSpawnedState();
    }
}
