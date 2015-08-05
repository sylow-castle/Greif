package coder.nodeAttribute;

import coder.DotAttribute;

public enum Style implements DotAttribute {
  solid,
  dashed,
  dotted,
  bold,
  rounded,
  diagonals,
  filled,
  striped,
  wedged;

  @Override
  public String getKey() {
    return "style";
  }

  @Override
  public String getValue() {
    return this.toString();
  }
}
