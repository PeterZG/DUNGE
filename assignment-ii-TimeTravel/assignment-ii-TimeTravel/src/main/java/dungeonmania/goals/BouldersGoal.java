package dungeonmania.goals;

import dungeonmania.Game;
import dungeonmania.entities.logicSwitches.Conductor.Switch;

public class BouldersGoal extends Goal {
  @Override
  public boolean achieved(Game game) {
    return hasPlayer(game) && game.getMap().getEntities(Switch.class).stream().allMatch(s -> s.isActivated());
  }

  @Override
  public String toString(Game game) {
    return achieved(game) ? "" : ":boulders";
  }
}
