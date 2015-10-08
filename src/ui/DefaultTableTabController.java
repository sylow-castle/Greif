package ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import table.Column;
import table.SimpleStringTable;
import ui.DefaultRow;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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
  public TableView<DefaultRow> tableView;

  private ObjectProperty<SimpleStringTable> modelTable = new SimpleObjectProperty<>();
  private final DefaultRow defaultValue = new DefaultRow();
  private final Map<DefaultRow, Map<Column, String>> index = new HashMap<>();;

  public void initialize(URL url, ResourceBundle rb) {
    //リストの設定
    tableView.setEditable(true);
    tableView.setItems(FXCollections.observableArrayList());

    TableColumn<DefaultRow, String> col = new TableColumn<DefaultRow, String>();
    col.setText("編集");
    tableView.getColumns().add(col);

    col.setCellFactory(new Callback<TableColumn<DefaultRow, String>, TableCell<DefaultRow, String>>() {
      @Override
      public TableCell<DefaultRow, String> call(TableColumn<DefaultRow, String> e) {
        return new ButtonTableCell<DefaultRow, String>();
      }
    });
  }


  public void setTableModel(SimpleStringTable model) {
    this.modelTable.setValue(model);

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
      DefaultRow row = tableView.getSelectionModel().getSelectedItem();
      model.removeRecord(this.index.get(row));
    });

    model.addColumnListener(new SetChangeListener<Column>() {
      @Override
      public void onChanged(SetChangeListener.Change<? extends Column> change) {
        if (change.wasAdded()) {
          Column column = change.getElementAdded();
          String columnName = column.getName();

          //defaultValueの整備
          StringProperty columnNameProp = new SimpleStringProperty(columnName);
          columnNameProp.bind(column.nameProperty());
          defaultValue.getValueMap().put(columnName, columnNameProp);

          //tableViewに追加するTableColumnの設定
          TableColumn<DefaultRow, String> addColumn = new TableColumn<DefaultRow, String>();
          addColumn.setEditable(true);
          addColumn.textProperty().bind(column.nameProperty());
          addColumn.setId(columnName);
          addColumn.setCellFactory(TextFieldTableCell.<DefaultRow> forTableColumn());
          addColumn.setCellValueFactory(
              new Callback<CellDataFeatures<DefaultRow, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(CellDataFeatures<DefaultRow, String> p) {
                  return p.getValue().getValueMap().get(columnName);
                }
              });

          addColumn.setOnEditCommit(event -> {
            //modelTableの操作。
            //TODO modelTable側に設定したリスナーからの通知で値を変更するようにしたい
              index.get(event.getRowValue()).put(column, event.getNewValue());
              event.getRowValue().getValueMap().get(columnName).set(event.getNewValue());
            });
          tableView.getColumns().add(addColumn);

          //既存のレコードへの値の追加
          for (DefaultRow row : tableView.getItems()) {
            row.getValueMap().put(columnName, new SimpleStringProperty(columnName));
          }

        }

        if (change.wasRemoved()) {
          String columnName = change.getElementRemoved().getName();
          //編集ボタンのカラムのために(notNullの条件がついてる）
          tableView.getColumns().removeIf(column -> (null != column.getId() && column.getId().equals(columnName)));

          for (DefaultRow row : tableView.getItems()) {
            row.getValueMap().remove(columnName);
          }

          defaultValue.getValueMap().remove(columnName);
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
              tableView.getItems().add(row);

              index.put(row, record);
            }
          }

          if(change.wasRemoved()) {
            for(Map<Column, String> record : change.getRemoved()) {
              tableView.getItems().stream()
                .filter(row -> index.get(row).equals(record))
                .findFirst()
                .ifPresent(row -> tableView.getItems().remove(row));
            }
          }
        }
      }
    });
  }
}
