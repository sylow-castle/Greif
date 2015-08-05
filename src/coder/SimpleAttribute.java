package coder;

public class SimpleAttribute implements DotAttribute {
  private String name;
  private String value;

  public SimpleAttribute(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @Override
  public String getKey() {
    return name;
  }
  @Override
  public String getValue() {
    return value;
  }
}
