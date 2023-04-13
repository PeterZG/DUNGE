package dungeonmania.entities.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



import dungeonmania.ConfigReader;
import dungeonmania.entities.BattleItem;
import dungeonmania.entities.Entity;
import dungeonmania.entities.EntityFactory;
import dungeonmania.entities.Player;
import dungeonmania.entities.buildables.Bow;
import dungeonmania.entities.collectables.Arrow;
import dungeonmania.entities.collectables.Key;
import dungeonmania.entities.collectables.Sword;
import dungeonmania.entities.collectables.Treasure;
import dungeonmania.entities.collectables.Wood;


public class Inventory {
    private List<InventoryItem> items = new ArrayList<>();
    private static int woodNumForBow = ConfigReader.getValue("woodNumForBow", Integer.class);
    private static int arrowNumForBow = ConfigReader.getValue("arrowNumForBow", Integer.class);
    private static int woodNumForShield = ConfigReader.getValue("woodNumForShield", Integer.class);
    private static int treasureNumForShield = ConfigReader.getValue("treasureNumForShield", Integer.class);
    private static int keyNumForShield = ConfigReader.getValue("keyNumForShield", Integer.class);

    public boolean add(InventoryItem item) {
        return items.add(item);
    }

    public void remove(InventoryItem item) {
        items.remove(item);
    }

    public List<String> getBuildables() {

        int wood = count(Wood.class);
        int arrows = count(Arrow.class);
        int treasure = count(Treasure.class);
        int keys = count(Key.class);
        List<String> result = new ArrayList<>();

        if (wood >= woodNumForBow && arrows >= arrowNumForBow) {
            result.add("bow");
        }
        if (wood >= woodNumForShield && (treasure >= treasureNumForShield || keys >= keyNumForShield)) {
            result.add("shield");
        }
        return result;
    }



    public InventoryItem checkBuildCriteria(Player p, boolean remove, boolean forceShield, EntityFactory factory) {
        List<Wood> wood = getEntities(Wood.class);
        List<Arrow> arrows = getEntities(Arrow.class);
        List<Treasure> treasure = getEntities(Treasure.class);
        List<Key> keys = getEntities(Key.class);

        if (canBuildBow(wood, arrows, forceShield)) {
            return buildBow(wood, arrows, remove, factory);
        } else if (canBuildShield(wood, treasure, keys)) {
            return buildShield(wood, treasure, keys, remove, factory);
        } else {
            return null;
        }
    }




    public <T extends InventoryItem> T getFirst(Class<T> itemType) {
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                return itemType.cast(item);
        return null;
    }

    public <T extends InventoryItem> int count(Class<T> itemType) {
        int count = 0;
        for (InventoryItem item : items)
            if (itemType.isInstance(item))
                count++;
        return count;
    }

    public Entity getEntity(String itemUsedId) {
        for (InventoryItem item : items)
            if (((Entity) item).getId().equals(itemUsedId))
                return (Entity) item;
        return null;
    }

    public List<Entity> getEntities() {
        return items.stream().map(Entity.class::cast).collect(Collectors.toList());
    }

    public <T> List<T> getEntities(Class<T> clz) {
        return items.stream().filter(clz::isInstance).map(clz::cast).collect(Collectors.toList());
    }

    public boolean hasWeapon() {
        return getFirst(Sword.class) != null || getFirst(Bow.class) != null;
    }

    public BattleItem getWeapon() {
        BattleItem weapon = getFirst(Sword.class);
        if (weapon == null)
            return getFirst(Bow.class);
        return weapon;
    }

    private boolean canBuildBow(List<Wood> wood, List<Arrow> arrows, boolean forceShield) {
        return wood.size() >= woodNumForBow && arrows.size() >= arrowNumForBow && !forceShield;
    }



    private boolean canBuildShield(List<Wood> wood, List<Treasure> treasure, List<Key> keys) {
        return wood.size() >= woodNumForShield
                && (treasure.size() >= treasureNumForShield || keys.size() >= keyNumForShield);
    }




    private InventoryItem buildBow(List<Wood> wood, List<Arrow> arrows, boolean remove, EntityFactory factory) {
        if (canBuildBow(wood, arrows, false)) {
            for (int i = 0; i < arrowNumForBow; i++) {
                remove(arrows.get(i));
            }
            for (int i = 0; i < woodNumForBow; i++) {
                remove(wood.get(i));
            }
            return factory.buildBow();
        }
        return null;
    }



    private InventoryItem buildShield(List<Wood> wood, List<Treasure> treasure,
    List<Key> keys, boolean remove, EntityFactory factory) {
        if (canBuildShield(wood, treasure, keys)) {

            for (int i = 0; i < woodNumForShield; i++) {
                remove(wood.get(i));
            }

            int treasureCount = treasure.size() >= treasureNumForShield ? treasureNumForShield : 0;
            for (int i = 0; i < treasureCount; i++) {
                remove(treasure.get(i));
            }

            int keyCount = keys.size() >= keyNumForShield ? keyNumForShield : 0;
            for (int i = 0; i < keyCount; i++) {
                remove(keys.get(i));
            }

            return factory.buildShield();
        }
        return null;
    }

    public int getKeyNum() {
        return getFirst(Key.class).getnumber();
    }
}



