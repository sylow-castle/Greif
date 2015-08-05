package ui;

import graph.Edge;
import graph.IdentifiedGraph;
import graph.Vertex;

import java.sql.SQLException;

import coder.DotAttribute;
import coder.EdgeCoder;
import coder.GraphCoder;
import coder.SimpleAttribute;
import coder.VertexCoder;
import coder.nodeAttribute.*;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.TabPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;

import runner.TextFileWriter;
import table.SimpleStringTable;
import database.DbFileLoader;

public class TableSchema {
  //フルパス
  private String filePath;

  //対応するタブ
  private TabPane schemaView;

  //頂点、辺等のテーブル名をキー
  //テーブルが持つ属性の名前一覧を値としたMap。
  private ObservableList<String> tables;
  private Map<String, ObservableList<String>> columnNameMap;
  private Map<String, String> columnToAttributeId;

  //定数
  private static final String DOT_WRITE_PATH = "D:\\temp\\dotlang.dot";
  private static final String LABEL = "label";
  private static final String COLOR = "color";
  private static final String SHAPE = "shape";
  private static final String STYLE = "style";
  private static final String END = "end";
  private static final String START = "start";
  private static final String ID = "id";

  //

  public TableSchema(String filePath) throws SQLException, IOException {
    try {
      this.filePath = filePath;
      this.schemaView = new TabPane();
      this.columnNameMap = new HashMap<String, ObservableList<String>>();
      this.columnToAttributeId = createDefaultAttributeMap();
      this.tables = FXCollections.observableArrayList();

      //テーブルの追加
      Connection dbFile = DbFileLoader.loadDbFile(filePath);
      Statement fileStatement = dbFile.createStatement();
      ResultSet tables = fileStatement.executeQuery("select name from sqlite_master where type = 'table'");

      //テーブル名（String）から対応するTableViewを得るためのMap及びテーブル名のList
      Map<String, TableView<DefaultRow>> tableMap = new HashMap<String, TableView<DefaultRow>>();
      List<String> tableNameList = new ArrayList<String>();

      while (tables.next()) {
        //シーングラフへのTab追加
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../ui/DefaultTableTab.fxml"));
        Tab tab = loader.<Tab>load();

        Map<String, String> tabName = getTabNameMap();


        //タブの設定
        String tableId = tables.getString("name");
        this.tables.add(tableId);
        tab.setText(tabName.get(tableId));
        tab.getContent().setId(tableId);
        schemaView.getTabs().add(tab);

        TableView<DefaultRow> view = (TableView<DefaultRow>) tab.getContent().lookup("#table");
        tableMap.put(tableId, view);
        tableNameList.add(tableId);

        DefaultTableTabController controller = loader.<DefaultTableTabController>getController();
        columnNameMap.put(tableId, FXCollections.<String>observableArrayList());
        columnNameMap.get(tableId).addListener(controller.<String>getAddColumnListener());
      }

      for(String tableName : tableNameList) {
        //モデルへのデータ設定
        ResultSet values = dbFile.createStatement().executeQuery("select * from " + tableName);

        //列の設定
        //あわせて列名のListの作成とColumnの追加
        int cardOfColumns = values.getMetaData().getColumnCount();
        for(int i = 0; i < cardOfColumns; i++) {
          String columnName = values.getMetaData().getColumnName(i+1);
          columnNameMap.get(tableName).add(columnName);
        }

        //行の設定
        while (values.next()){
          //追加するDataRow用のvalueMapの組み立て
          ObservableMap<String, StringProperty>  valueMap = FXCollections.<String, StringProperty>observableHashMap();
          for(String columnName : columnNameMap.get(tableName)) {
            valueMap.put(columnName, new SimpleStringProperty(values.getString(columnName)));
          }

          //追加するDataRowの構成
          DefaultRow row = new DefaultRow();
          row.setValueMap(valueMap);
          tableMap.get(tableName).getItems().add(row);
        }
      }
      dbFile.close();
    } catch (SQLException e) {
      this.schemaView = null;
      e.printStackTrace();
    } catch (IOException e) {
      this.schemaView = null;
      e.printStackTrace();
    }
  }

  private Map<String, String> getTabNameMap() {
    Map<String, String> tabNameMap = new HashMap<String, String>();
    tabNameMap.put("vertex", "頂点");
    tabNameMap.put("edge", "辺");

    return tabNameMap;
  }

  public TabPane getSchemaView() {
    return schemaView;
  }


  public List<DefaultRow> getVerticesAsList() {
    List<DefaultRow> list = new ArrayList<DefaultRow>();
    TableView<DefaultRow> table = (TableView<DefaultRow>) schemaView.lookup("#vertex").lookup("#table");
    for(DefaultRow row : table.getItems()) {
      list.add(row);
    }
    return list;
  }

  public List<DefaultRow> getEdgesAsList() {
    List<DefaultRow> list = new ArrayList<DefaultRow>();
    TableView<DefaultRow> table = (TableView<DefaultRow>) schemaView.lookup("#edge").lookup("#table");
    for(DefaultRow row : table.getItems()) {
      list.add(row);
    }
    return list;
  }

