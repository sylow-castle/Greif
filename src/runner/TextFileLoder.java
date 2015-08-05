package runner;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class TextFileLoder {
  private String FullPath;

  TextFileLoder(String FullPath){
    this.FullPath = FullPath;
  }

  //FullPathのファイルを文字列のリストに展開する
  public List<String> loadFile(){
    List<String> Content = new ArrayList<String>();

      try{
        FileReader in = new FileReader(this.FullPath);
        BufferedReader br  = new BufferedReader(in);

        //テキストファイルの内容をリストとして展開
        String Line = br.readLine();
        while(Line != null){
          Content.add(Line);
          Line = br.readLine();
        }
        in.close();
      }catch (FileNotFoundException e){
        System.out.println(e);
      }catch (IOException e){
        System.out.println(e);
      }
    return Content;
  }
}
