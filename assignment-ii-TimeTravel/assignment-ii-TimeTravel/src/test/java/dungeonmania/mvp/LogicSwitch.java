package dungeonmania.mvp;

import dungeonmania.DungeonManiaController;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogicSwitch {
    @Test
    @Tag("108-1")
    @DisplayName("Test walking onto a wire")
    public void canWalingOnWire() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_LogicSwitchTest_testWalkingOnWire",
                "c_DoorsKeysTest_cannotWalkClosedDoor");
        EntityResponse initPlayer = TestUtils.getPlayer(res).get();

        // create the expected result
        EntityResponse expectedPlayer = new EntityResponse(initPlayer.getId(), initPlayer.getType(), new Position(2, 1),
                false);

        // move player upward
        DungeonResponse actualDungonRes = dmc.tick(Direction.UP);
        EntityResponse actualPlayer = TestUtils.getPlayer(actualDungonRes).get();

        // assert after movement
        assertTrue(TestUtils.entityResponsesEqual(expectedPlayer, actualPlayer));
    }

    @Test
    @Tag("108-2")
    @DisplayName("Test turning on a light bulb and creat four type of loigic rule for light bulb")
    public void turningOnLightBulb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_LogicSwitch_openLightBulb",
                "c_DoorsKeysTest_cannotWalkClosedDoor");

        assertEquals(4, TestUtils.getEntities(res, "light_bulb_off").size());

        res = dmc.tick(Direction.RIGHT);

        // Open Light Bulb
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(3, TestUtils.getEntities(res, "light_bulb_off").size());
    }

    @Test
    @Tag("108-3")
    @DisplayName("Test activating a wire and exploding a bomb")
    public void activatingWireExplodingBomb() throws InvalidActionException {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_logicSwitch_activeWireUseBomb", "c_DoorsKeysTest_cannotWalkClosedDoor");

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(0, TestUtils.getInventory(res, "bomb").size());

        // Pick up bomb
        res = dmc.tick(Direction.UP);

        // Pick up bomb
        res = dmc.tick(Direction.UP);
        assertEquals(1, TestUtils.getInventory(res, "bomb").size());
        res = dmc.tick(TestUtils.getInventory(res, "bomb").get(0).getId());
        res = dmc.tick(Direction.RIGHT);

        assertEquals(0, TestUtils.getInventory(res, "bomb").size());
        assertEquals(0, TestUtils.getEntities(res, "switch").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
    }

    @Test
    @Tag("108-4")
    @DisplayName("Test complex logic switches with AND light bulb")
    public void andLightBulb() {
        DungeonManiaController dmc;
        dmc = new DungeonManiaController();
        DungeonResponse res = dmc.newGame("d_LogicSwitchTest_andLihtBulb", "c_DoorsKeysTest_cannotWalkClosedDoor");

        /**
         *      0  1  2  3  4
         *   0
         *   1  P  B  S  W  L
         *   2              W
         *   3              S
         *   4              B
         */
        // assert that the light bulb is off
        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());

        // push boulder onto first switch
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.DOWN);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);
        res = dmc.tick(Direction.RIGHT);

        assertEquals(1, TestUtils.getEntities(res, "light_bulb_off").size());
        assertEquals(0, TestUtils.getEntities(res, "light_bulb_on").size());
    }
}
