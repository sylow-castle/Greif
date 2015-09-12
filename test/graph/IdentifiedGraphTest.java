package graph;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.experimental.runners.Enclosed;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class IdentifiedGraphTest {

  public static class EditableGraphへの委譲 {

    //メソッド利用状況確認内部クラス
    //確認するメソッドをオーバーライドする。
    private static class TestGraph extends EditableGraph {
      public String string;
      String called = "呼び出されました";

      private TestGraph() {
        super();
      }

      @Override
      public void addEdge(Edge e) {
        string = "EditableGraph." + Thread.currentThread().getStackTrace()[1].getMethodName() + "が" + called;
      }

      @Override
      public void removeEdge(Edge e) {
        string = "EditableGraph." + Thread.currentThread().getStackTrace()[1].getMethodName() + "が" + called;
      }

      @Override
      public boolean isConnected(Vertex start, Vertex end, Edge.Direction view) {
        string = "EditableGraph." + Thread.currentThread().getStackTrace()[1].getMethodName() + "が" + called;
        return true;
      }

      @Override
      public int countConnection(Vertex start, Vertex end, Edge.Direction view) {
        string = "EditableGraph." + Thread.currentThread().getStackTrace()[1].getMethodName() + "が" + called;
        return 0;
      }

      @Override
      public Set<Vertex> collectNeighborVertex(Vertex origin) {
        string = "EditableGraph." + Thread.currentThread().getStackTrace()[1].getMethodName() + "が" + called;
        return new HashSet<Vertex>();
      }

      @Override
      public int computeDegree(Vertex origin) {
        string = "EditableGraph." + Thread.currentThread().getStackTrace()[1].getMethodName() + "が" + called;
        return 0;
      }

    }

    IdentifiedGraph<String> actual;
    TestGraph stab;
    @Before
    public void setUp() {
      stab = new TestGraph();
      actual = new IdentifiedGraph<String>(stab);
    }

    @Test
    public void addEdgeの利用() {
      actual.addEdge(new Edge());
      String expected = "EditableGraph.addEdgeが呼び出されました";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void removeEdgeの利用() {
      actual.removeEdge(new Edge());
      String expected = "EditableGraph.removeEdgeが呼び出されました";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void isConnectedの利用() {
      actual.isConnected("start", "end", Edge.Direction.Directed);
      String expected = "EditableGraph.isConnectedが呼び出されました";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void countConnectionの利用() {
      actual.countConnection("start", "end", Edge.Direction.Directed);
      String expected = "EditableGraph.countConnectionが呼び出されました";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void collectNeighborVertexの利用() {
      actual.collectNeighborVertex("start");
      String expected = "EditableGraph.collectNeighborVertexが呼び出されました";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void computeDegreeの利用() {
      actual.computeDegree("start");
      String expected = "EditableGraph.computeDegreeが呼び出されました";
      assertThat(stab.string, is(expected));
    }
  }

}
