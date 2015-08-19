package graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import for_now.EquivalRelation;

public class Edge implements Cloneable {
  private final Vertex start;
  private final Vertex end;

  public enum Direction implements EquivalRelation<Edge> {
    Directed {
      @Override
      public boolean isRelated(Edge edge1, Edge edge2) {
        return (edge1.start == edge2.start) && (edge2.end == edge2.end);
      }
    },

    NonDirected {
      @Override
      public boolean isRelated(Edge edge1, Edge edge2) {
        return (edge1.getTerminalsAsSet().equals(edge2.getTerminalsAsSet()));
      }
    }
  }

  public Edge() {
    this(new Vertex(), new Vertex());
  }

  public Edge(Vertex start, Vertex end) {
    this.start = start;
    this.end = end;
  }

  public Vertex getStart() {
    return start;
  }

  public Vertex getEnd() {
    return end;
  }

  public Set<Vertex> getTerminalsAsSet() {
    Set<Vertex> terminals = new HashSet<Vertex>();
    terminals.add(start);
    terminals.add(end);

    return terminals;
  }

  public Map<Vertex, Vertex> getTerminalAsMap() {
    Map<Vertex, Vertex> terminals = new HashMap<Vertex, Vertex>();
    terminals.put(start, end);

    return terminals;
  }

  @Override
  public String toString() {
    return start.toString() + "->" + end.toString();
  }
}
