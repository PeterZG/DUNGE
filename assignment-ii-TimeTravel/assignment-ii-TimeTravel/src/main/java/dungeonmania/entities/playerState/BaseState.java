package dungeonmania.entities.playerState;

import dungeonmania.entities.Player;

public class BaseState extends PlayerState {
  public BaseState(Player player) {
    super(player, false, false);
  }

  @Override
  public void transitionBase() {
    // Do nothing
  }
}
