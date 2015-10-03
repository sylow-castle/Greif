package coder;

import java.util.List;
import java.util.ArrayList;

public class EdgeCoder implements DotWriter{
  private String start;
  private String end;
  private boolean directed;
  private final AttributeCoder attribute;

  public EdgeCoder(String start, String end, boolean directed) {
    this(start, end, directed, null);
  }

  public EdgeCoder(String start, String end, boolean directed, List<DotAttribute> attribute) {
    start = start.replaceAll("\"","'\"");
    end = end.replaceAll("\"","'\"");

    this.start = "\"" + start + "\"";
    this.end = "\"" + end + "\"";
    this.directed = directed;
    this.attribute = new AttributeCoder(attribute);
  }


  public List<String> writeDot(){
    List<String> dotLang = new ArrayList<String>();

    String connecter;
    if(directed) {
      connecter = "->";
    } else {
      connecter = "--";
    }




    String dotCode = start + " " + connecter + " " +  end;
    if (attribute.writeDot().get(0).length() > 0) {
      dotCode += " " + attribute.writeDot().get(0);
    }
    dotCode += ";";


    dotLang.add(dotCode);

    return dotLang;
  }

}
