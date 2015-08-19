package coder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class AttributeCoderTest {

  @Test
  public void Dot言語のAttributeリストが取得できる() {
    List<DotAttribute> list = new ArrayList<DotAttribute>();
    DotAttribute attr;
    attr = new DotAttribute() {
      @Override
      public String getKey() {
        return "test";
      }

      @Override
      public String getValue() {
        return "value";
      }
    };
    list.add(attr);

    AttributeCoder coder = new AttributeCoder(list);

    String actual = coder.writeDot().get(0);
    String expected = "[\"test\"=\"value\"]";

    assertThat(actual, is(expected));
  }

  @Test
  public void 構築子がnullの場合空文字だけのリストを返却する() {
    AttributeCoder coder = new AttributeCoder(null);

    String actual = coder.writeDot().get(0);
    String expected = "";
    assertThat(actual, is(expected));
  }

  @Test
  public void 返却されるリストはサイズが1である() {
    AttributeCoder coder = new AttributeCoder(null);
    assertThat(coder.writeDot().size(), is(1));
  }


}
