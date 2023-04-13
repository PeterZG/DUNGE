package dungeonmania.entities.playerState;

import dungeonmania.entities.Player;

public class InvincibleState extends PlayerState {
  public InvincibleState(Player player) {
    super(player, true, false);
  }
}
