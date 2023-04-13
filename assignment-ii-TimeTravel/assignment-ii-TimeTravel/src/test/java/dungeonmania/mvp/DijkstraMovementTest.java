package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DijkstraMovementTest {
    @Test
    @Tag("15-1")
    @DisplayName("Test mercenary moves towards the player following the shortest path")
    public void simpleMercMovement() {
        /*
         * 0    1   2   3   4   5   6
         * 1    E
         * 2        W               W
         * 3    M   W           W   P
         * 4
         */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_dijkstraMovementTest_simpleMercMovement",
                "c_dijkstraMovementTest_simpleMercMovement");

        res = dmc.tick(Direction.UP);
        assertEquals(new Position(1, 4), getMercPos(res));
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(4, 4), getMercPos(res));
        res = dmc.tick(Direction.UP);
        res = dmc.tick(Direction.UP);
        assertEquals(new Position(6, 4), getMercPos(res));
    }

    @Test
    @Tag("15-2")
    @DisplayName("Test mercenary moves towards the player using portals")
    public void mercWithPortals() {
        /*
         * 0    1   2   3   4   5   6   7   8   9   10  11  12  13   14   15  16
         * 1    E   PO      M                               PO       P    W
         * 2                                                    P0
         * 3
         * 4
         * 5
         * 6                                                                    p0
         */
        DungeonManiaController dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_dijkstraMovementTest_mercWithPortals",
                "c_dijkstraMovementTest_mercWithPortals");

        res = dmc.tick(Direction.RIGHT);
        // Merc should move backwards to use portal
        assertEquals(new Position(3, 1), getMercPos(res));
        // in the worset case, the merc were killed in 6 ticks, it should not use portal p0
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        assertEquals(TestUtils.countEntityOfType(res.getEntities(), "mercenary"), 0);
    }

    private Position getMercPos(DungeonResponse res) {
        return TestUtils.getEntityPos(res, "mercenary");
    }
}
