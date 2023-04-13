package dungeonmania.entities.enemies;

import dungeonmania.Game;
import dungeonmania.entities.Entity;
import dungeonmania.entities.Player;
import dungeonmania.entities.collectables.potions.InvincibilityPotion;
import dungeonmania.entities.collectables.potions.InvisibilityPotion;
import dungeonmania.entities.collectables.potions.Potion;
import dungeonmania.map.GameMap;
import dungeonmania.util.Position;

public class MovingStrategy {
    public static Position chooseMovingStrategy(Game game, Entity entity) {
        GameMap map = game.getMap();
        Player player = map.getPlayer();
        Potion potion = player.getEffectivePotion();

        // For Zombie and Mercenary, they are moving away due to Invincivility Potion
        if (potion instanceof InvincibilityPotion) {
            return new MovingAwayStrategy().getNextPosition(map, entity);
        } else {
            // Walking random for zombie or Invisibility Potion
            if (entity instanceof ZombieToast || potion instanceof InvisibilityPotion) {
                return new RandomMovingStrategy().getNextPosition(map, entity);
            } else {
                // walking in dijkstra path as it is Mercenary and not allied
                return map.dijkstraPathFind(entity.getPosition(), player.getPosition(), entity);
            }
        }
    }
}
