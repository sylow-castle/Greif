package graph;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import graph.Edge.Direction;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class EditableGraphTest {
  public static class グラフ頂点や辺の登録及び削除 {
    EditableGraph actual;
    Vertex[] vertices;
    Edge e1;
    Edge e2;

    @Before
    public void setUp() throws Exception {
      actual = new EditableGraph();
      vertices = new Vertex[6];
      for (int i = 0; i < 6; i++) {
        vertices[i] = new Vertex();
      }

      e1 = new Edge(vertices[0], vertices[1]);
      e2 = new Edge(vertices[0], vertices[2]);
    }

    @Test
    public void addVertexの引数の頂点が追加される() {
      Set<Vertex> expected = new HashSet<Vertex>();
      assertThat(actual.getAllVertexAsSet(), is(expected));

      actual.addVertex(vertices[0]);
      expected.add(vertices[0]);
      assertThat(actual.getAllVertexAsSet(), is(expected));
    }

    @Test
    public void addVertexの引数がnullの場合登録されない() {
      Set<Vertex> expected = new HashSet<Vertex>();
      actual.addVertex(null);
      assertThat(actual.getAllVertexAsSet(), is(expected));
    }

    @Test
    public void 同じVertexインスタンスを二重に登録しない() {
      Set<Vertex> expected = new HashSet<Vertex>();
      expected.add(vertices[0]);

      actual.addVertex(vertices[0]);
      actual.addVertex(vertices[0]);

      assertThat(actual.getAllVertexAsSet(), is(expected));
    }

    @Test
    public void getAllvertexAsSetで得られるインスタンスはその都度違う() {
      actual.addVertex(vertices[0]);
      assertNotSame(actual.getAllVertexAsSet(), actual.getAllVertexAsSet());
    }

    @Test
    public void removeVertexで引数の頂点が削除される() {
      Set<Vertex> expected = new HashSet<Vertex>();
      expected.add(vertices[1]);

      actual.addVertex(vertices[0]);
      actual.addVertex(vertices[1]);
      actual.removeVertex(vertices[0]);

      assertNotSame(actual.getAllVertexAsSet(), is(expected));
    }

    @Test
    public void removeVertexの引数を端点にもつ辺も削除される() {
      Set<Edge> expected = new HashSet<Edge>();

      actual.addVertex(vertices[0]);
      actual.addVertex(vertices[1]);
      actual.addVertex(vertices[2]);

      actual.addEdge(e1);
      actual.addEdge(e2);

      expected.add(e1);
      expected.add(e2);
      assertThat(actual.getAllEdgeAsSet(), is(expected));

      expected.removeAll(expected);
      actual.removeVertex(vertices[0]);
      assertThat(actual.getAllEdgeAsSet(), is(expected));

    }

    @Test
    public void addEdgeの引数edgeのstartとendどちらかが登録されていない時は辺を登録しない() {
      Set<Edge> expected = new HashSet<Edge>();
      assertThat(actual.getAllEdgeAsSet(), is(expected));

      actual.addVertex(vertices[0]);
      actual.addEdge(e1);
      assertThat(actual.getAllEdgeAsSet(), is(expected));
    }

    @Test
    public void addEdgeの引数edgeのstartとendの両方の頂点が登録されてる時は辺が追加される() {
      Set<Edge> expected = new HashSet<Edge>();
      assertThat(actual.getAllEdgeAsSet(), is(expected));

      actual.addVertex(e1.getStart());
      actual.addVertex(e1.getEnd());
      actual.addEdge(e1);
      expected.add(e1);
      assertThat(actual.getAllEdgeAsSet(), is(expected));
    }

    @Test
    public void 同じEdgeインスタンスを二重に登録しない() {
      Set<Edge> expected = new HashSet<Edge>();
      actual.addVertex(e1.getStart());
      actual.addVertex(e1.getEnd());

      actual.addEdge(e1);
      actual.addEdge(e1);
      expected.add(e1);
      assertThat(actual.getAllEdgeAsSet(), is(expected));
    }

    @Test
    public void getAllEdgeAsSetで得られるインスタンスはその都度違う() {
      assertNotSame(actual.getAllEdgeAsSet(), actual.getAllEdgeAsSet());
    }

    @Test
    public void removeEdgeで引数の辺が削除される() {
      actual.addVertex(e1.getStart());
      actual.addVertex(e1.getEnd());
      actual.addVertex(e2.getStart());
      actual.addVertex(e2.getEnd());

      actual.addEdge(e1);
      actual.addEdge(e2);
      actual.removeEdge(e1);

      Set<Edge> expected = new HashSet<Edge>();
      expected.add(e2);

      assertNotSame(actual.getAllEdgeAsSet(), is(expected));
    }

    @Test
    public void 二重辺をもつ場合getEdgesで両方が取得される() {
      actual.addVertex(e1.getStart());
      actual.addVertex(e1.getEnd());
      actual.addVertex(e2.getStart());
      actual.addVertex(e2.getEnd());

      actual.addEdge(e1);

      Edge e3 = new Edge(vertices[0], vertices[1]);
      actual.addEdge(e3);

      Set<Edge> expected = new HashSet<Edge>();
      expected.add(e1);
      expected.add(e3);

      assertThat(actual.getEdges(vertices[0], vertices[1], Edge.Direction.Directed), is(expected));
    }

    @Test
    public void 第三引数がDirectedの場合startとend両方が一致したものだけ取得() {
      actual.addVertex(vertices[0]);
      actual.addVertex(vertices[1]);

      Edge e3 = new Edge(vertices[1], vertices[0]);
      actual.addEdge(e1);
      actual.addEdge(e3);

      Set<Edge> expected = new HashSet<Edge>();
      expected.add(e1);

      assertThat(actual.getEdges(vertices[0], vertices[1], Edge.Direction.Directed), is(expected));
    }

    @Test
    public void 第三引数がNonDirectedの場合startとendが一致していなくても取得() {
      actual.addVertex(vertices[0]);
      actual.addVertex(vertices[1]);

      Edge e3 = new Edge(vertices[1], vertices[0]);
      actual.addEdge(e1);
      actual.addEdge(e3);

      Set<Edge> expected = new HashSet<Edge>();
      expected.add(e1);
      expected.add(e3);

      assertThat(actual.getEdges(vertices[0], vertices[1], Edge.Direction.NonDirected), is(expected));
    }

  }

  //委譲の確認
  public static class SimpleElementalAnalyzerへの利用を確認する {
    //テストスタブクラスの設定
    public static class StabElementalAnalyzer implements ElementalAnalyzer<Vertex> {
      public String string;

      @Override
      public Set<Vertex> collectNeighborVertex(Vertex vertex) {
        string = Thread.currentThread().getStackTrace()[1].getMethodName();
        return null;
      }

      @Override
      public int computeDegree(Vertex vertex) {
        string = Thread.currentThread().getStackTrace()[1].getMethodName();
        return 0;
      }

      @Override
      public boolean isConnected(Vertex start, Vertex end, Direction view) {
        string = Thread.currentThread().getStackTrace()[1].getMethodName();
        return false;
      }

      @Override
      public int countConnection(Vertex start, Vertex end, Direction view) {
        string = Thread.currentThread().getStackTrace()[1].getMethodName();
        return 0;
      }
    }

    public EditableGraph acutual;
    public StabElementalAnalyzer stab;

    @Before
    public void setUp() {
      stab = new StabElementalAnalyzer();
      this.acutual = new EditableGraph(stab);
    }

    @Test
    public void computeDegereeのstab利用確認() {
      acutual.computeDegree(new Vertex());
      String expected = "computeDegree";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void collectNeighborVertexのstab利用確認() {
      acutual.collectNeighborVertex(new Vertex());
      String expected = "collectNeighborVertex";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void countConnectionのstab利用確認() {
      acutual.countConnection(new Vertex(), new Vertex(), Edge.Direction.Directed);
      String expected = "countConnection";
      assertThat(stab.string, is(expected));
    }

    @Test
    public void isConnectedのstab利用確認() {
      acutual.isConnected(new Vertex(), new Vertex(), Edge.Direction.Directed);
      String expected = "isConnected";
      assertThat(stab.string, is(expected));
    }

  }

}
