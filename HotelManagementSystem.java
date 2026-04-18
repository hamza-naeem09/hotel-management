import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.*;
import java.util.*;

public class HotelManagementSystem extends Application {

    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "1234";
    private final String CUSTOMER_FILE = "customerData.txt";
    private final String STAFF_FILE = "staffData.txt";

    Stage window;
    final int WIDTH = 700, HEIGHT = 650;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        showLoginScene();
    }

    private void showLoginScene() {
        Label title = new Label("Hotel Management Login");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));

        Label userLabel = new Label("Username:");
        userLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField userField = createTextField("Enter username");

        Label passLabel = new Label("Password:");
        passLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        PasswordField passField = createPasswordField("Enter password");

        Button loginBtn = createStyledButton("Login");
        loginBtn.setPrefWidth(100);
        Label msg = new Label();
        msg.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        loginBtn.setOnAction(e -> {
            if (userField.getText().equals(ADMIN_USER) && passField.getText().equals(ADMIN_PASS)) {
                showDashboard();
            } else {
                msg.setText("Invalid credentials!");
            }
        });

        VBox layout = new VBox(10,title, userLabel, userField, passLabel, passField, loginBtn, msg);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f0f4f7;");

        // Prevent auto-focus on input fields
        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        layout.requestFocus(); // Prevent initial auto-focus on text fields


        //Scene scene = new Scene(layout, WIDTH, HEIGHT);
        window.setScene(scene);
        window.setTitle("Hotel Login");
        window.show();
    }


    private void showDashboard() {
        Label title = new Label("Admin Dashboard");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));

        Button customerBtn = createStyledButton("Manage Customers");
        customerBtn.setPrefWidth(180);

        Button staffBtn = createStyledButton("Manage Staff");
        staffBtn.setPrefWidth(180);
        Button logoutBtn = createStyledButton("Logout");
        logoutBtn.setPrefWidth(180);

        VBox layout = new VBox(15, title, customerBtn, staffBtn, logoutBtn);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(30));
        layout.setStyle("-fx-background-color: #e1f5fe;");

        customerBtn.setOnAction(e -> showCustomerScene());
        staffBtn.setOnAction(e -> showStaffScene());
        logoutBtn.setOnAction(e -> showLoginScene());

        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        layout.requestFocus(); // prevent button auto-focus
        window.setScene(scene);
        window.setTitle("Admin Dashboard");
    }

    private void showCustomerScene() {
        Label title = new Label("Customer Management");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));

        Label nameLabel = new Label("Customer Name:");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField nameField = createTextField("e.g. Khawaja Hasnain");


        Label phoneLabel = new Label("Phone:");
        phoneLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField phoneField = createTextField("e.g. 0300-123456789");

        Label roomLabel = new Label("Room No:");
        roomLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField roomField = createTextField("e.g. 101");

        Button saveBtn = createStyledButton("Save Customer");
        saveBtn.setPrefWidth(200);

        Button viewBtn = createStyledButton("View All Customers");
        viewBtn.setPrefWidth(200);

        Button backBtn = createStyledButton("Back");
        backBtn.setPrefWidth(200);

        // New Button for Check Room Availability
        Button checkAvailabilityBtn = createStyledButton("Check Availability");
        checkAvailabilityBtn.setPrefWidth(200);

        Label msg = new Label();
        msg.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String room = roomField.getText().trim();

            String errorType = ""; // Will hold the type of error

            // Identify the type of error
            if (name.isEmpty() || phone.isEmpty() || room.isEmpty()) {
                errorType = "EMPTY";
            } else if (!name.matches("[a-zA-Z ]+")) {
                errorType = "INVALID_NAME";
            } else if (!phone.matches("\\d{4}-\\d{7}")) {
                errorType = "INVALID_PHONE";
            } else if (!room.matches("\\d+")) {
                errorType = "INVALID_ROOM";
            }else if (isRoomOccupied(room)) {
                errorType = "ROOM_OCCUPIED";
            }

            // Handle each case in the switch
            switch (errorType) {
                case "EMPTY":
                    showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                    return;
                case "INVALID_NAME":
                    showAlert("Input Error", "Customer Name must contain only alphabets!", Alert.AlertType.ERROR);
                    return;
                case "INVALID_PHONE":
                    showAlert("Input Error", "Phone must be in format 0300-1234567", Alert.AlertType.ERROR);
                    return;
                case "INVALID_ROOM":
                    showAlert("Input Error", "Room Number must contain only Numbers!", Alert.AlertType.ERROR);
                    return;
                case "ROOM_OCCUPIED":
                    showAlert("Input Error", "This Room Number is already occupied!", Alert.AlertType.ERROR);
                    return;
                    default:
                    // If there are no errors, save the data
                    saveToFile(CUSTOMER_FILE, name + "," + phone + "," + room);
                    showAlert("Success", "Customer Saved Successfully!", Alert.AlertType.INFORMATION);

                    // Clear the fields
                    nameField.clear();
                    phoneField.clear();
                    roomField.clear();
            }
        });


        viewBtn.setOnAction(e -> showCustomerTable());
        backBtn.setOnAction(e -> showDashboard());

        // Event for Check Room Availability
        checkAvailabilityBtn.setOnAction(e -> {
            String room = roomField.getText().trim();
            if (room.isEmpty()) {
                msg.setText("Enter room number to check.");
                return;
            }
            if (!room.matches("[0-9980 ]+")) {
                showAlert("Input Error", "Room Number must contain only Numbers!", Alert.AlertType.ERROR);
                return;
            }
            if (isRoomOccupied(room)) {
                msg.setText("Room is occupied.");
            } else {
                msg.setText("Room is available.");
            }
        });

        VBox layout = new VBox(10, title, nameLabel, nameField, phoneLabel, phoneField, roomLabel, roomField,
                saveBtn, checkAvailabilityBtn, viewBtn, backBtn, msg);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #f1f8e9;");

        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        layout.requestFocus(); // remove text field autofocus
        window.setScene(scene);
        window.setTitle("Customer Management");
    }

    /**
     * Checks if the given room is already occupied by reading from the file.
     */
    private boolean isRoomOccupied(String roomNumber) {
        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3 && data[2].equals(roomNumber)) {
                    return true; // Room is occupied
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading customer data.");
        }
        return false; // Room is available
    }



    private void showStaffScene() {
        Label title = new Label("Staff Management");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));

        Label nameLabel = new Label("Staff Name:");
        nameLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField nameField = createTextField("e.g. Alice Smith");

        Label roleLabel = new Label("Role:");
        roleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField roleField = createTextField("e.g. Manager");

        Label salaryLabel = new Label("Salary:");
        salaryLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        TextField salaryField = createTextField("e.g. 50000");

        Button saveBtn = createStyledButton("Save Staff");
        saveBtn.setPrefWidth(200);
        Button viewBtn = createStyledButton("View All Staff");
        viewBtn.setPrefWidth(200);
        Button backBtn = createStyledButton("Back");
        backBtn.setPrefWidth(200);
        Label msg = new Label();
        msg.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String role = roleField.getText().trim();
            String salary = salaryField.getText().trim();

            String errorType = ""; // Will hold the type of error

            // Identify the type of error
            if (name.isEmpty() || role.isEmpty() || salary.isEmpty()) {
                errorType = "EMPTY";
            } else if (!name.matches("[a-zA-Z ]+")) {
                errorType = "INVALID_NAME";
            } else if (!role.matches("[a-zA-Z ]+")) {
                errorType = "INVALID_ROLE";
            } else if (!salary.matches("\\d+")) {
                errorType = "INVALID_SALARY";
            }

            // Handle each case in the switch
            switch (errorType) {
                case "EMPTY":
                    showAlert("Error", "Please fill in all fields", Alert.AlertType.ERROR);
                    return;
                case "INVALID_NAME":
                    showAlert("Input Error", "Staff Name must contain only alphabets!", Alert.AlertType.ERROR);
                    return;
                case "INVALID_ROLE":
                    showAlert("Input Error", "Role must contain only alphabets!", Alert.AlertType.ERROR);
                    return;
                case "INVALID_SALARY":
                    showAlert("Input Error", "Salary must contain only numbers!", Alert.AlertType.ERROR);
                    return;
                default:
                    // If there are no errors, save the data
                    saveToFile(STAFF_FILE, name + "," + role + "," + salary);
                    showAlert("Success", "Staff Saved Successfully!", Alert.AlertType.INFORMATION);

                    // Clear the fields
                    nameField.clear();
                    roleField.clear();
                    salaryField.clear();
            }
        });


        viewBtn.setOnAction(e -> showStaffTable());
        backBtn.setOnAction(e -> showDashboard());

        VBox layout = new VBox(10,title, nameLabel, nameField, roleLabel, roleField, salaryLabel, salaryField, saveBtn, viewBtn, backBtn, msg);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #fff3e0;");

        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        layout.requestFocus(); // fix focus delay
        window.setScene(scene);
        window.setTitle("Staff Management");
    }


    private void showCustomerTable() {
        Label title = new Label("Customer List");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));
        TableView<Customer> table = new TableView<>();

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

        TableColumn<Customer, String> roomCol = new TableColumn<>("Room No");
        roomCol.setCellValueFactory(new PropertyValueFactory<>("room"));

        table.getColumns().addAll(nameCol, phoneCol, roomCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try (BufferedReader reader = new BufferedReader(new FileReader(CUSTOMER_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3)
                    table.getItems().add(new Customer(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.out.println("Error loading customer data.");
        }

        Button deleteBtn = createStyledButton("Delete Selected");
        deleteBtn.setOnAction(e -> {
            Customer selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                table.getItems().remove(selected);
                saveAllToFile(CUSTOMER_FILE, table.getItems());
            }
        });
        Button deleteAllBtn = createStyledButton("Delete All");
        deleteAllBtn.setOnAction(e -> {
            table.getItems().clear(); // Clear the table
            saveAllToFile(CUSTOMER_FILE, table.getItems()); // Clear the file as well
        });

        Button backBtn = createStyledButton("Back");
        backBtn.setPrefWidth(100);
        backBtn.setOnAction(e -> showCustomerScene());

        VBox layout = new VBox(10, title, table, deleteBtn, deleteAllBtn, backBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #e8eaf6;");

        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        layout.requestFocus(); // fix focus delay
        window.setScene(scene);
        window.setTitle("Customer List");
    }

    private void showStaffTable() {
        Label title = new Label("Staff List");
        title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#2c3e50"));
        TableView<Staff> table = new TableView<>();

        TableColumn<Staff, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Staff, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

        TableColumn<Staff, String> salaryCol = new TableColumn<>("Salary");
        salaryCol.setCellValueFactory(new PropertyValueFactory<>("salary"));

        table.getColumns().addAll(nameCol, roleCol, salaryCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        try (BufferedReader reader = new BufferedReader(new FileReader(STAFF_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3)
                    table.getItems().add(new Staff(parts[0], parts[1], parts[2]));
            }
        } catch (IOException e) {
            System.out.println("Error loading staff data.");
        }

        Button deleteBtn = createStyledButton("Delete Selected");
        deleteBtn.setOnAction(e -> {
            Staff selected = table.getSelectionModel().getSelectedItem();
            if (selected != null) {
                table.getItems().remove(selected);
                saveAllToFile(STAFF_FILE, table.getItems());
            }
        });
        Button deleteAllBtn = createStyledButton("Delete All");
        deleteAllBtn.setOnAction(e -> {
            table.getItems().clear(); // Clear the table
            saveAllToFile(STAFF_FILE, table.getItems()); // Clear the file as well
        });

        Button backBtn = createStyledButton("Back");
        backBtn.setPrefWidth(100);
        backBtn.setOnAction(e -> showStaffScene());

        VBox layout = new VBox(10, title, table,deleteBtn, deleteAllBtn, backBtn);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #e8eaf6;");

        Scene scene = new Scene(layout, WIDTH, HEIGHT);
        layout.requestFocus(); // fix focus delay
        window.setScene(scene);
        window.setTitle("Staff List");
    }

    private void saveToFile(String fileName, String data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName, true))) {
            writer.write(data);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    private <T> void saveAllToFile(String fileName, List<T> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (T item : items) {
                if (item instanceof Customer c) {
                    writer.write(c.getName() + "," + c.getPhone() + "," + c.getRoom());
                } else if (item instanceof Staff s) {
                    writer.write(s.getName() + "," + s.getRole() + "," + s.getSalary());
                }
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving list to file: " + e.getMessage());
        }
    }

    public static class Customer {
        private String name;
        private String phone;
        private String room;

        public Customer(String name, String phone, String room) {
            this.name = name;
            this.phone = phone;
            this.room = room;
        }

        public String getName() { return name; }
        public String getPhone() { return phone; }
        public String getRoom() { return room; }
    }

    public static class Staff {
        private String name;
        private String role;
        private String salary;

        public Staff(String name, String role, String salary) {
            this.name = name;
            this.role = role;
            this.salary = salary;
        }

        public String getName() { return name; }
        public String getRole() { return role; }
        public String getSalary() { return salary; }
    }

    private Button createStyledButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 20;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);
        """);
        btn.setOnMouseEntered(e -> btn.setStyle("""
            -fx-background-color: #2980b9;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 20;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 5, 0, 0, 2);
        """));
        btn.setOnMouseExited(e -> btn.setStyle("""
            -fx-background-color: #3498db;
            -fx-text-fill: white;
            -fx-font-size: 16px;
            -fx-padding: 10px 20px;
            -fx-background-radius: 20;
            -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);
        """));
        return btn;
    }
    private TextField createTextField(String promptText) {
        TextField tf = new TextField();
        tf.setPromptText(promptText);
        tf.setStyle("""
            -fx-font-size: 14px;
            -fx-padding: 10px;
            -fx-border-color: #bdc3c7;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
            -fx-background-color: #ffffff;
        """);
        tf.setMaxWidth(250);
        return tf;
    }
    private PasswordField createPasswordField(String promptText) {
        PasswordField tf = new PasswordField();
        tf.setPromptText(promptText);
        tf.setStyle("""
            -fx-font-size: 14px;
            -fx-padding: 10px;
            -fx-border-color: #bdc3c7;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
            -fx-background-color: #ffffff;
        """);
        tf.setMaxWidth(250);
        return tf;
    }
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
