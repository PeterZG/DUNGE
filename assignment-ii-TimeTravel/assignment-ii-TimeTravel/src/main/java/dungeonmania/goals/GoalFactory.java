package dungeonmania.goals;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoalFactory {
  public static Goal createGoal(JSONObject jsonGoal, JSONObject config) {
    JSONArray subgoals;
    switch (jsonGoal.getString("goal")) {
    case "AND":
      subgoals = jsonGoal.getJSONArray("subgoals");
      return new AndGoal(createGoal(subgoals.getJSONObject(0), config), createGoal(subgoals.getJSONObject(1), config));
    case "OR":
      subgoals = jsonGoal.getJSONArray("subgoals");
      return new OrGoal(createGoal(subgoals.getJSONObject(0), config), createGoal(subgoals.getJSONObject(1), config));
    case "exit":
      return new ExitGoal();
    case "boulders":
      return new BouldersGoal();
    case "treasure":
      return new TreasureGoal(config.optInt("treasure_goal", 1));
    case "enemy":
      return new EnemyGoal(config.optInt("enemy_goal", 1));
    default:
      return null;
    }
  }
}
