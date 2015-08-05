package graph;

public interface Editor {

  // 頂点に関する操作、追加、参照、削除
  public abstract void addVertex(Vertex vertex);

  public abstract void removeVertex(Vertex vertex);

  // 辺に関する操作、追加、参照、、削除
  public abstract void addEdge(Edge edge);

  public abstract void removeEdge(Edge edge);

}