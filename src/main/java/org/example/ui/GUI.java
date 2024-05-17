package org.example.ui;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.EmployeeDAO;
import org.example.dao.GroupDAO;
import org.example.entity.EmployeeEntity;
import org.example.entity.GroupEntity;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;


public class GUI extends Application {

    private final EmployeeDAO employeeDAO;
    private final GroupDAO groupDAO;

    private TableView<EmployeeEntity> employeeTableView;
    private ComboBox<GroupEntity> groupComboBox;

    public GUI() {
        Configuration configuration = new Configuration().configure();
        SessionFactory sessionFactory = configuration.buildSessionFactory();

        employeeDAO = new EmployeeDAO(sessionFactory);
        groupDAO = new GroupDAO(sessionFactory);
    }


    @Override
    public void start(Stage stage) {
        BorderPane borderPane = createBorderPane();
        Scene scene = new Scene(borderPane, 550, 500);
        setupStage(stage, scene);
    }

    private BorderPane createBorderPane() {
        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(10));
        borderPane.setTop(createButtonContainer());
        borderPane.setCenter(createGridPane());
        borderPane.setBottom(createTableContainer());
        borderPane.setRight(createGroupsVBox());
        return borderPane;
    }

    private HBox createButtonContainer() {
        Button removeButton = new Button("Usuń pracownika");
        removeButton.setOnAction(event -> removeEmployee(employeeTableView.getSelectionModel().getSelectedItem()));

        Button exportButton = new Button("Eksportuj do CSV");
        // exportButton.setOnAction(event -> exportEmployeesToCSV("src/main/resources/plik.csv"));

        return new HBox(removeButton);
    }


    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10));

        //etykiety
        Label firstNameLabel = new Label("Imię:");
        Label lastNameLabel = new Label("Nazwisko:");
        Label salaryLabel = new Label("Pensja:");
        Label groupLabel = new Label("Grupa:");

        //pola tekstowe
        TextField firstNameField = new TextField();
        TextField lastNameField = new TextField();
        TextField salaryField = new TextField();

        // ComboBox
        groupComboBox = new ComboBox<>();
        groupComboBox.setItems(FXCollections.observableArrayList(groupDAO.getAllGroups()));

        // Przyciski
        Button addButton = new Button("Dodaj pracownika");

        // Dodanie elementów do siatki
        gridPane.add(firstNameLabel, 0, 0);
        gridPane.add(firstNameField, 1, 0);
        gridPane.add(lastNameLabel, 0, 1);
        gridPane.add(lastNameField, 1, 1);
        gridPane.add(salaryLabel, 0, 2);
        gridPane.add(salaryField, 1, 2);
        gridPane.add(groupLabel, 0, 3);
        gridPane.add(groupComboBox, 1, 3);
        gridPane.add(addButton, 0, 4);

        addButton.setOnAction(event -> addEmployee(firstNameField.getText(), lastNameField.getText(),
                salaryField.getText(), groupComboBox.getValue()));

        return gridPane;
    }

    private VBox createTableContainer() {
        employeeTableView = createEmployeeTableView();
        return new VBox(new Label("Lista pracowników"), employeeTableView);
    }

    private TableView<EmployeeEntity> createEmployeeTableView() {
        TableView<EmployeeEntity> tableView = new TableView<>();
        TableColumn<EmployeeEntity, String> firstNameCol = new TableColumn<>("Imię Nazwisko");
        firstNameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<EmployeeEntity, String> salaryCol = new TableColumn<>("Pensja");
        salaryCol.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getSalary())));

        TableColumn<EmployeeEntity, String> groupCol = new TableColumn<>("Grupa");
        groupCol.setCellValueFactory(data -> {
            GroupEntity group = groupDAO.getGroupById(data.getValue().getGroupId());
            return new SimpleStringProperty(group != null ? group.getName() : "");
        });

        tableView.getColumns().addAll(firstNameCol, salaryCol, groupCol);

        tableView.getItems().addAll(employeeDAO.getAllEmployees());

        return tableView;
    }

    private VBox createGroupsVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);

        Label label = new Label("Wybierz grupę:");
        ComboBox<String> groupComboBox = new ComboBox<>();

        List<GroupEntity> groups = groupDAO.getAllGroups();
        for (GroupEntity group : groups) {
            groupComboBox.getItems().add(group.getName());
        }

        groupComboBox.setOnAction(event -> {
            String selectedGroupName = groupComboBox.getValue();
            if (selectedGroupName != null) {

                GroupEntity selectedGroup = groupDAO.getGroupByName(selectedGroupName);
                if (selectedGroup != null) {
                    List<EmployeeEntity> employeesInGroup = employeeDAO.getEmployeesByGroupId(selectedGroup.getId());

                    employeeTableView.getItems().clear();
                    employeeTableView.getItems().addAll(employeesInGroup);
                } else {
                    showAlert("Wybrana grupa nie istnieje.");
                }
            }
        });

        vBox.getChildren().addAll(label, groupComboBox);
        return vBox;
    }


    private void addEmployee(String firstName, String lastName, String salaryText, GroupEntity selectedGroup) {
        try {
            int salary = Integer.parseInt(salaryText);

            EmployeeEntity newEmployee = new EmployeeEntity();
            newEmployee.setName(firstName + " " + lastName);
            newEmployee.setSalary(salary);
            if (selectedGroup != null) {
                newEmployee.setGroupId(selectedGroup.getId());
            }

            employeeDAO.addEmployee(newEmployee);
            employeeTableView.getItems().add(newEmployee);
        } catch (NumberFormatException e) {
            showAlert("Niepoprawny format danych. Wprowadź poprawne wartości.");
        }
    }

    private void removeEmployee(EmployeeEntity employee) {
        if (employee == null) {
            showAlert("Nie wybrano pracownika do usunięcia.");
            return;
        }

        employeeDAO.deleteEmployee(employee);
        employeeTableView.getItems().remove(employee);
    }

    private void setupStage(Stage stage, Scene scene) {
        stage.setScene(scene);
        stage.setTitle("Ewidencja pracowników");
        stage.show();
    }

    private void showAlert(String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Błąd");
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
