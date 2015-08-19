package coder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class VertexCoderTest {
  List<String> expect = new ArrayList<String>();
  List<String> actual;

  @Test
  public void ノードが属性を持たない場合() {
    String id = "test";
    VertexCoder coder = new VertexCoder(id);

    expect.add(id + ";");
    actual = coder.writeDot();

    assertThat(actual, is(expect));
  }

  @Test
  public void ノードが属性を持つ場合() {
    String id = "test";
    String key = "attribute";
    String value = "属性";

    SimpleAttribute attribute = new SimpleAttribute(key, value);
    List<DotAttribute> list = new ArrayList<DotAttribute>();
    list.add(attribute);

    VertexCoder coder = new VertexCoder(id, list);

    expect.add(id + " [\"attribute\"=\"属性\"];");
    actual = coder.writeDot();

    assertThat(actual, is(expect));
  }


  @Test
  public void idがnullの場合() {
    String id = null;
    VertexCoder coder = new VertexCoder(id);

    actual = coder.writeDot();

    assertThat(actual, is(expect));
  }

}
