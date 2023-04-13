package dungeonmania.entities.enemies;

import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.DestoryAble;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Interactable;
import dungeonmania.entities.Player;
import dungeonmania.entities.Wall;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends Entity implements Interactable, DestoryAble {
  public static final int DEFAULT_SPAWN_INTERVAL = 0;

  public ZombieToastSpawner(Position position, int spawnInterval) {
    super(position);
  }

  public void spawn(Game game) {
    game.getEntityFactory().spawnZombie(game, this);
  }

  @Override
  public void onDestroy(GameMap map) {
    Game g = map.getGame();
    g.unsubscribe(getId());
  }

  @Override
  public void interact(Player player, Game game) {
    player.getInventory().getWeapon().use(game);
    game.getMap().destroyEntity(this);
  }

  @Override
  public boolean isInteractable(Player player) {
    return Position.isAdjacent(player.getPosition(), getPosition()) && player.hasWeapon();
  }

  public List<Position> getPositionsForZombie(GameMap map) {
    return getPosition().getCardinallyAdjacentPositions().stream()
        .filter(p -> !map.getEntities(p).stream().anyMatch(e -> (e instanceof Wall))).collect(Collectors.toList());
  }

}