  private static Map<String, String> createDefaultAttributeMap() {
    Map<String, String> defaultAttributeMap = new HashMap<String, String>();
    defaultAttributeMap.put(ID, ID);
    defaultAttributeMap.put(LABEL, LABEL);
    defaultAttributeMap.put(START, START);
    defaultAttributeMap.put(END, END);
    defaultAttributeMap.put(STYLE, STYLE);
    defaultAttributeMap.put(SHAPE, SHAPE);
    defaultAttributeMap.put(COLOR, COLOR);

    return defaultAttributeMap;
  }

  public void export() {
    IdentifiedGraph<String> baseGraph;
    Map<String, DefaultRow> vertexInfo;
    Map<Edge, DefaultRow> edgeInfo;

    SimpleStringTable vertices = new SimpleStringTable();
    SimpleStringTable edges = new SimpleStringTable();

    baseGraph = new IdentifiedGraph<String>();
    vertexInfo = new HashMap<String, DefaultRow>();
    edgeInfo = new HashMap<Edge, DefaultRow>();

    //頂点の追加
    for (DefaultRow vertex : this.getVerticesAsList()) {
      String key = vertex.getValueMap().get(ID).get();
      baseGraph.addVertex(key);
      vertexInfo.put(key, vertex);
    }

    //辺の追加
    for (DefaultRow edge : this.getEdgesAsList()) {
      String start = edge.getValueMap().get(START).get();
      String end = edge.getValueMap().get(END).get();

      if (start != null && end != null) {
        Edge addedEdge = baseGraph.addEdge(start, end);
        edgeInfo.put(addedEdge, edge);
      }
    }

    //Dot言語の構成
    List<String> givenColumnName = new ArrayList<String>();
    givenColumnName.add(LABEL);
    givenColumnName.add(SHAPE);
    givenColumnName.add(COLOR);


    Set<VertexCoder> VCoder = new HashSet<VertexCoder>();
    //頂点に属性を付与
    for (String vertex : baseGraph.getAllVertexAsSet()) {
      List<DotAttribute> attrList = new ArrayList<DotAttribute>();
      Map<String, StringProperty> row = vertexInfo.get(vertex).getValueMap();
      for (String columnName : givenColumnName) {
        attrList.add(new SimpleAttribute(
            columnToAttributeId.get(columnName), row.get(columnName).get()));
      }

      VCoder.add(new VertexCoder(vertex, attrList));
    }

    //辺に属性を付与
    Set<EdgeCoder> ECoder = new HashSet<EdgeCoder>();
    for (Edge edge : baseGraph.getAllEdgeAsSet()) {
      String start = baseGraph.valueOf(edge.getStart());
      String end = baseGraph.valueOf(edge.getEnd());
      ECoder.add(new EdgeCoder(start, end));
    }

    GraphCoder GCoder = new GraphCoder(VCoder, ECoder);

    //生成したDot言語の書き出し
    new TextFileWriter(DOT_WRITE_PATH).writeFile(GCoder.writeDot());
  }

  public List<String> getColumns(String tableName) {
    List<String> list = new ArrayList<String>(columnNameMap.get("tableName"));
    return list;
  }



  public void save() {
    try {
      Connection dbFile = DbFileLoader.createMemoryDB();
      Statement stmt = dbFile.createStatement();
      stmt.addBatch("begin;");


      //テーブルの作成
      Map<String, SimpleStringTable> tables = new HashMap();

      for (String tableName : this.tables) {
        //翻訳元になるTableView
        // TODO ここで作るSimpleTableオブジェクトをTableViewの元にしたい
        TableView<DefaultRow> tableview = (TableView<DefaultRow>) schemaView.lookup("#" + tableName).lookup("#table");

        //テーブルの作成
        SimpleStringTable table = new SimpleStringTable();
        tables.put(tableName, table);

        //列の設定
        for (String columName : this.columnNameMap.get(table)) {
          table.addColumn(columName);
        }

        //行の設定
        for(DefaultRow row : tableview.getItems()) {
          Map<String, String> record = new HashMap<String, String>();
          for(String columnName : table.getColumns()) {
            record.put(columnName, row.getValueMap().get(columnName).get());
          }

          table.addRecord(record);
        }
      }

      //create文の作成
      for (String table : this.tables) {
        //create文作成
        String sql ="";
        sql = "create table " + table + " ";

        List<String> columns = new ArrayList<String>();
        for (String columName : tables.get(table).getColumns()) {
          columns.add(columName + " text");
        }
        sql = sql + "(" + for_now.Utilities.serealizeString(columns, ", ") + ");";
        stmt.addBatch(sql);
      }


      //insert文の作成
      for (String tableName : this.tables) {
        String sql ="";

        SimpleStringTable table = tables.get(tableName);
        //テーブルのinsert文
        for (Map<String, String> record : table.getAllRecords()) {
          List<String> columns = table.getColumns();
          sql = "insert into " + tableName + " ";
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
      stmt.executeUpdate("backup to " + this.filePath);
      dbFile.close();

    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}