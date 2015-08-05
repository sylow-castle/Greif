package coder.nodeAttribute;

import coder.DotAttribute;

public class Label implements DotAttribute{
  String label = "";

  public Label(String label) {
    this.label = "\"" + label + "\"";
  }

  @Override
  public String getKey() {
    return "label";
  }

  @Override
  public String getValue() {
    return label;
  }
}