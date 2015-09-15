package runner;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class TextFileWriter {
  private String FullPath;

  public TextFileWriter(String FullPath){
    this.FullPath = FullPath;
  }
  public void writeFile(List<String> content){
      try{
        OutputStream fos = new FileOutputStream(this.FullPath);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
        BufferedWriter wr  = new BufferedWriter(osw);

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
