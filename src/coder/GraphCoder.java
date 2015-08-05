package coder;
import graph.Edge;
import graph.IdentifiedGraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GraphCoder implements DotWriter{
  private Set<DotWriter> vertex;
  private Set<DotWriter> edge;

  public GraphCoder(Set<VertexCoder> vertex, Set<EdgeCoder> edge){
    this.vertex = new HashSet<DotWriter>(vertex);
    this.edge = new HashSet<DotWriter>(edge);
  }

  public GraphCoder(IdentifiedGraph<String> graph, Map<String, List<DotAttribute>> attribute) {
    this.vertex = new HashSet<DotWriter>();
    this.edge = new HashSet<DotWriter>();

    for(String v : graph.getAllVertexAsSet()) {
      DotWriter VCoder = new VertexCoder(v, attribute.get(v));
      vertex.add(VCoder);
    }

    for(Edge e : graph.getAllEdgeAsSet()) {
      String start = graph.valueOf(e.getStart());
      String end = graph.valueOf(e.getEnd());
      DotWriter ECoder = new EdgeCoder(start, end);
      edge.add(ECoder);
    }
  }

  public List<String> writeDot(){
    ArrayList<String> dotLang = new ArrayList<String>();
    //利用する日本語フォントの設定
    String FONTNAME = "meiryo";

     //プレフィクス
     dotLang.add("digraph{");
     dotLang.add("node[fontname=\"" + FONTNAME + "\"];");
     dotLang.add("edge[fontname=\"" + FONTNAME + "\"];");

    //頂点のdot言語の出力
    for(DotWriter DWriter : vertex){
      dotLang.addAll(DWriter.writeDot());
    }

    //辺を表現するdot言語の出力
    for(DotWriter DWriter : edge){
      dotLang.addAll(DWriter.writeDot());
    }

    //サフィクス
    dotLang.add("}");


    return dotLang;
  }

  public void code(){
    for(String line : this.writeDot()){
      System.out.println(line);
    }
  }
}
