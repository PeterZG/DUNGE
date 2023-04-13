package dungeonmania.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

import dungeonmania.entities.Entity;
import dungeonmania.entities.Portal;
import dungeonmania.map.GameMap;
import dungeonmania.map.GraphNode;

public class PathAlgorithm {
  private GameMap map;
  private Map<Position, GraphNode> nodes;

  private Map<Position, Integer> dist;
  private Map<Position, Position> prev;
  private Map<Position, Boolean> visited;
  private PriorityQueue<Position> q;

  public PathAlgorithm(GameMap map, Map<Position, GraphNode> nodes) {
    this.map = map;
    this.nodes = nodes;
  }

  private void init(Position src) {

    dist = new HashMap<>();
    prev = new HashMap<>();
    visited = new HashMap<>();

    prev.put(src, null);
    dist.put(src, 0);

    q = new PriorityQueue<>(
        (x, y) -> Integer.compare(dist.getOrDefault(x, Integer.MAX_VALUE), dist.getOrDefault(y, Integer.MAX_VALUE)));
    q.add(src);
  }

  public Position dijkstraPathFind(Position src, Position dest, Entity entity) {
    // if inputs are invalid, don't move
    if (!nodes.containsKey(src) || !nodes.containsKey(dest))
      return src;

    init(src);

    while (!q.isEmpty()) {
      Position curr = q.poll();
      if (curr.equals(dest) || dist.get(curr) > 200)
        break;
      // check portal
      if (isPortal(nodes, curr)) {
        processPortal(curr, entity);
        continue;
      }
      visited.put(curr, true);
      List<Position> neighbours = curr.getCardinallyAdjacentPositions().stream().filter(p -> !visited.containsKey(p))
          .filter(p -> !nodes.containsKey(p) || nodes.get(p).canMoveOnto(map, entity)).collect(Collectors.toList());

      neighbours.forEach(n -> {
        int newDist = dist.get(curr) + (nodes.containsKey(n) ? nodes.get(n).getWeight() : 1);
        if (newDist < dist.getOrDefault(n, Integer.MAX_VALUE)) {
          q.remove(n);
          dist.put(n, newDist);
          prev.put(n, curr);
          q.add(n);
        }
      });
    }
    return nextPosition(src, dest);
  }

  private boolean isPortal(Map<Position, GraphNode> nodes, Position curr) {
    return nodes.containsKey(curr) && nodes.get(curr).getEntities().stream().anyMatch(Portal.class::isInstance);
  }

  private void processPortal(Position curr, Entity entity) {
    Portal portal = nodes.get(curr).getEntities().stream().filter(Portal.class::isInstance).map(Portal.class::cast)
        .collect(Collectors.toList()).get(0);
    List<Position> teleportDest = portal.getDestPositions(map, entity);
    teleportDest.stream().filter(p -> !visited.containsKey(p)).forEach(p -> {
      dist.put(p, dist.get(curr));
      prev.put(p, prev.get(curr));
      q.add(p);
    });
  }

  private Position nextPosition(Position src, Position dest) {

    Position ret = dest;
    if (prev.get(ret) == null || ret.equals(src))
      return src;
    while (!prev.get(ret).equals(src)) {
      ret = prev.get(ret);
    }
    return ret;
  }
}
