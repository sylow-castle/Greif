package table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class SimpleStringTable {
  private SortedSet<String> columns;
  private List<Map<String, String>> records;


  public SimpleStringTable() {
    columns = new TreeSet<String>();
    records = new ArrayList<Map<String, String>>();
  }

  public void addColumn(String column) {
    columns.add(column);
  }

  public List<String> getColumns() {
    return new ArrayList<String>(columns);
  }

  public void removeColumn(String column) {
    columns.remove(column);
  }

  public void addRecord(Map<String, String> record) {
    records.add(record);
  }

  public void removeRecord(Map<String, String> record) {
    records.remove(record);
  }


  public Map<String, String> getTemplateRecord() {
    Map<String, String> record = new HashMap<String, String>();
    for(String column : columns) {
      record.put(column, null);
    }

    return record;
  }

  public List<Map<String, String>> getAllRecords() {
    return new ArrayList<Map<String, String>>(records);
  }

}
