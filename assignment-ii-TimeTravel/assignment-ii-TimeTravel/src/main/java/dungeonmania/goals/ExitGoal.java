package dungeonmania.goals;

import java.util.List;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Exit;
import dungeonmania.entities.Player;
import dungeonmania.util.Position;

public class ExitGoal extends Goal {
  @Override
  public boolean achieved(Game game) {
    if (!hasPlayer(game))
      return false;
    Player character = game.getPlayer();
    Position pos = character.getPosition();
    List<Exit> es = game.getMap().getEntities(Exit.class);
    if (es == null || es.size() == 0)
      return false;
    return es.stream().map(Entity::getPosition).anyMatch(pos::equals);
  }

  @Override
  public String toString(Game game) {
    return achieved(game) ? "" : ":exit";
  }
}
