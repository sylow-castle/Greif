package runner;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import table.*;
import database.*;


public class Main extends Application {
  public Connection defaultConnection = null;
  public static Window window;
    /**
     * @param args
     */
  public static void main(String[] args) {
    /*
    Tester.test_loadTextFile();
    Tester.test_CreateGraph_2();
    */
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Class.forName("org.sqlite.JDBC");
    defaultConnection = DbFileLoader.createMemoryDB();
    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("../UI/MainWindowView.fxml"));
      VBox root = (VBox) loader.load();
      Scene scene = new Scene(root, 640, 480);
      stage.setScene(scene);
      stage.show();
      window = scene.getWindow();
    } catch(Exception e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop( )throws SQLException {
    try {
      if(defaultConnection != null){
        defaultConnection.close();
      }
    } catch (SQLException e) {
      e.printStackTrace();
    }

  }


}
