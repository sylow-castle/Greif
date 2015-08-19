package coder;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Before;
import org.junit.Test;

public class SimpleAttributeTest {
  String key;
  String value;
  SimpleAttribute test;

  @Before
  public void setup() {
    key = "キー";
    value = "バリュー";

    test = new SimpleAttribute(key, value);
  }

  @Test
  public void keyのゲッター() {
    String actual = "キー";
    String exact = test.getKey();

    assertThat(actual, is(exact));
  }

  @Test
  public void valueのゲッター() {
    String actual = "バリュー";
    String exact = test.getValue();

    assertThat(actual, is(exact));
  }

}
