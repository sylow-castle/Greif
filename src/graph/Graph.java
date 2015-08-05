package graph;

import java.util.Set;

public interface Graph {
  public abstract Set<Vertex> getAllVertexAsSet();
  public abstract Set<Edge> getAllEdgeAsSet();

}