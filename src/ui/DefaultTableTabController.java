package ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import table.SimpleStringTable;
import ui.DefaultRow;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class DefaultTableTabController implements Initializable {
  @FXML
  public Button add;

  @FXML
  public Button del;

  @FXML
  public ObservableList<DefaultRow> list;

  @FXML
  public TableView<DefaultRow> table;

  private SimpleStringTable modelTable;

  public DefaultRow defaultValue;

  public void initialize(URL url, ResourceBundle rb) {
    //デフォルト値の設定
    //tableのTableColumnのリストを受けて変化する。
    defaultValue = new DefaultRow();

    //リストの設定
    this.list = FXCollections.observableArrayList();
    table.setEditable(true);
    table.setItems(list);

    TableColumn<DefaultRow, String> col = new TableColumn<DefaultRow, String>();
    col.setText("編集");
    table.getColumns().add(col);

    col.setCellFactory(new Callback<TableColumn<DefaultRow, String>, TableCell<DefaultRow, String>>() {
      @Override
      public TableCell<DefaultRow, String> call(TableColumn<DefaultRow, String> e) {
        return new ButtonTableCell<DefaultRow, String>();
      }
    });

    //Columnの追加・削除時の動作、
    ListChangeListener<TableColumn<DefaultRow, ?>> listener = new ListChangeListener<TableColumn<DefaultRow, ?>>() {
      @Override
      public void onChanged(ListChangeListener.Change<? extends TableColumn<DefaultRow, ?>> e) {
        for (TableColumn<DefaultRow, ?> column : e.getList()) {
          String columnName = column.getText();
          defaultValue.getValueMap().put(columnName, new SimpleStringProperty(columnName));
        }
      }
    };

    table.getColumns().addListener(listener);

    //ボタンの設定
    EventHandler<ActionEvent> handler;
    handler = (ActionEvent e) -> {
      DefaultRow row = new DefaultRow(defaultValue);
      table.getItems().add(row);
    };
    add.setOnAction(handler);

    handler = (ActionEvent e) -> {
      int model = table.getSelectionModel().getSelectedIndex();
      list.remove(model);
    };
    del.setOnAction(handler);
  }

  public void addColumn(String columnName) {
    TableColumn<DefaultRow, String> addColumn = new TableColumn<DefaultRow, String>();
    addColumn.setEditable(true);
    addColumn.setText(columnName);
    addColumn.setId(columnName);
    addColumn.setCellFactory(TextFieldTableCell.forTableColumn());
    addColumn.setCellValueFactory(
        new Callback<CellDataFeatures<DefaultRow, String>, ObservableValue<String>>() {
          @Override
          public ObservableValue<String> call(CellDataFeatures<DefaultRow, String> p) {
            return p.getValue().getValueMap().get(columnName);
          }
        }
        );
    table.getColumns().add(addColumn);
  }

  public <T> SetChangeListener<T> getAddColumnListener() {
    SetChangeListener<T> listener = new SetChangeListener<T>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends T> change) {
        if(change.wasAdded()) {
          String columnName = change.getElementAdded().toString();
          addColumn(columnName);
          for(DefaultRow row : table.getItems()) {
            row.getValueMap().put(columnName, new SimpleStringProperty("new value"));
          }
        }

        if(change.wasRemoved()) {
          String columnName = change.getElementRemoved().toString();
          for(TableColumn<DefaultRow, ?> column : new ArrayList<>(table.getColumns())) {
            if(null != column && column.getId() == columnName) {
              table.getColumns().remove(column);
            }
          }

          for(DefaultRow row : table.getItems()) {
            row.getValueMap().remove(columnName);
          }

        }
      }
    };

    return listener;
  }

  public void setTableModel(SimpleStringTable model) {
    this.modelTable = model;

    SetChangeListener<String> listener = this.<String>getAddColumnListener();
    modelTable.addColumnListener(listener);

    modelTable.addRecordsListener(
        new ListChangeListener<Map<String, String>>() {
          @Override
          public void onChanged(ListChangeListener.Change<? extends Map<String, String>> change) {
            while (change.next()) {
              for (Map<String, String> record : change.getAddedSubList()) {
                //rowに入れるデータの組立て
                Map<String, StringProperty> rowData = new HashMap<>();
                for (String column : modelTable.getColumns()) {
                  StringProperty value = new SimpleStringProperty(record.get(column));
                  rowData.put(column, value);
                }

                //row自身の組立て
                DefaultRow row = new DefaultRow();
                row.setValueMap(FXCollections.observableMap(rowData));

                //追加操作
                table.getItems().add(row);
              }
            }
          }
        }
    );
  }

}
