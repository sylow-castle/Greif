package graph;

import java.util.HashSet;
import java.util.Set;
import graph.Edge.Direction;

public class EditableGraph implements Graph, Editor, ElementalAnalyzer<Vertex> {
  protected final Set<Vertex> vertices;
  protected final Set<Edge> edges;
  protected final ElementalAnalyzer<Vertex> analyzer;

  public EditableGraph() {
    this(null, null, null);
  }

  EditableGraph(ElementalAnalyzer<Vertex> analyzer) {
    this(null, null, analyzer);
  }

  public EditableGraph(Set<Vertex> vertices, Set<Edge> edges) {
    this(vertices, edges, null);
  }

  public EditableGraph(Graph graph) {
    this(graph.getAllVertexAsSet(), graph.getAllEdgeAsSet(), null);
  }

  EditableGraph(Set<Vertex> vertices, Set<Edge> edges, ElementalAnalyzer<Vertex> analyzer) {
    super();
    //nullの引数は適当なものに置き換え
    if (null == vertices) {
      vertices = new HashSet<Vertex>();
    }

    if (null == edges) {
      edges = new HashSet<Edge>();
    }

    if (null == analyzer) {
      analyzer = new SimpleElementalAnalyzer(this);
    }

    //インスタンス変数の設定
    this.vertices = new HashSet<Vertex>(vertices);
    this.edges = new HashSet<Edge>();
    for (Edge edge : edges) {
      this.addEdge(edge);
    }

    this.removeEdge(null);
    this.removeVertex(null);
    this.analyzer = analyzer;
  }

  // 頂点に関する操作。追加、参照、削除の順番。
  @Override
  public void addVertex(Vertex vertex) {
    if (null != vertex) {
      vertices.add(vertex);
    }
  }

  @Override
  public Set<Vertex> getAllVertexAsSet() {
    Set<Vertex> vertices = new HashSet<Vertex>();
    vertices.addAll(this.vertices);

    return vertices;
  }

  @Override
  public void removeVertex(Vertex vertex) {
    //ConcurrentModificationException回避のためにgetAllEdgeAsSetメソッドを使ってる
    for (Edge edge : this.getAllEdgeAsSet()) {
      if (edge.getTerminalsAsSet().contains(vertex)) {
        this.edges.remove(edge);
      }
    }

    vertices.remove(vertex);
  }

  // 辺に関する操作。追加、参照、削除の順番。
  @Override
  public void addEdge(Edge edge) {
    if (null != edge && vertices.containsAll(edge.getTerminalsAsSet())) {
      this.edges.add(edge);
    }
  }

  public Set<Edge> getAllEdgeAsSet() {
    Set<Edge> edges = new HashSet<Edge>();
    edges.addAll(this.edges);

    return edges;
  }

  public Set<Edge> getEdges(Vertex start, Vertex end, Edge.Direction view) {
    Set<Edge> set = new HashSet<Edge>();
    Edge edge = new Edge(start, end);
    for (Edge e : this.edges) {
      if (view.isRelated(edge, e)) {
        set.add(e);
      }
    }
    return set;
  }

  @Override
  public void removeEdge(Edge edge) {
    this.edges.remove(edge);
  }

  //簡単な解析
  @Override
  public Set<Vertex> collectNeighborVertex(Vertex vertex) {
    return this.analyzer.collectNeighborVertex(vertex);
  }

  @Override
  public int computeDegree(Vertex vertex) {
    return this.analyzer.computeDegree(vertex);
  }

  @Override
  public boolean isConnected(Vertex start, Vertex end, Direction view) {
    return this.analyzer.isConnected(start, end, view);
  }

  @Override
  public int countConnection(Vertex start, Vertex end, Direction view) {
    return this.analyzer.countConnection(start, end, view);
  }

}
