 //@author Kayra Cansın Gökmen
package databaseapplication;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;


public class MainController implements Initializable {

    @FXML
    private Button connectBT;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/java_project";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; 
    @FXML
    private Button displayContentBT;
    @FXML
    private Button CustomQueryBT;
    @FXML
    private ListView<?> ListView;
    @FXML
    private TextArea QueryArea;
    @FXML
    private Button ExecuteBT;
   
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        connectBT.setOnAction(c -> {
            connectToDatabase();
            try {
                URL composeNewUrl = getClass().getResource("Tables.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();
                
                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("");
                composeNewStage.setScene(new Scene(composeNewRoot));
                composeNewStage.show();
                Stage currentStage = (Stage) connectBT.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {
                
            }
        });
    }
    
    private void connectToDatabase() {
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            System.out.println("Connected to the database!");
            TablesController.connection=connection;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
    
}
