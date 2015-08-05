package graph;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IdentifiedGraph<T>  implements ElementalAnalyzer<T> {
  private Map<Vertex, T> valueMap;
  private Map<T, Vertex> vertexMap;
  private EditableGraph baseGraph;

  public IdentifiedGraph(){
    this.valueMap = new HashMap<Vertex, T>();
    this.vertexMap = new HashMap<T, Vertex>();
    this.baseGraph = new EditableGraph();
  }

  public IdentifiedGraph(Set<T> vertices){
     this();
     this.addVertices(vertices);
  }

  public T valueOf(Vertex vertex){
    return valueMap.get(vertex);
  }

  public Vertex vertexOf(T Value){
    return vertexMap.get(Value);
  }

  public Map<T, Vertex> getVertexMap(){
    Map<T, Vertex> map = new HashMap<T, Vertex>(vertexMap);
    return map;
  }

  public Map<Vertex, T> getValueMap(){
    Map<Vertex, T> map = new HashMap<Vertex, T>(valueMap);
    return map;
  }


  // 頂点に関する操作。追加、参照、削除の順番。
  public void addVertex(T vertex){
    if(valueMap.containsValue(vertex)){
      return;
    }

    Vertex v = new Vertex();
    baseGraph.addVertex(v);
    valueMap.put(v, vertex);
    vertexMap.put(vertex, v);
  }

  public void addVertices(Set<T> vertices){
    for(T vertex : vertices){
      addVertex(vertex);
    }
  }

  public Set<T> getAllVertexAsSet(){
    Set<T> vertices = new HashSet<T>();
    vertices.addAll(valueMap.values());

    return vertices;
  }

  public void removeVertex(T vertex){
    if(valueMap.containsKey(vertex)){
      baseGraph.removeVertex(vertexMap.get(vertex));
      valueMap.remove(vertexMap.get(vertex));
      vertexMap.remove(vertex);
    }
  }

  //辺に関する操作
  public Edge addEdge(T start, T end){
    Edge edge = null;
    if(valueMap.containsValue(start) && valueMap.containsValue(end)) {
      edge = new Edge(vertexMap.get(start), vertexMap.get(end));
      baseGraph.addEdge(edge);
    }

    return edge;
  }

  public void addEdge(Edge edge){
    baseGraph.addEdge(edge);
  }


  public void addEdges(Set<Edge> edges){
    for(Edge e : edges){
      baseGraph.addEdge(e);
    }
  }
  //startからendへ結ばれる辺を返却します。
  //directionがnonDirectionの場合はendからstartへ結ばれる辺も戻り値に含まれます。
  public Set<Edge> getEdgesAsSet(T start, T end, Edge.Direction view){
    Edge edge = new Edge(vertexMap.get(start), vertexMap.get(end));
    Set<Edge> edges = new HashSet<Edge>();

    for(Edge e : baseGraph.getAllEdgeAsSet()){
      if(view.isRelated(edge, e)) {
        edges.add(e);
      }
    }

    return edges;
  }

  public Set<Edge> getAllEdgeAsSet(){
    return baseGraph.getAllEdgeAsSet();
  }

  public void removeEdge(Edge edge) {
    baseGraph.removeEdge(edge);
  }

  //startからendへ結ばれる辺を除きます。
  //directionがnonDirectionの場合はendからstartへ結ばれる辺も除かれます。
  public void removeEdge(T start, T end, Edge.Direction direction){
    removeEdges(getEdgesAsSet(start, end, direction));
  }

  public void removeEdges(Set<Edge> edges) {
    for(Edge e : edges){
      baseGraph.removeEdge(e);
    }
  }

  public boolean isConnected(T start, T end, Edge.Direction view) {
    return baseGraph.isConnected(new Edge(vertexMap.get(start), vertexMap.get(end)), view);
  }

  public int countConnection(T start, T end, Edge.Direction view){
    return baseGraph.countConnection(new Edge(vertexMap.get(start), vertexMap.get(end)), view);
  }

  @Override
  public Set<T> collectNeighborVertex(T vertex) {
    Set<T> vertices = new HashSet<T>();
    for(Vertex v : baseGraph.collectNeighborVertex(vertexMap.get(vertex))){
      vertices.add(valueMap.get(v));
    }
    return vertices;

  }

  @Override
  public int computeDegree(T vertex){
    return baseGraph.computeDegree(vertexMap.get(vertex));
  }
}


