package coder;

public class SimpleAttribute implements DotAttribute {
  private String key;
  private String value;

  public SimpleAttribute(String key, String value) {
    this.key = key;
    this.value = value;
  }

  @Override
  public String getKey() {
    return key;
  }
  @Override
  public String getValue() {
    return value;
  }
}
