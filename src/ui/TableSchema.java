package ui;

import graph.Edge;
import graph.IdentifiedGraph;

import java.net.URL;
import java.sql.SQLException;

import coder.DotAttribute;
import coder.EdgeCoder;
import coder.GraphCoder;
import coder.SimpleAttribute;
import coder.VertexCoder;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.IOException;

import runner.TextFileWriter;
import table.Column;
import table.SimpleStringTable;
import database.DbFileLoader;

public class TableSchema {
  //フルパス
  private String filePath;
  private IdentifiedGraph<String> baseGraph;

  //対応するタブ
  private TabPane schemaView;

  //外部から見え、のはtableNames

  //テーブルが持つ属性の名前一覧を値としたMap。
  private final ObservableSet<StringProperty> tableNames;
  private final ObservableSet<SimpleStringTable> tables;

  private SimpleStringTable vertexTable;
  private SimpleStringTable edgeTable;


  //定数
  private static final String DOT_WRITE_PATH = runner.Main.prop.getProperty("DOTFilePath");

  public TableSchema(String filePath) throws SQLException, IOException {
    this.filePath = filePath;
    this.schemaView = new TabPane();

    this.tableNames = FXCollections.<StringProperty> observableSet();
    this.tables = FXCollections.<SimpleStringTable> observableSet();

    //テーブル変更時の動作
    tables.addListener((SetChangeListener.Change<? extends SimpleStringTable> change) -> {
      if (change.wasAdded()) {
        SimpleStringTable table = change.getElementAdded();
        tableNames.add(new SimpleStringProperty(table.getName()));
        buildTableView(table.getName(), table);
      }

      if (change.wasRemoved()) {
        String name = change.getElementRemoved().getName();
        List<Tab> removedTabs = schemaView.getTabs().stream()
            .filter(tab -> null != tab.getId())
            .filter(tab -> tab.getId().equals(name))
            .collect(Collectors.toList());
        schemaView.getTabs().removeAll(removedTabs);

        tableNames.removeIf((StringProperty nameProp) -> nameProp.get().equals(name));
      }
    });

    //見てくれ部分
    Node content = (new TableManager(this)).getView();
    Tab managerTab = new Tab("テーブル一覧");
    managerTab.setContent(content);
    this.schemaView.getTabs().add(managerTab);

    try {
      //テーブルの追加
      Connection dbFile = DbFileLoader.loadDbFile(filePath);
      Statement fileStatement = dbFile.createStatement();
      ResultSet tables = fileStatement.executeQuery("select name from sqlite_master where type = 'table'");

      while (tables.next()) {
        String tableName = tables.getString("name");
        this.addTable(tableName);
      }

      for (StringProperty tableNameProp : this.tableNames) {
        String tableName = tableNameProp.get();
        ResultSet values = dbFile.createStatement().executeQuery("select * from " + tableName);
        this.getTable(tableName)
            .ifPresent(table -> loadTableData(table, values));
      }

      //TODO 仮実装
      this.getTable("vertex")
          .ifPresent(table -> vertexTable = table);

      this.getTable("edge")
          .ifPresent(table -> edgeTable = table);

      dbFile.close();
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    }
  }

  public void addTable(String name) {
    tables.add(new SimpleStringTable(name));
  }

  public void removeTable(String name) {
    this.tables.stream()
        .filter(table -> table.getName().equals(name))
        .findFirst()
        .ifPresent(table -> this.tables.remove(table));
  }

  public void addTablesListener(SetChangeListener<? super StringProperty> listener) {
    tableNames.addListener(listener);
  }

  public void removeTablesListener(SetChangeListener<? super StringProperty> listener) {
    tableNames.removeListener(listener);
  }

  public Optional<SimpleStringTable> getTable(String tableName) {
    return this.tables.stream()
        .filter(table -> table.getName().equals(tableName))
        .findFirst();
  }

  private void loadTableData(SimpleStringTable table, ResultSet tableData) {
    try {
      //列の設定
      //あわせて列名のListの作成とColumnの追加
      int cardOfColumns = tableData.getMetaData().getColumnCount();
      for (int i = 0; i < cardOfColumns; i++) {
        String columnName = tableData.getMetaData().getColumnName(i + 1);
        table.addColumn(columnName);
      }

      //行の設定
      while (tableData.next()) {
        Map<Column, String> record = table.getTemplateRecord();

        //行の値の組立て
        for (Column column : record.keySet()) {
          record.put(column, tableData.getString(column.getName()));
        }

        table.addRecord(record);

      }
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    }
  }

  private TableView<?> buildTableView(String tableId, SimpleStringTable table) {
    //シーングラフへのTab追加
    try {
      URL url = ui.view.fxmlRoot.class.getResource("DefaultTableTab.fxml");
      FXMLLoader loader = new FXMLLoader(url);
      Tab tab = loader.<Tab> load();

      //タブの設定
      tab.setId(tableId);
      tab.textProperty().bind(table.NameProperty());
      tab.getContent().setId(tableId);
      schemaView.getTabs().add(tab);

      TableView<?> view = (TableView<?>) tab.getContent().lookup("#table");
      DefaultTableTabController controller = loader.<DefaultTableTabController> getController();

      //テーブルの元データを設定
      controller.setTableModel(table);

      return view;
    } catch (IOException e) {
      // TODO 自動生成された catch ブロック
      e.printStackTrace();
      return null;
    }
  }

