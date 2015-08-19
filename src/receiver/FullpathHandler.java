package receiver;

import java.util.ArrayDeque;
import java.util.Deque;

public class FullpathHandler {
  private String fullpath;
  private String delimiter;

  public FullpathHandler(String fullpath, String delimiter){
    this.fullpath = fullpath;
    this.delimiter = delimiter;
  }

  public FullpathHandler(String fullpath){
    this.fullpath = fullpath;
    this.delimiter = "\\\\";
  }

  public String[] getElements(){
    String regex = delimiter;
    return this.fullpath.split(regex);
  }

  public String getFullpath(){
    return fullpath;
  }

  public Deque<String> stackFullpath(){
    Deque<String> stackElements = new ArrayDeque<String>();
    String[] arrayElements = this.getElements();

    for(String element : arrayElements){
      stackElements.push(element);
    }
    return stackElements;
  }


  public String computeParentPath(){
    String parentPath;
    Deque<String> pathAsStack = this.stackFullpath();
    parentPath = "";
    pathAsStack.pop();

    if(!(pathAsStack.isEmpty())){
      parentPath = pathAsStack.pop();
    }

    while(!(pathAsStack.isEmpty())){
      parentPath = pathAsStack.pop()+ "\\" + parentPath ;
    }

    return parentPath;
  }

  public int getDepth(){
    return this.getElements().length;
  }

}
