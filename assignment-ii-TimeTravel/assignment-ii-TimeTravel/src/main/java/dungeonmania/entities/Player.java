package dungeonmania.entities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Random;

import dungeonmania.ConfigReader;
import dungeonmania.battles.BattleStatistics;
import dungeonmania.battles.Battleable;
import dungeonmania.entities.collectables.Bomb;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.entities.enemies.Enemy;
import dungeonmania.entities.enemies.Mercenary;
import dungeonmania.entities.enemies.Spider;
import dungeonmania.entities.inventory.Inventory;
import dungeonmania.entities.inventory.InventoryItem;
import dungeonmania.entities.playerState.BaseState;
import dungeonmania.entities.playerState.PlayerState;
import dungeonmania.map.GameMap;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends Entity implements Battleable, OverlapAble {
  public static final double DEFAULT_ATTACK = 5.0;
  public static final double DEFAULT_HEALTH = 5.0;
  private BattleStatistics battleStatistics;
  private Inventory inventory;
  private Queue<Potion> queue = new LinkedList<>();
  private Potion inEffective = null;
  private int nextTrigger = 0;

  private PlayerState state;

  private int killCount;

  public Player(Position position, double health, double attack) {
    super(position);
    battleStatistics = new BattleStatistics(health, attack, 0, BattleStatistics.DEFAULT_DAMAGE_MAGNIFIER,
        BattleStatistics.DEFAULT_PLAYER_DAMAGE_REDUCER);
    inventory = new Inventory();
    state = new BaseState(this);
  }

  public int getCollectedTreasureCount() {
    return inventory.count(Treasure.class);
  }

  public boolean hasWeapon() {
    return inventory.hasWeapon();
  }

  public BattleItem getWeapon() {
    return inventory.getWeapon();
  }

  public List<String> getBuildables() {
    return inventory.getBuildables();
  }

  public boolean build(String entity, EntityFactory factory) {
    InventoryItem item = inventory.checkBuildCriteria(this, true, entity.equals("shield"), factory);
    if (item == null)
      return false;
    return inventory.add(item);
  }

  public void move(GameMap map, Direction direction) {
    this.setFacing(direction);
    map.moveTo(this, Position.translateBy(this.getPosition(), direction));
  }

  @Override
  public void onOverlap(GameMap map, Entity entity) {
    if (entity instanceof Enemy) {
      if (entity instanceof Mercenary) {
        if (((Mercenary) entity).isAllied())
          return;
      }
      map.getGame().battle(this, (Enemy) entity);
    }
  }

  @Override
  public boolean canMoveOnto(GameMap map, Entity entity) {
    return true;
  }

  public Entity getEntity(String itemUsedId) {
    return inventory.getEntity(itemUsedId);
  }

  public boolean pickUp(Entity item) {
    return inventory.add((InventoryItem) item);
  }

  public Inventory getInventory() {
    return inventory;
  }

  public Potion getEffectivePotion() {
    return inEffective;
  }

  public <T extends InventoryItem> void use(Class<T> itemType) {
    T item = inventory.getFirst(itemType);
    if (item != null)
      inventory.remove(item);
  }

  public void use(Bomb bomb, GameMap map) {
    inventory.remove(bomb);
    bomb.onPutDown(map, getPosition());
  }

  public void triggerNext(int currentTick) {
    if (queue.isEmpty()) {
      inEffective = null;
      state.transitionBase();
      return;
    }
    inEffective = queue.remove();
    if (inEffective instanceof InvincibilityPotion) {
      state.transitionInvincible();
    } else {
      state.transitionInvisible();
    }
    nextTrigger = currentTick + inEffective.getDuration();
  }

  public void changeState(PlayerState playerState) {
    state = playerState;
  }

  public void use(Potion potion, int tick) {
    inventory.remove(potion);
    queue.add(potion);
    if (inEffective == null) {
      triggerNext(tick);
    }
  }

  public void onTick(int tick) {
    if (inEffective == null || tick == nextTrigger) {
      triggerNext(tick);
    }
  }

  public void remove(InventoryItem item) {
    inventory.remove(item);
  }

  @Override
  public BattleStatistics getBattleStatistics() {
    return battleStatistics;
  }

  public <T extends InventoryItem> int countEntityOfType(Class<T> itemType) {
    return inventory.count(itemType);
  }

  public BattleStatistics applyBuff(BattleStatistics origin) {
    if (state.isInvincible()) {
      return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, 0, 1, 1, true, true));
    } else if (state.isInvisible()) {
      return BattleStatistics.applyBuff(origin, new BattleStatistics(0, 0, 0, 1, 1, false, false));
    }
    return origin;
  }

  public Key getKey() {
    return inventory.getFirst(Key.class);
  }

  public int getKeyNum() {
    return inventory.getKeyNum();
  }

  public Position getPositionsForSpider(Spider dummySpider, Random rng, GameMap map) {
    Position position = getPosition();
    int radius = ConfigReader.getValue("distanceToCreateSpider", Integer.class);
    List<Position> availablePos = new ArrayList<>();

    for (int i = position.getX() - radius; i < position.getX() + radius; i++) {
      for (int j = position.getY() - radius; j < position.getY() + radius; j++) {
        if (Position.calculatePositionBetween(position, new Position(i, j)).magnitude() > radius)
          continue;
        Position np = new Position(i, j);
        if (!map.canMoveTo(dummySpider, np) || np.equals(position))
          continue;
        if (map.getEntities(np).stream().anyMatch(e -> e instanceof Enemy))
          continue;
        availablePos.add(np);
      }
    }
    return availablePos.get(rng.nextInt(availablePos.size()));
  }

  public int getKillCount() {
    return killCount;
  }

  public void killOneEnemy() {
    killCount++;
  }

}