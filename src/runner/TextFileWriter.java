package runner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class TextFileWriter {
  private String FullPath;

  public TextFileWriter(String FullPath){
    this.FullPath = FullPath;
  }
  public void writeFile(List<String> content){
      try{
        FileWriter out = new FileWriter(this.FullPath);
        BufferedWriter wr  = new BufferedWriter(out);

        //テキストファイルの内容をリストとして展開
        for (String Line : content){
          wr.write(Line);
          wr.newLine();
        }

        wr.close();
      }catch (FileNotFoundException e){
        System.out.println(e);
      }catch (IOException e){
        System.out.println(e);
      }

  }
}
