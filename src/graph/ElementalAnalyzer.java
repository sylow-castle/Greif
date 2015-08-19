package graph;
import java.util.Set;

public interface ElementalAnalyzer<T> {
  public abstract Set<T> collectNeighborVertex(T vertex);
  public abstract int  computeDegree(T vertex);
  public boolean isConnected(T start, T end, Edge.Direction view);
  public int countConnection(T start, T end, Edge.Direction view);
}
