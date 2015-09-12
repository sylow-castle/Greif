package graph;

import java.util.ArrayList;
import java.util.List;

public class listVertex{
  private String Name;
  private List<listVertex> Children;

  public listVertex(){
    Children = new ArrayList<listVertex>();
  }


  public listVertex(String Name){
    this.Name = Name;
    Children = new ArrayList<listVertex>();
  }

  public List<listVertex> getChildren(){
    List<listVertex> Children = new ArrayList<listVertex>();
    Children.addAll(this.Children);
    return Children;
  }

  public void addChild(listVertex Child){
    listVertex.connectVertex(this, Child);
  }

  public void setParent(listVertex Parent){
    listVertex.connectVertex(Parent, this);
  }

  public String getName(){
    return this.Name;
  }

  private static void connectVertex(listVertex Parent, listVertex Child){
    Parent.Children.add(Child);
  }
}

