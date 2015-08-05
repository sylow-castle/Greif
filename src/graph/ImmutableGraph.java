package graph;

import java.util.HashSet;
import java.util.Set;

public class ImmutableGraph implements Graph {
  private final Set<Vertex> vertices;
  private final Set<Edge> edges;

  public ImmutableGraph(Set<Vertex> vertices, Set<Edge> edges) {
    this.vertices = new HashSet<Vertex>(vertices);
    this.edges = new HashSet<Edge>(edges);
  }

  @Override
  public Set<Vertex> getAllVertexAsSet() {
    return new HashSet<Vertex>(this.vertices);
  }

  @Override
  public Set<Edge> getAllEdgeAsSet() {
    return new HashSet<Edge>(this.edges);
  }
}
