package dungeonmania.entities.collectables.bombState;

import dungeonmania.entities.collectables.Bomb;

public abstract class BombState {
    private Bomb bomb;

    public BombState(Bomb bomb) {
        this.bomb = bomb;
    }

    public void onOverlap() {
        toInventoryState();
    }

    public void onPutDown() {
        toPlacedState();
    }
    public void toInventoryState() {
        bomb.changeState(new InventoryState(bomb));
    }

    public void toSpawnedState() {
        bomb.changeState(new SpawnedState(bomb));
    }

    public void toPlacedState() {
        bomb.changeState(new PlacedState(bomb));
    }
}
