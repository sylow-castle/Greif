package coder;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class GraphCoderTest {

  @Test
  public void 構築子がnullの場合() {
    GraphCoder GCoder = new GraphCoder(null, null);
    List<String> expected = new ArrayList<String>();
    expected.add("digraph {");
    expected.add("node [fontname=\"null\"];");
    expected.add("edge [fontname=\"null\"];");
    expected.add("}");

    List<String> actual = GCoder.writeDot();

    assertThat(actual, is(expected));
  }

  @Test
  public void 構築子がnullでない場合() {
    String vState = "頂点たちのdot言語";
    DotWriter vertex = new DotWriter() {
      @Override
      public List<String> writeDot() {
        List<String> list = new ArrayList<String>();
        list.add(vState);

        return list;
      }
    };
    Set<DotWriter> vertices = new HashSet<DotWriter>();
    vertices.add(vertex);

    String eState = "辺たちのdot言語";
    DotWriter edge = new DotWriter() {
      @Override
      public List<String> writeDot() {
        List<String> list = new ArrayList<String>();
        list.add(eState);

        return list;
      }
    };
    Set<DotWriter> edges = new HashSet<DotWriter>();
    edges.add(edge);

    GraphCoder GCoder = new GraphCoder(vertices, edges);
    List<String> expected = new ArrayList<String>();
    expected.add("digraph {");
    expected.add("node [fontname=\"null\"];");
    expected.add("edge [fontname=\"null\"];");
    expected.add(vState);
    expected.add(eState);
    expected.add("}");

    List<String> actual = GCoder.writeDot();

    assertThat(actual, is(expected));
  }

}
