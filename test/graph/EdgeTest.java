package graph;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

public class EdgeTest {
  Edge edge1;
  Edge edge2;
  Edge reverse;

  @Before
  public void setUp() {
    edge1 = new Edge();
    edge2 = new Edge(edge1.getStart(), edge1.getEnd());
    reverse = new Edge(edge1.getEnd(), edge1.getStart());
  }

  @Test
  public void 構築子のテスト() {
    assertThat(edge1.getStart(), is(edge2.getStart()));
    assertThat(edge1.getEnd(), is(edge2.getEnd()));
  }

  @Test
  public void 端点をSetとして取得した場合同一向きでは一致() {
    assertThat(edge1.getTerminalAsMap(), is(edge2.getTerminalAsMap()));
  }

  @Test
  public void 端点をSetとして取得した場合逆向きでも一致() {
    assertThat(edge1.getTerminalsAsSet(), is(reverse.getTerminalsAsSet()));
  }

  @Test
  public void 端点をMapとして取得した場合同一向きでは一致() {
    assertThat(edge1.getTerminalAsMap(), is(edge2.getTerminalAsMap()));
  }

  @Test
  public void 端点をMapとして取得した場合逆向きでは不一致() {
    assertThat(edge1.getTerminalAsMap(), is(not(reverse.getTerminalAsMap())));
  }

  @Test
  public void NonDirectedで同一向きの場合() {
    boolean expected = true;
    boolean actual = Edge.Direction.NonDirected.isRelated(edge1, edge2);
    assertThat(actual, is(expected));
  }

  @Test
  public void NonDirectedで逆向きの場合() {
    boolean expected = true;
    boolean actual = Edge.Direction.NonDirected.isRelated(edge1, reverse);
    assertThat(actual, is(expected));
  }

  @Test
  public void Directedで同一向きの場合() {
    boolean expected = true;
    boolean actual = Edge.Direction.Directed.isRelated(edge1, edge2);
    assertThat(actual, is(expected));
  }

  @Test
  public void Directedで逆向きの場合() {
    boolean expected = false;
    boolean actual = Edge.Direction.Directed.isRelated(edge1, reverse);
    assertThat(actual, is(expected));
  }

}
