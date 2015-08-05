package coder.nodeAttribute;

import coder.DotAttribute;

public enum Shape implements DotAttribute {
  box,
  polygon,
  ellipse,
  oval,
  circle,
  point,
  egg,
  triangle,
  plaintext,
  plain,
  diamond,
  trapezium,
  paralellogram,
  house,
  pentagon,
  hexagon,
  septagon,
  octagon,
  doublecircle,
  doubleoctagon,
  tripleoctagon,
  invtriangle,
  invtrapezium,
  invhouse,
  Mdiamond,
  Msquare,
  Mcircle,
  rect,
  rectangle,
  square,
  star,
  none,
  underine,
  note,
  tab,
  folder,
  box3d,
  component,
  promoter,
  cds,
  terminator,
  utr,
  primersite,
  restrictionsite,
  fivepoverhang,
  threepoverhang,
  noverhang,
  assembly,
  signature,
  insulator,
  ribosite,
  rnastab,
  proteasesite,
  porteinstab,
  rpromoter,
  rarrow,
  larrow,
  lpromoter;

  @Override
  public String getKey() {
    return "shape";
  }

  @Override
  public String getValue() {
    return this.toString();
  }

}
