package dungeonmania.mvp;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dungeonmania.DungeonManiaController;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.Direction;

public class EnemyGoalsTest {
  @Test
  @DisplayName("Test basic enemy goal works")
  public void testBasicEnemyGoal() {
    DungeonManiaController dmc;
    dmc = new DungeonManiaController();
    DungeonResponse res = dmc.newGame("d_enemyGoalsTest_basic", "c_enemyGoalsTest_basic");

    // assert goal not met
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));

    // move player to right
    res = dmc.tick(Direction.RIGHT);

    // assert goal met
    assertEquals("", TestUtils.getGoals(res));

  }

  @Test
  @DisplayName("Test enemy goal with exit goal, exit goal must be achieved last")
  public void testEnemyAndExit() {

    DungeonManiaController dmc;
    dmc = new DungeonManiaController();
    DungeonResponse res = dmc.newGame("d_enemyGoalsTest_exit", "c_enemyGoalsTest_exit");

    // assert goal not met
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // move player to right
    res = dmc.tick(Direction.RIGHT);
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));

    res = dmc.tick(Direction.RIGHT);

    // move player met spider
    res = dmc.tick(Direction.RIGHT);
    assertFalse(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // move player to exit
    res = dmc.tick(Direction.LEFT);
    res = dmc.tick(Direction.LEFT);
    // assert goal met
    assertEquals("", TestUtils.getGoals(res));
  }

  @Test
  @DisplayName("Test enemy goal with spawner on the map")
  public void testEnemyWithSpawner() {

    DungeonManiaController dmc;
    dmc = new DungeonManiaController();
    DungeonResponse res = dmc.newGame("d_enemyGoalsTest_spawner", "c_enemyGoalsTest_spawner");
    String spawnerId = TestUtils.getEntities(res, "zombie_toast_spawner").get(0).getId();
    // assert goal not met
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // move player to right
    res = dmc.tick(Direction.RIGHT);
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));

    res = dmc.tick(Direction.RIGHT);

    // move player met spider
    res = dmc.tick(Direction.RIGHT);
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // Destroy spawner
    res = assertDoesNotThrow(() -> dmc.interact(spawnerId));
    assertEquals(0, TestUtils.countType(res, "zombie_toast_spawner"));
    assertFalse(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // move player to exit
    res = dmc.tick(Direction.LEFT);
    // assert goal met
    assertEquals("", TestUtils.getGoals(res));

  }

  @Test
  @DisplayName("Test enemy goal conjunction other goals")
  public void testConjunctionEnemy() {
    DungeonManiaController dmc;
    dmc = new DungeonManiaController();
    DungeonResponse res = dmc.newGame("d_enemyGoalsTest_conjunction", "c_enemyGoalsTest_conjunction");

    // assert goal not met
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":boulders"));
    assertTrue(TestUtils.getGoals(res).contains(":treasure"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // Move onto exit
    res = dmc.tick(Direction.RIGHT);

    assertTrue(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":boulders"));
    assertTrue(TestUtils.getGoals(res).contains(":treasure"));

    // Kill enemy
    res = dmc.tick(Direction.DOWN);

    assertTrue(TestUtils.getGoals(res).contains(":boulders"));
    assertTrue(TestUtils.getGoals(res).contains(":treasure"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));
    assertFalse(TestUtils.getGoals(res).contains(":enemy"));

    // Push boulder onto switch
    res = dmc.tick(Direction.RIGHT);

    assertTrue(TestUtils.getGoals(res).contains(":treasure"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));
    assertFalse(TestUtils.getGoals(res).contains(":boulders"));
    assertFalse(TestUtils.getGoals(res).contains(":enemy"));

    // Collect treasure
    res = dmc.tick(Direction.UP);

    assertTrue(TestUtils.getGoals(res).contains(":exit"));
    assertFalse(TestUtils.getGoals(res).contains(":treasure"));
    assertFalse(TestUtils.getGoals(res).contains(":boulders"));
    assertFalse(TestUtils.getGoals(res).contains(":enemy"));

    // Move back onto exit
    res = dmc.tick(Direction.LEFT);

    // assert goal met
    assertEquals("", TestUtils.getGoals(res));

  }

  @Test
  @DisplayName("Test enemy goal disjunction other goals")
  public void testDisjunctionEnemy() {
    DungeonManiaController dmc;
    dmc = new DungeonManiaController();
    DungeonResponse res = dmc.newGame("d_enemyGoalsTest_disjunction", "c_enemyGoalsTest_disjunction");

    // assert goal not met
    assertTrue(TestUtils.getGoals(res).contains(":enemy"));
    assertTrue(TestUtils.getGoals(res).contains(":boulders"));
    assertTrue(TestUtils.getGoals(res).contains(":treasure"));
    assertTrue(TestUtils.getGoals(res).contains(":exit"));

    // Kill enemy
    res = dmc.tick(Direction.DOWN);

    // assert goal met
    assertEquals("", TestUtils.getGoals(res));

  }

}
