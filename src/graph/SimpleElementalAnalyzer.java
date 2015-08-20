package graph;

import for_now.Utilities;
import graph.Edge.Direction;

import java.util.HashSet;
import java.util.Set;

public class SimpleElementalAnalyzer implements ElementalAnalyzer<Vertex> {
  private final Graph analyzedGraph;

  public SimpleElementalAnalyzer(Graph analizedGraph) {
    this.analyzedGraph = analizedGraph;
  }

  @Override
  public boolean isConnected(Vertex start, Vertex end, Edge.Direction view) {
    for (Edge e : analyzedGraph.getAllEdgeAsSet()) {
      if (view.isRelated(new Edge(start, end), e)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public int countConnection(Vertex start, Vertex end, Edge.Direction view) {
    int counter = 0;
    Edge fixedEdge = new Edge(start, end);
    for (Edge variableEdge : analyzedGraph.getAllEdgeAsSet()) {
      counter = counter + Utilities.boolToNum(view.isRelated(fixedEdge, variableEdge));
    }
    return counter;
  }

  @Override
  public Set<Vertex> collectNeighborVertex(Vertex vertex) {
    Set<Vertex> vertices = new HashSet<Vertex>();
    for (Edge e : analyzedGraph.getAllEdgeAsSet()) {
      if (vertex == e.getStart()) {
        vertices.add(e.getEnd());
      }

      if (vertex == e.getEnd()) {
        vertices.add(e.getStart());
      }
    }

    return vertices;
  }

  @Override
  public int computeDegree(Vertex vertex) {
    int degree = 0;
    for (Edge e : analyzedGraph.getAllEdgeAsSet()) {
      degree = degree + Utilities.boolToNum(e.getStart() == vertex) + Utilities.boolToNum(e.getEnd() == vertex);
    }

    return degree;
  }
}
