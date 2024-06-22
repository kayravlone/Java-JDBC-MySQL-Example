 //@author Ahmet Emre Çakmak
package databaseapplication;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;
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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StudentSelectedController implements Initializable {

    @FXML
    private Button connectBT;
    @FXML
    private Button displayContentBTN;
    @FXML
    private Button deleteSelectedBT;
    @FXML
    private Button addNewBT;
    @FXML
    private Button UpdateSelectedBT;
    @FXML
    private Label DBLabel;
    @FXML
    private ListView<String> ListView;
    @FXML
    private TableView<ObservableList<String>> tableView;

    public static Connection connection;
    public static String selectedTable;
    private ResultSetMetaData metaData;
    String whereQuery = " WHERE ";
    String addQuery = "";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showTables();
        populateTableView();
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
        UpdateSelectedBT.setOnAction(k -> {
            try {
                Stage currentStage = (Stage) addNewBT.getScene().getWindow();
                Scene scene = currentStage.getScene();

                ArrayList<TextField> list = new ArrayList<>();
                ArrayList<String> firstVariables = new ArrayList<>();
                ArrayList<TextField> TFList = new ArrayList<>();
                Parent root = scene.getRoot();

                if (root instanceof BorderPane) {
                    BorderPane borderPane = (BorderPane) root;
                    HBox hbox = new HBox();
                    borderPane.setBottom(hbox);

                    for (int i = 0; i < tableView.getColumns().size(); i++) {
                        TextField textField = new TextField();
                        list.add(textField);
                        textField.setMaxWidth(150);
                        textField.setText(tableView.getSelectionModel().getSelectedItem().get(i));
                        firstVariables.add(tableView.getSelectionModel().getSelectedItem().get(i));
                        if (!tableView.getSelectionModel().getSelectedItem().get(i).equals("")) {
                            whereQuery += tableView.getColumns().get(i).getText() + " = '"
                                    + tableView.getSelectionModel().getSelectedItem().get(i) + "'";
                            if (i != tableView.getColumns().size() - 1) {
                                whereQuery += " and ";
                            }

                        } else {
                            whereQuery += "";
                        }

                        TFList.add(textField);
                        hbox.getChildren().add(textField);
                    }

                    Button updateButton = new Button("Update");
                    updateButton.setMaxWidth(150);
                    hbox.getChildren().add(updateButton);

                    updateButton.setOnAction(a -> {
                        try {
                            StringBuilder updateQuery = new StringBuilder("UPDATE " + selectedTable + " SET ");

                            ArrayList<Object> parameters = new ArrayList<>();
                            DatabaseMetaData metadataDB = connection.getMetaData();
                            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                                String fieldName = metaData.getColumnName(i);
                                String originalValue = firstVariables.get(i - 1);
                                String newValue = list.get(i - 1).getText();

                                if (!TFList.get(i - 1).getText().equals("")) {
                                    updateQuery.append(fieldName).append(" =  '" + TFList.get(i - 1).getText() + "'");
                                    if (i != metaData.getColumnCount()) {
                                        updateQuery.append(" , ");
                                    }
                                    parameters.add(newValue);
                                } else {
                                    updateQuery.append(fieldName).append(" =  " + "NULL" + "");
                                    if (i != metaData.getColumnCount()) {
                                        updateQuery.append(" , ");
                                    }
                                    parameters.add(newValue);
                                }

                            }

                            updateQuery.append(whereQuery);

                            parameters.add(selectedValue);

                            if (parameters == null || parameters.isEmpty()) {
                                System.out.println("Error: Parameter list is null or empty.");
                            } else {
                                try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery.toString())) {

                                    int rowsAffected = preparedStatement.executeUpdate();
                                    System.out.println(rowsAffected + " row(s) updated.");
                                } catch (SQLException ex) {
                                    ex.printStackTrace();
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Error");
                                    alert.setHeaderText("Error");
                                    alert.setContentText(ex.getMessage());
                                    alert.showAndWait();
                                    if (ex.getMessage().toLowerCase().contains("duplicate entry")) {
                                        Alert duplicateEntryAlert = new Alert(AlertType.ERROR);
                                        duplicateEntryAlert.setTitle("Duplicate Entry Error");
                                        duplicateEntryAlert.setHeaderText("Duplicate Entry");
                                        duplicateEntryAlert.setContentText("The entry you are trying to update already exists in the database.");

                                        duplicateEntryAlert.showAndWait();
                                    }
                                }
                            }

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

                                Stage currentStage2 = (Stage) updateButton.getScene().getWindow();
                                currentStage2.close();
                            } catch (Exception ex) {
                                System.out.println("Error loading StudentSelected.fxml: " + ex.getMessage());
                            }
                        } catch (Exception ex) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Error");
                            alert.setHeaderText("Error");
                            alert.setContentText(ex.getMessage());
                            alert.showAndWait();
                        }
                    });
                } else {
                    System.out.println("Root is not an instance of BorderPane.");
                }
            } catch (Exception ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Error");
                alert.setContentText(ex.getMessage());
                alert.showAndWait();
            }
        });

        addNewBT.setOnAction(t -> {

            Stage currentStage = (Stage) addNewBT.getScene().getWindow();
            Scene scene = currentStage.getScene();
            addQuery = "INSERT INTO " + selectedTable + " VALUES (";
            ArrayList<TextField> list = new ArrayList<>();
            Parent root = scene.getRoot();

            if (root instanceof BorderPane) {
                BorderPane borderPane = (BorderPane) root;
                HBox hbox = new HBox();
                borderPane.setBottom(hbox);
                try {
                    for (int i = 1; i <= metaData.getColumnCount(); i++) {
                        TextField textField = new TextField();
                        textField.setMaxWidth(150);
                        textField.setPromptText(metaData.getColumnName(i));
                        hbox.getChildren().add(textField);
                        list.add(textField);
                    }
                    Button addButton = new Button();
                    addButton.setText("add");
                    addButton.setMaxWidth(150);
                    hbox.getChildren().add(addButton);
                    addButton.setOnAction(b -> {
                        try {
                            for (int i = 0; i < list.size(); i++) {
                                if (i < list.size() - 1) {
                                    if (list.get(i).getText().equals("")) {
                                        addQuery += "" + "NULL" + " , ";
                                    } else {
                                        addQuery += "'" + list.get(i).getText() + "' , ";
                                    }

                                } else {
                                    if (list.get(i).getText().equals("")) {
                                        addQuery += "" + "NULL" + ") ";
                                    } else {
                                        addQuery += "'" + list.get(i).getText() + "') ";
                                    }

                                }
                            }
                            try {
                                Statement statement = connection.createStatement();
                                int result = statement.executeUpdate(addQuery);

                                if (result > 0) {
                                    System.out.println("Veritabanına ekleme başarılı!");
                                } else {
                                    System.out.println("Veritabanına ekleme başarısız!");
                                }
                            } catch (SQLException e) {
                                e.printStackTrace();
                                System.out.println("Exception message: " + e.getMessage());

                                {
                                    Alert genericSqlErrorAlert = new Alert(AlertType.ERROR);
                                    genericSqlErrorAlert.setTitle("SQL Hatası");
                                    genericSqlErrorAlert.setHeaderText("SQL Hatası");
                                    genericSqlErrorAlert.setContentText("SQL hatası oluştu: " + e.getMessage());

                                    genericSqlErrorAlert.showAndWait();
                                }
                            }

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
                            Stage currentStage2 = (Stage) addButton.getScene().getWindow();
                            currentStage2.close();
                        } catch (Exception ex) {
                            System.out.println(ex.getMessage());
                        }
                    });
                } catch (SQLException ex) {
                    Logger.getLogger(StudentSelectedController.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else {
                System.out.println("Kök düğüm (root) bir BorderPane değil.");
            }
        });
        ListView.setOnMouseClicked(event -> {
            selectedValue = ListView.getSelectionModel().getSelectedItem();
        });

        deleteSelectedBT.setOnAction(t -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();

            if (selectedIndex >= 0) {
                try {
                    DatabaseMetaData metaData = connection.getMetaData();
                    String selectedTableName = selectedTable;

                    if (selectedTableName != null && !selectedTableName.isEmpty()) {
                        ResultSet primaryKeys = metaData.getPrimaryKeys(null, null, selectedTableName);
                        primaryKeys.next();
                        String primaryKeyColumn = primaryKeys.getString("COLUMN_NAME");
                        primaryKeys.close();

                        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + selectedTableName);

                        resultSet.absolute(selectedIndex + 1);
                        resultSet.deleteRow();

                        tableView.getItems().remove(selectedIndex);
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Foreign Key Constraint Violation");
                    alert.setContentText("Cannot delete or update a parent row: a foreign key constraint fails.");
                    alert.showAndWait();
                }
            }

        });
        displayContentBTN.setOnAction(b -> {
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
                Stage currentStage = (Stage) displayContentBTN.getScene().getWindow();
                currentStage.close();
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
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
    }

    private void populateTableView() {
        try {
            String query = "SELECT * FROM " + selectedTable;

            PreparedStatement preparedStatement = connection.prepareStatement(query);

            ResultSet resultSet = preparedStatement.executeQuery();

            metaData = resultSet.getMetaData();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                int columnIndex = i;
                TableColumn<ObservableList<String>, String> column = new TableColumn<>(metaData.getColumnName(i));
                column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(columnIndex - 1)));

                tableView.getColumns().add(column);
            }

            while (resultSet.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String value = resultSet.getString(i);

                    if (resultSet.wasNull()) {
                        value = "";
                    }

                    row.add(value);
                }
                tableView.getItems().add(row);
            }

            preparedStatement.close();
            resultSet.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