  public TabPane getSchemaView() {
    return schemaView;
  }

  public List<Map<Column, String>> getVerticesAsList() {
    return this.vertexTable.getAllRecordsAsList();
  }

  public List<Map<Column, String>> getEdgesAsListAsList() {
    return this.edgeTable.getAllRecordsAsList();
  }


  public void export() {
    //グラフの設定
    IdentifiedGraph<String> baseGraph = new IdentifiedGraph<String>();

    //頂点の追加、属性の設定
    Map<String, Map<Column, String>> vertexValue = new HashMap<>();
    for(Map<Column, String> vertexRecord : this.getVerticesAsList()) {
      String id = vertexRecord.get(this.vertexTable.getColumn("id"));
      if(null == id) {
        continue;
      }
      baseGraph.addVertex(id);
      vertexValue.put(id, vertexRecord);
    }

    //辺の追加,属性の設定
    Map<Edge, Map<Column, String>> edgeValue = new HashMap<>();
    for(Map<Column, String> edgeRecord : this.getEdgesAsListAsList()) {
      String start, end;
      start = edgeRecord.get(this.edgeTable.getColumn("start"));
      end   = edgeRecord.get(this.edgeTable.getColumn("end"));
      if(null == edgeRecord.get(start) || null == edgeRecord.get(end)) {
        continue;
      }

      Edge edge = baseGraph.addEdge(start, end);
      edgeValue.put(edge, edgeRecord);
    }

    //Coderの設定
    Set<VertexCoder> VCoder = new HashSet<>();
    for (String id : baseGraph.getAllVertexAsSet()) {
      List<DotAttribute> attrList = new ArrayList<>();
      for (Map.Entry<Column, String> entry : vertexValue.get(id).entrySet()) {
        attrList.add(new SimpleAttribute(entry.getKey().getName(), entry.getValue()));
      }
      VCoder.add(new VertexCoder(id, attrList));
    }

    Set<EdgeCoder> ECoder = new HashSet<>();
    for (Edge edge : baseGraph.getAllEdgeAsSet()) {
      List<DotAttribute> attrList = new ArrayList<>();
      for (Map.Entry<Column, String> entry : edgeValue.get(edge).entrySet()) {
        attrList.add(new SimpleAttribute(entry.getKey().getName(), entry.getValue()));
      }

      String startId, endId;
      startId = baseGraph.valueOf(edge.getStart());
      endId = baseGraph.valueOf(edge.getEnd());
      ECoder.add(new EdgeCoder(startId, endId, true, attrList));
    }

    GraphCoder GCoder = new GraphCoder(VCoder, ECoder);
    //Dot言語の書き出し
    new TextFileWriter(DOT_WRITE_PATH).writeFile(GCoder.writeDot());
  }

  public void save() {
    this.save(this.filePath);
  }

  public void save(String filePath) {
    try {
      Connection dbFile = DbFileLoader.createMemoryDB();
      Statement stmt = dbFile.createStatement();
      stmt.addBatch("begin;");

      //create文の作成
      for (SimpleStringTable table : this.tables) {
        //create文作成
        String sql = "";
        sql = "create table " + table.getName() + " ";

        List<String> columns = new ArrayList<String>();
        for (String columName : table.getColumns()) {
          columns.add(columName + " text");
        }
        sql = sql + "(" + for_now.Utilities.serealizeString(columns, ", ") + ");";
        stmt.addBatch(sql);
      }

      //insert文の作成
      for (SimpleStringTable table : this.tables) {
        String sql = "";

        //テーブルのinsert文
        List<Map<String, String>> records = new ArrayList<>();
        for (Map<Column, String> record : table.getAllRecordsAsList()) {
          Map<String, String> row = new HashMap<>();
          for (Column column : record.keySet()) {
            row.put(column.getName(), record.get(column));
          }
          records.add(row);
        }

        for (Map<String, String> record : records) {
          List<String> columns = table.getColumns();
          sql = "insert into " + table.getName() + " ";
          sql = sql + "(" + for_now.Utilities.serealizeString(columns, ", ") + ") ";

          //代入用の値の文字列化
          List<String> values = new ArrayList<String>();
          for (String columnName : columns) {
            String brace = "'";
            if (null == record.get(columnName)) {
              brace = "";
            }
            values.add(brace + record.get(columnName) + brace);
          }

          sql = sql + "values (" + for_now.Utilities.serealizeString(values, ", ") + ");";
          stmt.addBatch(sql);
        }
      }

      stmt.addBatch("commit;");
      stmt.executeBatch();
      stmt.executeUpdate("backup to " + filePath);
      dbFile.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}