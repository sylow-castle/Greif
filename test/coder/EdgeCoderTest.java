package coder;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

@RunWith(Enclosed.class)
public class EdgeCoderTest {
  public static class 正常系 {
    String start;
    String end;
    boolean directed;

    @Before
    public void setup() {
      start = "start Vertex";
      end = "end Vertex";
    }

    @Test
    public void コンストラクタの第三引数がtrueの時() {
      directed = true;

      EdgeCoder coder = new EdgeCoder(start, end, directed);

      String actual = coder.writeDot().get(0);
      String expected = "\"start Vertex\" -> \"end Vertex\";";

      assertThat(actual, is(expected));
    }

    @Test
    public void コンストラクタの第三引数がfalseの時() {
      directed = false;

      EdgeCoder coder = new EdgeCoder(start, end, directed);

      String actual = coder.writeDot().get(0);
      String expected = "\"start Vertex\" -- \"end Vertex\";";

      assertThat(actual, is(expected));
    }
  }

  public static class エスケープ処理 {
    String start;
    String end;
    @Before
    public void setup() {
      start = "start \"Vertex";
      end = "end\"Vertex\"";
    }

    @Test
    public void ダブルクォートのエスケープ処理() {
      EdgeCoder coder = new EdgeCoder(start, end, true);
      String actual = coder.writeDot().get(0);
      String expected = "\"start '\"Vertex\" -> \"end'\"Vertex'\"\";";

      assertThat(actual, is(expected));
    }
  }
}
