package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.enemies.ZombieToastSpawner;

public class EnemyGoal extends Goal {
  private int target;

  public EnemyGoal(int target) {
    this.target = target;
  }

  @Override
  public boolean achieved(Game game) {
    if (!hasPlayer(game)) {
      return false;
    }
    return game.getPlayer().getKillCount() >= target && game.getMap().getEntities(ZombieToastSpawner.class).isEmpty();
  }

  @Override
  public String toString(Game game) {
    return achieved(game) ? "" : ":enemy";
  }
}
