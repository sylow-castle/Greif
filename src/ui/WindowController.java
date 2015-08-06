package ui;

import coder.DotAttribute;
import coder.AttributeCoder;
import coder.EdgeCoder;
import coder.GraphCoder;
import coder.SimpleAttribute;
import coder.VertexCoder;
import coder.nodeAttribute.Shape;
import graph.Edge;
import graph.IdentifiedGraph;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import runner.Main;
import runner.TextFileWriter;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

public class WindowController implements Initializable {
  private static final String INITIAL_DIR = "D:/temp/";
  private static final String PNG_FILE_PATH = "D:\\temp\\sample.png";
  private static final String DOT_FILE_PATH = "D:\\temp\\dotlang.dot";
  private static final String GRAPHVIXZ_BIN_PATH = "D:\\Program\\Graphviz2.38\\bin\\dot.exe";

  @FXML
  public VBox root;

  @FXML
  public Button Generate;

  @FXML
  public Button SaveFile;

  @FXML
  public Button OpenFile;

  @FXML
  public ComboBox<TableSchema> SchemaList;

  //画像の表示部分
  @FXML
  public ImageView imView;
  public ImageView view;


  @FXML
  public TabPane tabsroot;

  @FXML
  public ComboBox<Shape> ShapeList;


  public void initialize(URL url, ResourceBundle rb) {
    EventHandler<ActionEvent> handler;
    //メニュータブの設定
    handler = (ActionEvent e ) -> {generateGraph(SchemaList.getValue());} ;
    Generate.setOnAction(handler);

    SchemaList.setItems(FXCollections.<TableSchema>observableArrayList());

    //メニュータブの設定
    handler = (ActionEvent e ) -> {
      FileChooser chooser = new FileChooser();
      chooser.setTitle("ファイルを選択");
      File dir = new File(INITIAL_DIR);
      chooser.setInitialDirectory(dir);
      File choosedFile = chooser.showOpenDialog(runner.Main.window);


      if(choosedFile != null) {
        //スキーマの追加操作の実行
        this.addSchema(choosedFile.getAbsolutePath());
      }
    };
    OpenFile.setOnAction(handler);

    handler = (ActionEvent e) -> {
      TableSchema target = SchemaList.getSelectionModel().getSelectedItem();
      target.save();
    };
    SaveFile.setOnAction(handler);

    Shape[] shapes = Shape.values();
    ObservableList<Shape> list = FXCollections.observableArrayList();
    for(Shape e : shapes){
      list.add(e);
    }
     ShapeList.setItems(list);
  }


  private void addSchema(String path) {
    //スキーマの追加操作の実行
    TableSchema choosedSchema;
    try {
      choosedSchema = new TableSchema(path);

      //メインウィンドウへの追加
      Tab schemaTab = new Tab(path);
      tabsroot.getTabs().add(schemaTab);
      schemaTab.setContent(choosedSchema.getSchemaView());

      //選択用コンボボックスへの追加
      SchemaList.getItems().add(choosedSchema);

    } catch (SQLException e1) {
      e1.printStackTrace();
    } catch (IOException e1) {
      e1.printStackTrace();
    }

  }

  public void generateGraph(TableSchema schema){
    schema.export();
    execGraphViz();

    FileSystem fs = FileSystems.getDefault();
    Path path = fs.getPath("D:/temp/sample.png");
    Image im = new Image(path.toFile().toURI().toString());
    this.imView.setImage(im);
  }

  private void execGraphViz(){
    String GraphVizPath = GRAPHVIXZ_BIN_PATH;
    String dotFilePath = DOT_FILE_PATH;
    String outFilePath = PNG_FILE_PATH;
    String command = GraphVizPath + " -Tpng " + dotFilePath + " -o " + outFilePath;
    try {
      Process proc = Runtime.getRuntime().exec(command);
      proc.waitFor();
    } catch( Exception e ) {
       System.out.println(e);
    }
  }

  public void readProp() {
    final Properties prop = new Properties();
    InputStream inStream = null;
    try {
      inStream = new BufferedInputStream(new FileInputStream("sample.properties"));
      prop.load(inStream);
      addSchema(prop.getProperty("mykey"));
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if(inStream != null) {
          inStream.close();
        }
      } catch(IOException e) {
          e.printStackTrace();
      }
    }
  }
}
