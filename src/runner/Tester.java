package runner;
import graph.EditableGraph;
import graph.Editor;
import graph.listVertex;

import java.util.List;
import java.util.ArrayList;
import java.util.TreeSet;

import receiver.FullpathHandler;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class Tester {
  public static List<FullpathHandler> document;

  public static void test_loadTextFile(){
    //読み込むファイルのフルパスを設定
    String FilePath = "D:/temp/tmp_UTF8.txt";
    TextFileLoder content = (new TextFileLoder(FilePath));

    //テキストファイルの読み込み
    document = new ArrayList<FullpathHandler>();
    for(String line : content.loadFile()){
      //System.out.println(line)
      FullpathHandler SD = new FullpathHandler(line);
      document.add(SD);
    }

  }

  public static void test_CreateGraph(){
    /*
    読み込んだファイルの利用
    幅優先探索
    各深さ毎にHashSetにまとめる
    */

    /*
     * Depth関数の像集合を設定。
     * （※Depthは整数のため、順序つきとする）
     * その後、各像に対する逆像を設定。
     */
    TreeSet<Integer> imageOfDepth = new TreeSet<Integer>();
    for(FullpathHandler SD : document){
      imageOfDepth.add(Integer.valueOf(SD.getDepth()));
    }

    HashMap<Integer, HashSet<FullpathHandler>> equalizers = new HashMap<Integer, HashSet<FullpathHandler>>();
    for(Integer imValue : imageOfDepth){
      equalizers.put(imValue, new HashSet<FullpathHandler>());
    }

    //各ファイバーの要素の設定
    for(FullpathHandler SD : document){
      int depth = SD.getDepth();
      equalizers.get(Integer.valueOf(depth)).add(SD);
    }

    /*
     * ノードの作成
     */
    Map<FullpathHandler, listVertex> tree = new HashMap<FullpathHandler, listVertex>();
    for(FullpathHandler SD : document){
      String nodeName = SD.stackFullpath().pop();
      tree.put(SD, new listVertex(nodeName));
    }

    for(FullpathHandler SD : document){
      //親ノードのパスを求める
      String parent = "";
      parent = SD.computeParentPath();

      if(tree.containsKey(parent)){
        listVertex parentNode = tree.get(parent);
        listVertex childNode = tree.get(SD.getFullpath());
        parentNode.addChild(childNode);
      }
    }

    /*
     * テスト出力用コード
     */
     for(FullpathHandler SD : document){
       listVertex node = tree.get(SD.getFullpath());
       if(node.getChildren().size() > 0){
         System.out.println(node.getName() + "の子ノードは：");
         for(listVertex child : node.getChildren()){
           System.out.println(child.getName());
         }
       }
     }

     /*
      *
      */
     ArrayList<String> dotCode = new ArrayList<String>();
     dotCode.add("digraph{");
     dotCode.add("node[fontname=\"meiryo\"];");
     dotCode.add("edge[fontname=\"meiryo\"];");

     for(FullpathHandler SD : document){
       listVertex node = tree.get(SD.getFullpath());
       if(node.getChildren().size() > 0){
         for(listVertex child : node.getChildren()){
           dotCode.add(node.getName() + " -> " + child.getName() + ";");
         }
       }
     }

     dotCode.add("}");
     for(String line : dotCode){
       System.out.println(line);
     }
  }

  public static void test_CreateGraph_2(){
    /*
     * グラフの作成
     */
     Editor G = new EditableGraph();

    /*
     * ノードの作成
     */
    HashMap<String, listVertex> tree = new HashMap<String, listVertex>();

    for(FullpathHandler SD : document){
      String[] elements = SD.getElements();
      String nodeName = elements[elements.length - 1];
      listVertex vertex = new listVertex(nodeName);
      tree.put(SD.getFullpath(), vertex);
//      G.addVertex(vertex);
    }

    /*
     * エッジの作成を行う。
     * まず、親のパスを求めた上で繋ぐ。
     */
    for(FullpathHandler SD : document){
      //親ノードのパスを求める
      String parent = "";
      parent = SD.computeParentPath();

      /*
       * 親ノードがある場合エッジを追加する。
       */
      if(tree.containsKey(parent)){
        listVertex parentNode = tree.get(parent);
        listVertex childNode = tree.get(SD.getFullpath());
//        Edge edge = new Edge(parentNode, childNode);
       // G.addEdge(edge);
      }
    }

//    GraphCoder GCoder = new GraphCoder(G);
//    GCoder.code();
  }


}
