package coder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class GraphCoder implements DotWriter {
  private Set<DotWriter> vertex;
  private Set<DotWriter> edge;

  public GraphCoder(Set<? extends DotWriter> vertex, Set<? extends DotWriter> edge) {
    if (null == vertex) {
      vertex = Collections.<VertexCoder> emptySet();
    }

    if (null == edge) {
      edge = Collections.<EdgeCoder> emptySet();
    }

    this.vertex = new HashSet<DotWriter>(vertex);
    this.edge = new HashSet<DotWriter>(edge);
  }

  public List<String> writeDot() {
    ArrayList<String> dotLang = new ArrayList<String>();
    //利用する日本語フォントの設定
    String FONTNAME = runner.Main.prop.getProperty("FONTNAME");

    //プレフィクス
    dotLang.add("digraph {");
    dotLang.add("node [fontname=\"" + FONTNAME + "\"];");
    dotLang.add("edge [fontname=\"" + FONTNAME + "\"];");

    //頂点のdot言語の出力
    for (DotWriter DWriter : vertex) {
      dotLang.addAll(DWriter.writeDot());
    }

    //辺を表現するdot言語の出力
    for (DotWriter DWriter : edge) {
      dotLang.addAll(DWriter.writeDot());
    }

    //サフィクス
    dotLang.add("}");

    return dotLang;
  }
}
