package ui;

import java.util.Set;

public class DbWriteSchema {


  public void writeTable(String TableName) {

  }

  public void writeTables(Set<String> TableNames) {
    for(String TableName : TableNames) {
      writeTables(TableNames);
    }
  }


}
