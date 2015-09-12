package table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ui.DefaultRow;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.util.Builder;

public class SimpleStringTable implements Builder<SimpleStringTable> {
  private StringProperty  name;
  private ObservableSet<String> columns;
  private ObservableList<Map<String, String>> records;

  public SimpleStringTable() {
    this(null);
  }

  public SimpleStringTable(String name) {
    this.name = new SimpleStringProperty(name);
    this.columns = FXCollections.<String>observableSet();
    this.records = FXCollections.<Map<String, String>>observableArrayList();
  }

  public String getName() {
    return name.get();
  }

  public void setName(String value) {
    name.set(value);
  }

  public StringProperty NameProperty() {
    return name;
  }

  public void addColumnListener(SetChangeListener<? super String> listener) {
    columns.addListener(listener);
   }

  public void removeColumnListener(SetChangeListener<? super String> listener) {
    columns.removeListener(listener);
   }

  public void addRecordsListener(ListChangeListener<? super Map<String, String>> listener) {
    records.addListener(listener);
   }

  public void removeRecordsListener(ListChangeListener<? super Map<String, String>> listener) {
    records.removeListener(listener);
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

  public void removeRecord(int i) {
    records.remove(i);
  }

  public void removeAllRecords() {
    records = FXCollections.<Map<String, String>>observableArrayList();
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

  public Map<String, String> getRecord(int i) {
    return records.get(i);
  }


  @Override
  public SimpleStringTable build() {
    return this;
  }


}
