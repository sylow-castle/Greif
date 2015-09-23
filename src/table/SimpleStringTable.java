package table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.util.Builder;

public class SimpleStringTable implements Builder<SimpleStringTable> {
  private StringProperty name;
  private ObservableSet<Column> columns;
  private ObservableList<Map<Column, String>> records;

  public SimpleStringTable() {
    this(null);
  }

  public SimpleStringTable(String name) {
    this.name = new SimpleStringProperty(name);
    this.columns = FXCollections.<Column> observableSet();
    this.records = FXCollections.<Map<Column, String>> observableArrayList();

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

  public void addColumnListener(SetChangeListener<? super Column> listener) {
    columns.addListener(listener);
  }

  public void removeColumnListener(SetChangeListener<? super Column> listener) {
    columns.removeListener(listener);
  }

  public void addRecordsListener(ListChangeListener<? super Map<Column, String>> listener) {
    records.addListener(listener);
  }

  public void removeRecordsListener(ListChangeListener<? super Map<Column, String>> listener) {
    records.removeListener(listener);
  }

  public void addColumn(String column) {
    columns.add(new Column(this, column));
  }

  public List<String> getColumns() {
    List<String> result;
    result = this.columns.stream()
        .map(column -> column.getName())
        .collect(Collectors.toList());

    return result;
  }

  public Column getColumn(String columnName) {
    Column result = null;
    for (Column column : this.columns) {
      if (column.getName().equals(columnName)) {
        result = column;
        break;
      }
    }
    return result;
  }

  public void removeColumn(String columnName) {
<<<<<<< HEAD
    this.columns.removeAll(columns.stream()
        .filter(column -> column.getName().equals(columnName))
        .collect(Collectors.toSet())
        );
=======
    this.columns.removeIf(column -> column.getName().equals(columnName));
>>>>>>> ・カラム削除時にテーブルから削除されないバグの修正。
  }

  public void addRecord(Map<Column, String> record) {
    records.add(record);
  }

  public void removeRecord(Map<Column, String> record) {
    records.remove(record);
  }

  public void removeRecord(int i) {
    records.remove(i);
  }

  public void removeAllRecords() {
    records.removeAll(records);
  }

  public Map<Column, String> getTemplateRecord() {
    Map<Column, String> record = new HashMap<Column, String>();
    for (Column column : columns) {
      record.put(column, null);
    }

    return record;
  }

  public List<Map<Column, String>> getAllRecordsAsList() {
    return new ArrayList<Map<Column, String>>(records);
  }

  public Map<Column, String> getRecord(int i) {
    return records.get(i);
  }

  @Override
  public SimpleStringTable build() {
    return this;
  }

}
