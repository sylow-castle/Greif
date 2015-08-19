package coder;

import java.util.List;
import java.util.ArrayList;

public class VertexCoder implements DotWriter {
  private final String id;
  private final AttributeCoder attribute;

  public VertexCoder(String id) {
    this(id, null);
  }

  public VertexCoder(String id, List<DotAttribute> attribute) {
    if (null == id) {
      id = "";
    }
    this.id = id;

    this.attribute = new AttributeCoder(attribute);
  }

  public List<String> writeDot() {
    List<String> dotLang = new ArrayList<String>();
    if (id.length() <= 0) {
      return dotLang;
    }

    //Dot言語のString設定。
    String dotCode;
    dotCode = id;
    if (attribute.writeDot().get(0).length() > 0) {
      dotCode += " " + attribute.writeDot().get(0);
    }
    dotCode += ";";

    dotLang.add(dotCode);
    return dotLang;
  }
}
