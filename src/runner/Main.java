package runner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

import java.io.*;
import java.util.Properties;

import database.*;


public class Main extends Application {
  public Connection defaultConnection = null;
  public static Window window;
  public static final Properties prop = new Properties();
    /**
     * @param args
     */
  public static void main(String[] args) {
    /*
    Tester.test_loadTextFile();
    Tester.test_CreateGraph_2();
    */
    loadConfigPropeties();
    launch(args);
  }

  @Override
  public void start(Stage stage) throws Exception {
    Class.forName("org.sqlite.JDBC");
    defaultConnection = DbFileLoader.createMemoryDB();
    try {
      URL url = ui.view.fxmlRoot.class.getResource("MainWindowView.fxml");
      FXMLLoader loader = new FXMLLoader(url);
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

    private static void loadConfigPropeties() {
      InputStream inStream = null;
      try {
        inStream = new BufferedInputStream(
            new FileInputStream("resource/config.properties"));
        prop.load(inStream);
      } catch (IOException e) {
        e.printStackTrace();
      } finally {
        try {
          if (inStream != null) {
            inStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
