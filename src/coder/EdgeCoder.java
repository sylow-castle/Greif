package coder;

import java.util.List;
import java.util.ArrayList;

public class EdgeCoder implements DotWriter{
  private String start;
  private String end;
  private boolean directed;


  public EdgeCoder(String start, String end, boolean directed) {
    start = start.replaceAll("\"","'\"");
    end = end.replaceAll("\"","'\"");

    this.start = "\"" + start + "\"";
    this.end = "\"" + end + "\"";
    this.directed = directed;
  }


  public List<String> writeDot(){
    List<String> dotLang = new ArrayList<String>();

    String connecter;
    if(directed) {
      connecter = "->";
    } else {
      connecter = "--";
    }
    String dotCode = start + " " + connecter + " " +  end + ";";
    dotLang.add(dotCode);

    return dotLang;
  }

}
