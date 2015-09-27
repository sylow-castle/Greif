package ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import table.Column;
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
  public Map<DefaultRow, Map<Column, String>> index;



  public void initialize(URL url, ResourceBundle rb) {
    //デフォルト値の設定
    //tableのTableColumnのリストを受けて変化する。
    defaultValue = new DefaultRow();
    index = new HashMap<>();

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

  }

  public void addColumn(Column column) {
    String columnName = column.getName();
    TableColumn<DefaultRow, String> addColumn = new TableColumn<DefaultRow, String>();
    addColumn.setEditable(true);
    addColumn.textProperty().bind(column.nameProperty());
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

  public void setTableModel(SimpleStringTable model) {
    this.modelTable = model;

    //ボタンの設定
    add.setOnAction((ActionEvent e) -> {
      Map<Column, String> map = new HashMap<>(model.getTemplateRecord());
      for (Column column : map.keySet()) {
        String value = defaultValue.getValueMap().get(column.getName()).get();
        map.put(column, value);
      }
      model.addRecord(map);
    });

    del.setOnAction((ActionEvent e) -> {
      DefaultRow row = table.getSelectionModel().getSelectedItem();
      model.removeRecord(this.index.get(row));
    });

    model.addColumnListener(new SetChangeListener<Column>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends Column> change) {
        if (change.wasAdded()) {
          String columnName = change.getElementAdded().getName();
          addColumn(change.getElementAdded());
          for (DefaultRow row : table.getItems()) {
            row.getValueMap().put(columnName, new SimpleStringProperty("new value"));
          }
        }

        if (change.wasRemoved()) {
          String columnName = change.getElementRemoved().getName();
          //編集ボタンのカラムのために(notNullの条件がついてる
          table.getColumns().removeIf(column -> (null != column.getId() && column.getId().equals(columnName)));

          for (DefaultRow row : table.getItems()) {
            row.getValueMap().remove(columnName);
          }
        }
      }
    });

    model.addRecordsListener(new ListChangeListener<Map<Column, String>>() {
      @Override
      public void onChanged(ListChangeListener.Change<? extends Map<Column, String>> change) {
        while (change.next()) {
          if (change.wasAdded()) {
            for (Map<Column, String> record : change.getAddedSubList()) {
              //追加rowの組立て
              //データの組立て
              Map<String, StringProperty> rowData = new HashMap<>();
              for (Column column : record.keySet()) {
                StringProperty value = new SimpleStringProperty(record.get(column));
                rowData.put(column.getName(), value);
              }

              DefaultRow row = new DefaultRow();
              row.setValueMap(FXCollections.observableMap(rowData));

              //追加操作
              table.getItems().add(row);

              //
              index.put(row, record);
            }
          }

          if(change.wasRemoved()) {
            for(Map<Column, String> record : change.getRemoved()) {
              table.getItems().stream()
                .filter(row -> index.get(row).equals(record))
                .findFirst()
                .ifPresent(row -> list.remove(row));
            }
          }
        }
      }
    });
  }
}
