package coder;
import java.util.List;
import java.util.ArrayList;

import graph.Vertex;

public class VertexCoder implements DotWriter{
  private String id;
  private AttributeCoder attribute;


  public VertexCoder(Vertex vertex){
    this.id = vertex.toString();
  }

  public VertexCoder(String id){
    this.id = id;
  }

  public VertexCoder(String id, String label) {
    this.id = id;

    //label属性の設定
    DotAttribute labelAttribute = new DotAttribute() {
      @Override
      public String getKey() {
        return "label";
      }

      @Override
      public String getValue() {
        return "\"" + label + "\"";
      }
    };

    List<DotAttribute> a = new ArrayList<DotAttribute>();
    a.add(labelAttribute);
    this.attribute = new AttributeCoder(a);
  }

  public VertexCoder(String node_id, List<DotAttribute> attribute){
    this.id = node_id;
    this.attribute = new AttributeCoder(attribute);
  }

  public List<String> writeDot(){
    List<String> dotLang = new ArrayList<String>();

    String dotCode = id + " " + attribute.writeDot().get(0) + ";";
    dotLang.add(dotCode);

    return dotLang;
  }
}
