package ui;


import java.net.URL;
import java.util.ResourceBundle;

import ui.DefaultRow;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableView;
import javafx.scene.control.MultipleSelectionModel;
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
  public ObservableList<DefaultRow> list;

  @FXML
  public TableView<DefaultRow> table;

  public DefaultRow defaultValue;

  public void initialize(URL url, ResourceBundle rb){
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

    col.setCellFactory(new Callback<TableColumn<DefaultRow, String>,TableCell<DefaultRow, String>>() {
      @Override
      public TableCell<DefaultRow, String> call(TableColumn<DefaultRow, String> e){
        ButtonTableCell<DefaultRow, String> cell = new ButtonTableCell<DefaultRow, String>();
        return new ButtonTableCell<DefaultRow, String>();
      }
    });

    //Columnの追加・削除時の動作、
    ListChangeListener<TableColumn<DefaultRow, ?>> listener
    = new ListChangeListener<TableColumn<DefaultRow, ?>>() {
      @Override
      public void onChanged(ListChangeListener.Change<? extends TableColumn<DefaultRow, ?>> e) {
        for(TableColumn<DefaultRow, ?> column : e.getList()) {
          String columnName = column.getText();
          defaultValue.getValueMap().put(columnName, new SimpleStringProperty(columnName));
        }
      }
    };

    table.getColumns().addListener(listener);

    //ボタンの設定
    EventHandler<ActionEvent> handler;
    handler = (ActionEvent e) -> {table.getItems().add(new DefaultRow(defaultValue));} ;
    add.setOnAction(handler);

    handler = (ActionEvent e ) -> {
      MultipleSelectionModel<DefaultRow>  model = table.getSelectionModel();
      list.removeAll(model.getSelectedItems());
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


  public <T> ListChangeListener<T> getAddColumnListener() {
  ListChangeListener<T> listener = new ListChangeListener<T>() {
    @Override
    public void onChanged(ListChangeListener.Change<? extends T> list) {
      while(list.next()) {
        if( list.wasAdded() && !(list.getAddedSubList().isEmpty()) ){
          for(T columnName : list.getAddedSubList()) {
            addColumn(columnName.toString());
          }
        }
      }
     // TODO 削除時の動作もつけたい
    }
  };

  return listener;
  }

}
