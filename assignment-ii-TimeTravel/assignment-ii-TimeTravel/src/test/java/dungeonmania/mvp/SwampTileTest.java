package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SwampTileTest {
    @Test
    @Tag("105-1")
    @DisplayName("Testing mercenary gets stuck in swamp")
    public void mercenaryStuckSwamp() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        /**
         *       0  1  2  3  4  5  6  7  8  9
         *    0                 W  W  W
         *    1  P              S  M  W
         *    2                 W  W  W
         *    3
         *    4
         *    5
         *    9                             E
         */
        DungeonResponse res = dmc.newGame("d_swampTileTest_mercenaryStuck", "c_basicGoalsTest_exit");

        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // stuck 1 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // stuck 2 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // move off
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());
    }

        @Test
    @Tag("105-2")
    @DisplayName("Testing if mercenaries take into account movement factor when pathfinding")
    public void mercenariesTakeAccountMovenmentFactorWhenPathfindin() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();

        //       0  1  2  3  4  5  6  7  8  9
        //    W  W  W  W  W  W  W  W  W  W
        //    W  S  S  S  S  S  S  S  S  S
        //    W  M  W  W  W  W  W  W  W  P
        //    W     W                 W
        //    W     W                 W
        //    W     W                 W
        //    W     w  w  w  w  w  w  w


        DungeonResponse res = dmc.newGame("d_swampTileTest_merceriesFindingPath", "c_basicGoalsTest_exit");

        assertEquals(new Position(0, 2), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 3), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 4), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 5), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(0, 6), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

    }

    @Test
    @Tag("105-3")
    @DisplayName("Testing player does not get slowed by swamp tile")
    public void playerIsFine() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();

        //       0  1  2  3  4  5  6  7  8  9
        //       0  P  S        E


        DungeonResponse res = dmc.newGame("d_SwampTile_playerIsFine", "c_basicGoalsTest_exit");

        assertEquals(new Position(1, 0), TestUtils.getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(2, 0), TestUtils.getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(3, 0), TestUtils.getEntities(res, "player").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 0), TestUtils.getEntities(res, "player").get(0).getPosition());

    }

    @Test
    @Tag("105-4")
    @DisplayName("Testing multiple swamp tiles all slow")
    public void multipleSwampWowSoSlow() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();

        /**
         *       0  1  2  3  4  5  6  7  8  9
         *    0                 W  W  W
         *    1  P              S  M  W
         *    2                 W  W  W
         *    3
         *    4
         *    5
         *    9                             E
         */

        DungeonResponse res = dmc.newGame("d__swampTileTest_multipleSwampTile", "c_basicGoalsTest_exit");

        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // stuck 1 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // stuck 2 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // stuck 3 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // stuck 4 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

        // move off
        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(4, 1), TestUtils.getEntities(res, "mercenary").get(0).getPosition());

    }

    @Test
    @Tag("105-5")
    @DisplayName("Test allied mercenary is fine")
    public void alliedMercenaryIsFine() {
        //                                         Wall     Wall    Wall    Wall  Wall
        // P1       P2/Treasure      P3    P4      M4       M3       M2     M1    Wall
        //                                         Wall     Wall    Wall    Wall  Wall
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_swampTileTest_alliedMercennaryIsFine", "c_mercenaryTest_bribeRadius");

        String mercId = TestUtils.getEntitiesStream(res, "mercenary").findFirst().get().getId();
        assertEquals(new Position(8, 1), getMercPos(res));

        // pick up treasure
        res = dmc.tick(Direction.RIGHT);
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(7, 1), getMercPos(res));

        // attempt bribe
        assertDoesNotThrow(() -> dmc.interact(mercId));
        assertEquals(1, TestUtils.getInventory(res, "treasure").size());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(3, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(new Position(5, 1), getMercPos(res));

        res = dmc.tick(Direction.LEFT);
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(2, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        // Meet Swamp Tile
        assertEquals(new Position(4, 1), getMercPos(res));

        res = dmc.tick(Direction.LEFT);
        assertEquals(0, TestUtils.getInventory(res, "treasure").size());
        assertEquals(new Position(1, 1), TestUtils.getEntities(res, "player").get(0).getPosition());
        assertEquals(new Position(3, 1), getMercPos(res));

    }

    @Test
    @Tag("105-6")
    @DisplayName("Testing Zombie gets stuck in swamp")
    public void zombieStuckSwamp() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        /**
         *       0  1  2  3  4  5  6  7  8  9
         *    0                 W  W  W
         *    1  P              S  Z  W
         *    2                 W  W  W
         *    3
         *    4
         *    5
         *    9                             E
         */
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombieStuck", "c_basicGoalsTest_exit");

        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // stuck 1 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // stuck 2 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // move off
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());
    }

    @Test
    @Tag("105-6")
    @DisplayName("Testing Zombie gets stuck in swamp in the start")
    public void initDangerous() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        /**
         *       0  1  2  3  4  5  6  7  8  9
         *    0                 W  W  W
         *    1  P                Z/S W
         *    2                 W  W  W
         *    3
         *    4
         *    5
         *    9                             E
         */
        DungeonResponse res = dmc.newGame("d_swampTileTest_zombieStuck", "c_basicGoalsTest_exit");

        assertEquals(new Position(6, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        res = dmc.tick(Direction.RIGHT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // stuck 1 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // stuck 2 time
        res = dmc.tick(Direction.LEFT);
        assertEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());

        // move off
        res = dmc.tick(Direction.RIGHT);
        assertNotEquals(new Position(5, 1), TestUtils.getEntities(res, "zombie_toast").get(0).getPosition());
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntities(res, "mercenary").get(0).getPosition();
    }

}
