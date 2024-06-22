 //@author Kayra Cansın Gökmen
package databaseapplication;

import static databaseapplication.StudentSelectedController.connection;
import static databaseapplication.StudentSelectedController.selectedTable;
import static databaseapplication.TablesController.connection;
import static databaseapplication.TablesController.selectedValue;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class QueryExecutedController implements Initializable {

    @FXML
    private Button connectBT;
    @FXML
    private Button displayContentBT;
    @FXML
    private Button CustomQueryBT;
    @FXML
    private ListView<String> ListView;
    @FXML
    private TextArea QueryArea;
    @FXML
    private Button ExecuteBT;
    public static Connection connection;
    public String query;
    @FXML
    private Label DBLabel;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showTables();
        DBLabel.setOnMouseClicked(a -> {
            try {
                StudentSelectedController.selectedTable = selectedValue;
                URL composeNewUrl = getClass().getResource("Tables.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();

                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("Main");
                composeNewStage.setScene(new Scene(composeNewRoot));
                composeNewStage.show();
                Stage currentStage2 = (Stage) DBLabel.getScene().getWindow();
                currentStage2.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

        });
        ListView.setOnMouseClicked(event -> {
            selectedValue = ListView.getSelectionModel().getSelectedItem();
        });

        ExecuteBT.setOnAction(a -> {
    try {
        query = QueryArea.getText();
        TableView<Object[]> resultTableView = new TableView<>();
        resultTableView.setMaxHeight(160);
        resultTableView.setMaxWidth(600);

        PreparedStatement preparedStatement = connection.prepareStatement(query);

        if (!query.trim().toLowerCase().startsWith("insert")) {
            ResultSet resultSet = preparedStatement.executeQuery();

            ResultSetMetaData metaData = resultSet.getMetaData();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                int columnIndex = i; 
                TableColumn<Object[], Object> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue()[columnIndex - 1]));

                resultTableView.getColumns().add(column);
            }

            while (resultSet.next()) {
                Object[] row = new Object[metaData.getColumnCount()];

                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    row[i - 1] = resultSet.getObject(i);
                }

                resultTableView.getItems().add(row);
            }

            resultSet.close();

        } else {
            System.out.println("INSERT sorgusu çalıştırıldı.");

            int affectedRows = preparedStatement.executeUpdate();
            System.out.println("Etkilenen satır sayısı: " + affectedRows);
        }

        preparedStatement.close();

        Stage currentStage = (Stage) ExecuteBT.getScene().getWindow();
        Scene scene = currentStage.getScene();

        Parent root = scene.getRoot();

        if (root instanceof BorderPane) {
            BorderPane borderPane = (BorderPane) root;
            borderPane.setBottom(resultTableView);
        } else {
            System.out.println("Kök düğüm (root) bir BorderPane değil.");
        }

    } catch (Exception e) {
        e.printStackTrace();
    Alert alert = new Alert(AlertType.ERROR);
    alert.setTitle("Hata");
    alert.setHeaderText("Sorgu Hatası");
    alert.setContentText("Girilen sorgu hatalı. Lütfen doğru bir SQL sorgusu girin.");

    alert.showAndWait();
    }
});
        connectBT.setOnAction(b -> {
            try {
                URL composeNewUrl = getClass().getResource("Main.fxml");
                FXMLLoader fxmlLoader = new FXMLLoader(composeNewUrl);
                Parent composeNewRoot = fxmlLoader.load();

                Stage composeNewStage = new Stage();
                composeNewStage.setMinWidth(600);
                composeNewStage.setMinHeight(450);
                composeNewStage.setTitle("");
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

                ListView.setItems(tableNames);
            } else {
                System.err.println("Database not connected!");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
