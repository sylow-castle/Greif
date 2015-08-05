package coder;

import java.util.List;
import java.util.ArrayList;

import graph.Edge;

public class EdgeCoder implements DotWriter{
  private String start;
  private String end;

  public EdgeCoder(Edge e){
    this.start = e.getStart().toString();
    this.end = e.getEnd().toString();
  }


  public EdgeCoder(String start, String end){
    this.start = start;
    this.end = end;
  }


  public List<String> writeDot(){
    List<String> dotLang = new ArrayList<String>();
    String dotCode = start + " -> " + end + ";";
    dotLang.add(dotCode);

    return dotLang;
  }

}
