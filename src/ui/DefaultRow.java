package ui;


import java.util.*;
import javafx.beans.property.MapProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableMap;
import javafx.collections.FXCollections;

public class DefaultRow {
  private MapProperty<String, StringProperty> valueMap;

  public DefaultRow(){
    this.valueMap = new SimpleMapProperty<String, StringProperty>(FXCollections.observableHashMap());
  }

  public DefaultRow(DefaultRow prototype) {
    this();
    ObservableMap<String, StringProperty> protoColumns = prototype.getValueMap();

    for(String key  : protoColumns.keySet()) {
      StringProperty value = new SimpleStringProperty(protoColumns.get(key).get());
      this.valueMap.put(key, value);
    }
  }

  public ObservableMap<String, StringProperty> getValueMap() {
    return valueMap.get();
  }

  public void setValueMap(ObservableMap<String, StringProperty> columns){
    this.valueMap.set(columns);
  }

  public Map<String, String> degenerateRow() {
    Map<String, String> record = new HashMap<String, String>();
    for(String column : this.valueMap.get().keySet() ) {
      record.put(column, this.valueMap.get(column).get());
    }

    return record;
  }

}