package dungeonmania.battles;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Game;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.ResponseBuilder;
import dungeonmania.util.NameConverter;

public class BattleFacade {
  private List<BattleResponse> battleResponses = new ArrayList<>();

  public void battle(Game game, Player player, Enemy enemy) {
    // 0. init
    double initialPlayerHealth = player.getBattleStatistics().getHealth();
    double initialEnemyHealth = enemy.getBattleStatistics().getHealth();
    String enemyString = NameConverter.toSnakeCase(enemy);

    // 1. apply buff provided by the game and player's inventory
    List<BattleItem> battleItems = new ArrayList<>();
    BattleStatistics playerBuff = getPlayerBuff(player, battleItems, game);

    // 2. Battle the two stats
    BattleStatistics playerBattleStatistics = getPlayerBattleStatistics(player, playerBuff);
    BattleStatistics enemyBattleStatistics = enemy.getBattleStatistics();
    if (!playerBattleStatistics.isEnabled() || !enemyBattleStatistics.isEnabled())
      return;
    List<BattleRound> rounds = BattleStatistics.battle(playerBattleStatistics, enemyBattleStatistics);

    // 3. update health to the actual statistics
    updateHealth(player, enemy, playerBattleStatistics, enemyBattleStatistics);

    // 4. decrease durability of items
    decreaseDurability(battleItems, game);

    // 5. Log the battle - consolidate it to be a battle response
    logBattleResponse(enemyString, rounds, battleItems, initialPlayerHealth, initialEnemyHealth);
  }

  public List<BattleResponse> getBattleResponses() {
    return battleResponses;
  }

  private BattleStatistics getPlayerBuff(Player player, List<BattleItem> battleItems, Game game) {
    Potion effectivePotion = player.getEffectivePotion();
    if (effectivePotion != null) {
      return player.applyBuff(new BattleStatistics(0, 0, 0, 1, 1));
    } else {
      BattleStatistics playerBuff = new BattleStatistics(0, 0, 0, 1, 1);
      for (BattleItem item : player.getInventory().getEntities(BattleItem.class)) {
        if (item instanceof Potion)
          continue;
        playerBuff = item.applyBuff(playerBuff);
        battleItems.add(item);
      }
      List<Mercenary> mercs = game.getMap().getEntities(Mercenary.class);
      for (Mercenary merc : mercs) {
        if (!merc.isAllied())
          continue;
        playerBuff = BattleStatistics.applyBuff(playerBuff, merc.getBattleStatistics());
      }
      return playerBuff;
    }
  }

  private BattleStatistics getPlayerBattleStatistics(Player player, BattleStatistics playerBuff) {
    BattleStatistics playerBaseStatistics = player.getBattleStatistics();
    return BattleStatistics.applyBuff(playerBaseStatistics, playerBuff);
  }

  private void updateHealth(Player player, Enemy enemy, BattleStatistics playerBattleStatistics,
      BattleStatistics enemyBattleStatistics) {
    player.getBattleStatistics().setHealth(playerBattleStatistics.getHealth());
    enemy.getBattleStatistics().setHealth(enemyBattleStatistics.getHealth());
    if (player.getBattleStatistics().getHealth() > 0 && enemy.getBattleStatistics().getHealth() <= 0) {
      player.killOneEnemy();
    }
  }

  private void decreaseDurability(List<BattleItem> battleItems, Game game) {
    for (BattleItem item : battleItems) {
      if (item instanceof InventoryItem) {
        item.use(game);
      }
    }
  }

  private void logBattleResponse(String enemyString, List<BattleRound> rounds, List<BattleItem> battleItems,
      double initialPlayerHealth, double initialEnemyHealth) {
    battleResponses
        .add(new BattleResponse(enemyString,
            rounds.stream().map(ResponseBuilder::getRoundResponse).collect(Collectors.toList()), battleItems.stream()
                .map(Entity.class::cast).map(ResponseBuilder::getItemResponse).collect(Collectors.toList()),
            initialPlayerHealth, initialEnemyHealth));
  }

}
