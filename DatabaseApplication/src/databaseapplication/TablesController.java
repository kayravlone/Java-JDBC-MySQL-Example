 //@author Kayra Cansın Gökmen & Ahmet Emre Çakmak
package databaseapplication;

import static databaseapplication.StudentSelectedController.connection;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;


public class TablesController implements Initializable {

    @FXML
    private Button connectBT;
    @FXML
    private Button displayContentBT;
    @FXML
    private Button CustomQueryBT;
    @FXML
    private ListView<String> listView;

    public static Connection connection;
    public static String selectedValue;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showTables();
        StudentSelectedController.connection = this.connection;
        connectBT.setOnAction(b -> {
            try {
                URL composeNewUrl = getClass().getResource("Main.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();

                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("Main");
                composeNewStage.setScene(new Scene(composeNewRoot));
                composeNewStage.show();
                connection.close();
                System.out.println("Connection closed!");
                Stage currentStage = (Stage) connectBT.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {

            } catch (SQLException ex) {
                Logger.getLogger(TablesController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
        listView.setOnMouseClicked(event -> {
            selectedValue = listView.getSelectionModel().getSelectedItem();
        });

        displayContentBT.setOnAction(b -> {
            try {
                StudentSelectedController.selectedTable = selectedValue;
                URL composeNewUrl = getClass().getResource("StudentSelected.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();
                
                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("Tables");
                composeNewStage.setScene(new Scene(composeNewRoot));
                composeNewStage.show();
                Stage currentStage = (Stage) displayContentBT.getScene().getWindow();
                currentStage.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        displayContentBT.setOnAction(b -> {
            try {
                StudentSelectedController.selectedTable = selectedValue;
                URL composeNewUrl = getClass().getResource("StudentSelected.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();
                
                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("Tables");
                composeNewStage.setScene(new Scene(composeNewRoot));
                composeNewStage.show();
                Stage currentStage = (Stage) displayContentBT.getScene().getWindow();
                currentStage.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }
        });
        CustomQueryBT.setOnAction(b -> {
            try {
                QueryExecutedController.connection=connection;
                URL composeNewUrl = getClass().getResource("QueryExecuted.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();

                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("Query");
                composeNewStage.setScene(new Scene(composeNewRoot));
                composeNewStage.show();
                Stage currentStage = (Stage) CustomQueryBT.getScene().getWindow();
                currentStage.close();
            } catch (IOException ex) {

            
            }
        });
    }

    private void showTables() {
        try {
            if (connection != null && !connection.isClosed()) {
                DatabaseMetaData metaData = connection.getMetaData();
                ResultSet resultSet = metaData.getTables(null, null, "%", null);

                ObservableList<String> tableNames = FXCollections.observableArrayList();

                while (resultSet.next()) {
                    String tableName = resultSet.getString("TABLE_NAME");
                    tableNames.add(tableName);
                }

                listView.setItems(tableNames);
            } else {
                System.err.println("Database not connected!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
