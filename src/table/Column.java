package table;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Column {
  private SimpleStringTable table;
  private StringProperty name;

  public Column(SimpleStringTable table) {
    this(table, null);
  }

  public Column(SimpleStringTable table, String name) {
    this.table = table;
    this.name = new SimpleStringProperty(name);
  }

  public void setName(String name) {
    this.name.set(name);
  }

  public String getName() {
    return this.name.get();
  }

  public StringProperty nameProperty() {
    return this.name;
  }
}
