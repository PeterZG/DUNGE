package dungeonmania.entities.playerState;

import dungeonmania.entities.Player;

public abstract class PlayerState {
  private Player player;
  private boolean isInvincible = false;
  private boolean isInvisible = false;

  PlayerState(Player player, boolean isInvincible, boolean isInvisible) {
    this.player = player;
    this.isInvincible = isInvincible;
    this.isInvisible = isInvisible;
  }

  public boolean isInvincible() {
    return isInvincible;
  };

  public boolean isInvisible() {
    return isInvisible;
  };

  public Player getPlayer() {
    return player;
  }

  public void transitionBase() {
    player.changeState(new BaseState(player));
  }

  public void transitionInvincible() {
    player.changeState(new InvincibleState(player));
  }

  public void transitionInvisible() {
    player.changeState(new InvisibleState(player));
  }
}
