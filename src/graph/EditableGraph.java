package graph;
import java.util.HashSet;
import java.util.Set;

import for_now.Utilities;


public class EditableGraph implements Graph, Editor , ElementalAnalyzer<Vertex>{
  protected  Set<Vertex> vertices;
  protected  Set<Edge> edges;

  public EditableGraph() {
    super();
    this.vertices = new HashSet<Vertex>();
    this.edges = new HashSet<Edge>();
  }

  public EditableGraph(Set<Vertex> vertices, Set<Edge> edges) {
    this();
    this.vertices.addAll(vertices);

    for(Edge edge : edges) {
      this.addEdge(edge);
    }
  }



  // 頂点に関する操作。追加、参照、削除の順番。
  @Override
  public void addVertex(Vertex vertex) {
    vertices.add(vertex);
  }

  @Override
  public Set<Vertex> getAllVertexAsSet() {
    Set<Vertex> vertices = new HashSet<Vertex>();
    vertices.addAll(this.vertices);

    return vertices;
  }

  @Override
  public void removeVertex(Vertex vertex) {
    for(Edge edge : edges){
      if( edge.getTerminalsAsSet().contains(vertex)){
        edges.remove(edge);
      }
    }

    vertices.remove(vertex);
  }


  // 辺に関する操作。追加、参照、削除の順番。
  @Override
  public void addEdge(Edge edge) {
    if(vertices.containsAll(edge.getTerminalsAsSet())) {
      edges.add(edge);
    }
  }

  public Set<Edge> getAllEdgeAsSet() {
    Set<Edge> edges = new HashSet<Edge>();
    edges.addAll(this.edges);

    return edges;
  }


  public Set<Edge> getEdges(Vertex start, Vertex end, Edge.Direction view) {
    Edge edge = new Edge(start, end);
    Set<Edge>  set = new HashSet<Edge>();
    for(Edge e : edges) {
      if(view.isRelated(edge, e)){
        set.add(e);
      }
    }
    return set;
  }

  @Override
  public void removeEdge(Edge edge) {
    edges.remove(edge);
  }


  //簡単な解析
  @Override
  public boolean isConnected(Vertex start, Vertex end, Edge.Direction view) {
    for(Edge e : edges) {
      if(view.isRelated(new Edge(start, end), e)){
        return true;
      }
    }
    return false;
  }

  @Override
  public int countConnection(Vertex start, Vertex end, Edge.Direction view){
    int counter = 0;
    for(Edge e : edges) {
      counter = counter + Utilities.boolToNum(view.isRelated(new Edge(start, end), e));
    }
    return counter;
  }

  @Override
  public Set<Vertex> collectNeighborVertex(Vertex vertex){
    Set<Vertex> vertices = new HashSet<Vertex>();
    for(Edge e : this.edges){
      if(vertex == e.getStart()){
        vertices.add(e.getEnd());
      }

      if(vertex == e.getEnd()){
        vertices.add(e.getStart());
      }
    }

    return vertices;
  }

  @Override
  public int computeDegree(Vertex vertex){
    int degree = 0;
    for(Edge e : this.edges){
      degree = degree + Utilities.boolToNum(e.getStart() == vertex) + Utilities.boolToNum(e.getEnd() == vertex);
    }

    return  degree;
  }

}
