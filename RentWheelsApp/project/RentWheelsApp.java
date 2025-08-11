import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import java.time.LocalDate;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;

public class RentWheelsApp extends Application {

    private Stage primaryStage;
    private User currentUser;
    private ObservableList<Car> cars = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();
    private ObservableList<
    Invoice> invoices = FXCollections.observableArrayList();
    private boolean isLoginMode = true;
    private List<User> registeredUsers = new ArrayList<>();
    private Timeline availabilityChecker;

    // Sample data
    private void initializeData() {
        // Initialize with admin user
        registeredUsers.add(new User("Admin User", "admin@rentwheels.com", "ADMIN", "password"));

        cars.addAll(
                new Car("Tesla Model S", "₹10020.00", "5 Seats", "Automatic", "Electric", "Available","tesla_model_s.jpg"),
                new Car("BMW X5", "₹7932.50", "5 Seats", "Automatic", "Hybrid", "Available", "bmw_x5.jpg"),
                new Car("Mercedes-Benz E-Class", "₹9185.00", "5 Seats", "Automatic", "Petrol", "Available","mercedes_e_class.jpg"),
                new Car("Audi A4", "₹7097.50", "5 Seats", "Automatic", "Diesel", "Available", "audi_a4.jpg"),
                new Car("Toyota Camry", "₹6845.00", "5 Seats", "Automatic", "Hybrid", "Available", "toyota_camry.jpg"),
                new Car("Ford Mustang", "₹8350.00", "4 Seats", "Manual", "Petrol", "Available", "ford_mustang.jpg"));
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeData();

        primaryStage.setTitle("RentWheels - Premium Car Rental Service");
        primaryStage.setMaximized(true);

        showLoginScreen();
        primaryStage.show();
    }

    private void showLoginScreen() {
        VBox root = new VBox(20);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #f5f5f5;");

        // Main container
        VBox container = new VBox(30);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(40));
        container.setMaxWidth(400);
        container.setStyle(
                "-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");

        // Logo and title
        Label carIcon = new Label("🚗");
        carIcon.setStyle("-fx-font-size: 48px; -fx-text-fill: #4285f4;");

        Label title = new Label("RentWheels");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setStyle("-fx-text-fill: #333;");

        Label subtitle = new Label("Premium Car Rental Service");
        subtitle.setStyle("-fx-text-fill: #666; -fx-font-size: 14px;");

        VBox header = new VBox(10);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(carIcon, title, subtitle);

        // Tab buttons
        HBox tabButtons = new HBox();
        tabButtons.setAlignment(Pos.CENTER);

        Button loginTab = new Button("Login");
        Button registerTab = new Button("Register");

        // Form container that will be updated
        VBox formContainer = new VBox();
        formContainer.setAlignment(Pos.CENTER);

        updateTabStyles(loginTab, registerTab);
        formContainer.getChildren().clear();
        formContainer.getChildren().add(isLoginMode ? createLoginForm() : createRegisterForm());

        loginTab.setOnAction(e -> {
            isLoginMode = true;
            updateTabStyles(loginTab, registerTab);
            formContainer.getChildren().clear();
            formContainer.getChildren().add(createLoginForm());
        });

        registerTab.setOnAction(e -> {
            isLoginMode = false;
            updateTabStyles(loginTab, registerTab);
            formContainer.getChildren().clear();
            formContainer.getChildren().add(createRegisterForm());
        });

        tabButtons.getChildren().addAll(loginTab, registerTab);

        container.getChildren().addAll(header, tabButtons, formContainer);
        root.getChildren().add(container);

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
    }

    private void updateTabStyles(Button loginTab, Button registerTab) {
        String activeStyle = "-fx-background-color: transparent; -fx-text-fill: #4285f4; -fx-border-color: transparent transparent #4285f4 transparent; -fx-border-width: 0 0 2 0; -fx-font-weight: bold; -fx-padding: 10 20;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #999; -fx-border-color: transparent; -fx-padding: 10 20;";

        if (isLoginMode) {
            loginTab.setStyle(activeStyle);
            registerTab.setStyle(inactiveStyle);
        } else {
            loginTab.setStyle(inactiveStyle);
            registerTab.setStyle(activeStyle);
        }
    }

    private VBox createLoginForm() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);

        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Enter Username");
        usernameField.setStyle(
                "-fx-background-color: #e8f0fe; -fx-border-color: transparent; -fx-padding: 12; -fx-font-size: 14px;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Enter Password");
        passwordField.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 12; -fx-font-size: 14px;");
        passwordField.setPrefWidth(300);

        Button loginButton = new Button("Login");
        loginButton.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 4; -fx-font-size: 14px;");
        loginButton.setPrefWidth(300);

        loginButton.setOnAction(e -> {
            // Check if user exists in registered users
            User foundUser = null;
            for (User user : registeredUsers) {
                if (user.getUsername().equals(usernameField.getText()) &&
                        user.getPassword().equals(passwordField.getText())) {
                    foundUser = user;
                    break;
                }
            }

            if (foundUser != null) {
                currentUser = foundUser;
                showMainApplication();
            } else {
                showAlert("Login Failed",
                        "Invalid username or password! Please register if you don't have an account.");
            }
        });

        VBox usernameBox = new VBox(5);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        VBox passwordBox = new VBox(5);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        form.getChildren().addAll(usernameBox, passwordBox, loginButton);

        return form;
    }

    private VBox createRegisterForm() {
        VBox form = new VBox(15);
        form.setAlignment(Pos.CENTER);

        Label nameLabel = new Label("Full Name");
        nameLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        TextField nameField = new TextField();
        nameField.setPromptText("Enter your full name");
        nameField.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 12; -fx-font-size: 14px;");
        nameField.setPrefWidth(300);

        Label emailLabel = new Label("Email");
        emailLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 12; -fx-font-size: 14px;");
        emailField.setPrefWidth(300);

        Label usernameLabel = new Label("Username");
        usernameLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        TextField usernameField = new TextField();
        usernameField.setPromptText("Choose a username");
        usernameField.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 12; -fx-font-size: 14px;");
        usernameField.setPrefWidth(300);

        Label passwordLabel = new Label("Password");
        passwordLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Create a password");
        passwordField.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 12; -fx-font-size: 14px;");
        passwordField.setPrefWidth(300);

        Label confirmPasswordLabel = new Label("Confirm Password");
        confirmPasswordLabel.setStyle("-fx-text-fill: #333; -fx-font-weight: bold;");

        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm your password");
        confirmPasswordField.setStyle(
                "-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 12; -fx-font-size: 14px;");
        confirmPasswordField.setPrefWidth(300);

        Button registerButton = new Button("Register");
        registerButton.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 20; -fx-background-radius: 4; -fx-font-size: 14px;");
        registerButton.setPrefWidth(300);

        registerButton.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty() || emailField.getText().trim().isEmpty() ||
                    usernameField.getText().trim().isEmpty() || passwordField.getText().trim().isEmpty()) {
                showAlert("Registration Failed", "Please fill in all fields!");
                return;
            }

            if (!passwordField.getText().equals(confirmPasswordField.getText())) {
                showAlert("Registration Failed", "Passwords do not match!");
                return;
            }

            if (!emailField.getText().contains("@")) {
                showAlert("Registration Failed", "Please enter a valid email address!");
                return;
            }

            // Check if username already exists
            boolean usernameExists = false;
            for (User user : registeredUsers) {
                if (user.getUsername().equals(usernameField.getText())) {
                    usernameExists = true;
                    break;
                }
            }

            if (usernameExists) {
                showAlert("Registration Failed", "Username already exists! Please choose a different username.");
                return;
            }

            // Registration successful - add new user
            User newUser = new User(nameField.getText(), emailField.getText(), usernameField.getText(),
                    passwordField.getText());
            registeredUsers.add(newUser);

            showSuccessDialog("Registration successful! Welcome " + nameField.getText()
                    + "!\n\nYou can now login with:\nUsername: " + usernameField.getText() + "\nPassword: "
                    + passwordField.getText());

            // Switch back to login tab
            isLoginMode = true;
            showLoginScreen();
        });

        VBox nameBox = new VBox(5);
        nameBox.getChildren().addAll(nameLabel, nameField);

        VBox emailBox = new VBox(5);
        emailBox.getChildren().addAll(emailLabel, emailField);

        VBox usernameBox = new VBox(5);
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        VBox passwordBox = new VBox(5);
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        VBox confirmPasswordBox = new VBox(5);
        confirmPasswordBox.getChildren().addAll(confirmPasswordLabel, confirmPasswordField);

        form.getChildren().addAll(nameBox, emailBox, usernameBox, passwordBox, confirmPasswordBox, registerButton);

        return form;
    }

    private void showMainApplication() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f8f9fa;");

        // Header
        HBox header = createHeader();
        root.setTop(header);

        // Main content - Available Cars by default
        VBox mainContent = createAvailableCarsView();
        root.setCenter(mainContent);

        // START THE AVAILABILITY CHECKER:
        startAvailabilityChecker();

        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
    }

    private void showAddCarDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Add New Car");

        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Add New Car");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        TextField nameField = new TextField();
        nameField.setPromptText("Car name (e.g., Honda Civic)");
        nameField.setPrefWidth(200);

        TextField priceField = new TextField();
        priceField.setPromptText("Price per day (e.g., 5000.00)");
        priceField.setPrefWidth(200);

        ComboBox<String> seatsCombo = new ComboBox<>();
        seatsCombo.getItems().addAll("2 Seats", "4 Seats", "5 Seats", "7 Seats", "8 Seats");
        seatsCombo.setValue("5 Seats");

        ComboBox<String> transmissionCombo = new ComboBox<>();
        transmissionCombo.getItems().addAll("Manual", "Automatic");
        transmissionCombo.setValue("Automatic");

        ComboBox<String> fuelCombo = new ComboBox<>();
        fuelCombo.getItems().addAll("Petrol", "Diesel", "Electric", "Hybrid");
        fuelCombo.setValue("Petrol");

        form.add(new Label("Car Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Price per Day:"), 0, 1);
        form.add(priceField, 1, 1);
        form.add(new Label("Seats:"), 0, 2);
        form.add(seatsCombo, 1, 2);
        form.add(new Label("Transmission:"), 0, 3);
        form.add(transmissionCombo, 1, 3);
        form.add(new Label("Fuel Type:"), 0, 4);
        form.add(fuelCombo, 1, 4);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #333; -fx-padding: 10 20; -fx-background-radius: 4;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button addBtn = new Button("Add Car");
        addBtn.setStyle(
                "-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        addBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
                showAlert("Error", "Please fill in all required fields!");
                return;
            }

            try {
                double price = Double.parseDouble(priceField.getText());
                String formattedPrice = "₹" + String.format("%.2f", price);

                Car newCar = new Car(
                        nameField.getText(),
                        formattedPrice,
                        seatsCombo.getValue(),
                        transmissionCombo.getValue(),
                        fuelCombo.getValue(),
                        "Available",
                        "default_car.jpg");

                cars.add(newCar);
                dialog.close();
                showSuccessDialog("Car added successfully!");

                // Refresh admin view
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminCarsView());

            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid price!");
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, addBtn);

        content.getChildren().addAll(title, form, buttonBox);

        Scene scene = new Scene(content, 500, 400);
        dialog.setScene(scene);
        dialog.show();
    }
    
    private void checkAndUpdateCarAvailability() {
    LocalDate today = LocalDate.now();
    boolean anyCarReturned = false;
    
    for (Reservation reservation : reservations) {
        // Check if reservation has ended
        if (reservation.getActualEndDate() != null && 
            !today.isBefore(reservation.getActualEndDate()) && 
            reservation.getStatus().equals("Upcoming")) {
            
            // Mark reservation as completed
            reservation.setStatus("Completed");
            
            // Make car available again
            for (Car car : cars) {
                if (car.getName().equals(reservation.getCarName())) {
                    car.setStatus("Available");
                    anyCarReturned = true;
                    break;
                }
            }
        }
    }
    
    if (anyCarReturned) {
        // Refresh the view if any cars were returned
        Platform.runLater(() -> {
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAvailableCarsView());
        });
    }
}

private void startAvailabilityChecker() {
    if (availabilityChecker != null) {
        availabilityChecker.stop();
    }
    
    // Check every 30 seconds (you can adjust this)
    availabilityChecker = new Timeline(new KeyFrame(Duration.seconds(30), e -> checkAndUpdateCarAvailability()));
    availabilityChecker.setCycleCount(Timeline.INDEFINITE);
    availabilityChecker.play();
}

private void stopAvailabilityChecker() {
    if (availabilityChecker != null) {
        availabilityChecker.stop();
    }
}

private String getCarReturnInfo(String carName) {
    for (Reservation reservation : reservations) {
        if (reservation.getCarName().equals(carName) && 
            reservation.getStatus().equals("Upcoming") &&
            reservation.getActualEndDate() != null) {
            
            LocalDate today = LocalDate.now();
            LocalDate returnDate = reservation.getActualEndDate();
            
            if (returnDate.isEqual(today)) {
                return "Returns today";
            } else if (returnDate.isAfter(today)) {
                long daysUntilReturn = ChronoUnit.DAYS.between(today, returnDate);
                if (daysUntilReturn == 1) {
                    return "Returns tomorrow";
                } else {
                    return "Returns in " + daysUntilReturn + " days";
                }
            } else {
                // Car should have been returned already
                return "Overdue return";
            }
        }
    }
    return "";
}
    
    private void showEditCarDialog(Car car) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Edit Car");

        VBox content = new VBox(15);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        Label title = new Label("Edit Car: " + car.getName());
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);

        TextField nameField = new TextField(car.getName());
        nameField.setPrefWidth(200);

        TextField priceField = new TextField(car.getPrice().replace("₹", ""));
        priceField.setPrefWidth(200);

        ComboBox<String> seatsCombo = new ComboBox<>();
        seatsCombo.getItems().addAll("2 Seats", "4 Seats", "5 Seats", "7 Seats", "8 Seats");
        seatsCombo.setValue(car.getSeats());

        ComboBox<String> transmissionCombo = new ComboBox<>();
        transmissionCombo.getItems().addAll("Manual", "Automatic");
        transmissionCombo.setValue(car.getTransmission());

        ComboBox<String> fuelCombo = new ComboBox<>();
        fuelCombo.getItems().addAll("Petrol", "Diesel", "Electric", "Hybrid");
        fuelCombo.setValue(car.getFuelType());

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Available", "Booked", "Unavailable");
        statusCombo.setValue(car.getStatus());

        form.add(new Label("Car Name:"), 0, 0);
        form.add(nameField, 1, 0);
        form.add(new Label("Price per Day:"), 0, 1);
        form.add(priceField, 1, 1);
        form.add(new Label("Seats:"), 0, 2);
        form.add(seatsCombo, 1, 2);
        form.add(new Label("Transmission:"), 0, 3);
        form.add(transmissionCombo, 1, 3);
        form.add(new Label("Fuel Type:"), 0, 4);
        form.add(fuelCombo, 1, 4);
        form.add(new Label("Status:"), 0, 5);
        form.add(statusCombo, 1, 5);

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #333; -fx-padding: 10 20; -fx-background-radius: 4;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button saveBtn = new Button("Save Changes");
        saveBtn.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        saveBtn.setOnAction(e -> {
            if (nameField.getText().trim().isEmpty() || priceField.getText().trim().isEmpty()) {
                showAlert("Error", "Please fill in all required fields!");
                return;
            }

            try {
                double price = Double.parseDouble(priceField.getText());
                String formattedPrice = "₹" + String.format("%.2f", price);

                // Update car properties
                car.setName(nameField.getText());
                car.setPrice(formattedPrice);
                car.setSeats(seatsCombo.getValue());
                car.setTransmission(transmissionCombo.getValue());
                car.setFuelType(fuelCombo.getValue());
                car.setStatus(statusCombo.getValue());

                dialog.close();
                showSuccessDialog("Car updated successfully!");

                // Refresh admin view
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminCarsView());

            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid price!");
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, saveBtn);

        content.getChildren().addAll(title, form, buttonBox);

        Scene scene = new Scene(content, 500, 500);
        dialog.setScene(scene);
        dialog.show();
    }

    private void showUserDetailsDialog(User user) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("User Details");

        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");
        content.setMaxWidth(400);

        Label title = new Label("User Details");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        VBox details = new VBox(10);

        Label nameLabel = new Label("Full Name: " + user.getName());
        nameLabel.setStyle("-fx-font-weight: bold;");

        Label emailLabel = new Label("Email: " + user.getEmail());
        Label usernameLabel = new Label("Username: " + user.getUsername());
        Label roleLabel = new Label("Role: " + (user.getUsername().equals("ADMIN") ? "Administrator" : "Customer"));
        roleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: "
                + (user.getUsername().equals("ADMIN") ? "#ff6b35" : "#4285f4") + ";");

        // Count user's reservations
        long userReservations = reservations.stream()
                .filter(r -> r.getCustomerName() != null && r.getCustomerName().equals(user.getName()))
                .count();

        Label reservationsLabel = new Label("Total Reservations: " + userReservations);

        details.getChildren().addAll(nameLabel, emailLabel, usernameLabel, roleLabel, reservationsLabel);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4;");
        closeBtn.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().add(closeBtn);

        content.getChildren().addAll(title, details, buttonBox);

        Scene scene = new Scene(content, 400, 300);
        dialog.setScene(scene);
        dialog.show();
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        // Logo
        Label logo = new Label("🚗 RentWheels");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4285f4;");

        // Navigation
        HBox navigation = new HBox(30);
        navigation.setAlignment(Pos.CENTER_LEFT);

        Button availableCarsBtn = new Button("Available Cars");
        Button reservationsBtn = new Button("My Reservations");
        Button invoicesBtn = new Button("My Invoices");

        String activeStyle = "-fx-background-color: transparent; -fx-text-fill: #4285f4; -fx-font-weight: bold; -fx-border-color: transparent transparent #4285f4 transparent; -fx-border-width: 0 0 2 0; -fx-padding: 8 0;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #666; -fx-padding: 8 0;";

        availableCarsBtn.setStyle(activeStyle);
        reservationsBtn.setStyle(inactiveStyle);
        invoicesBtn.setStyle(inactiveStyle);

        availableCarsBtn.setOnAction(e -> {
            availableCarsBtn.setStyle(activeStyle);
            reservationsBtn.setStyle(inactiveStyle);
            invoicesBtn.setStyle(inactiveStyle);
            if (isAdmin()) {
                resetAdminButtonStyles();
            }
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAvailableCarsView());
        });

        reservationsBtn.setOnAction(e -> {
            availableCarsBtn.setStyle(inactiveStyle);
            reservationsBtn.setStyle(activeStyle);
            invoicesBtn.setStyle(inactiveStyle);
            if (isAdmin()) {
                resetAdminButtonStyles();
            }
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createReservationsView());
        });

        invoicesBtn.setOnAction(e -> {
            availableCarsBtn.setStyle(inactiveStyle);
            reservationsBtn.setStyle(inactiveStyle);
            invoicesBtn.setStyle(activeStyle);
            if (isAdmin()) {
                resetAdminButtonStyles();
            }
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createInvoicesView());
        });

        navigation.getChildren().addAll(availableCarsBtn, reservationsBtn, invoicesBtn);

        // Admin navigation (only show if admin)
        if (isAdmin()) {
            Button adminCarsBtn = new Button("Admin: Manage Cars");
            Button adminUsersBtn = new Button("Admin: Manage Users");

            adminCarsBtn.setStyle(inactiveStyle);
            adminUsersBtn.setStyle(inactiveStyle);

            adminCarsBtn.setOnAction(e -> {
                resetAllButtonStyles(availableCarsBtn, reservationsBtn, invoicesBtn);
                adminCarsBtn.setStyle(activeStyle);
                adminUsersBtn.setStyle(inactiveStyle);
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminCarsView());
            });

            adminUsersBtn.setOnAction(e -> {
                resetAllButtonStyles(availableCarsBtn, reservationsBtn, invoicesBtn);
                adminCarsBtn.setStyle(inactiveStyle);
                adminUsersBtn.setStyle(activeStyle);
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminUsersView());
            });

            navigation.getChildren().addAll(adminCarsBtn, adminUsersBtn);
        }

        // Right side
        HBox rightSide = new HBox(15);
        rightSide.setAlignment(Pos.CENTER_RIGHT);

        Label welcomeLabel = new Label("Welcome, " + (currentUser != null ? currentUser.getName() : "User"));
        welcomeLabel.setStyle("-fx-text-fill: #333;");

        if (isAdmin()) {
            Label adminBadge = new Label("👑 ADMIN");
            adminBadge.setStyle(
                    "-fx-background-color: #ff6b35; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
            rightSide.getChildren().add(adminBadge);
        }

        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #4285f4; -fx-border-color: #4285f4; -fx-border-radius: 4; -fx-padding: 5 15;");
        logoutBtn.setOnAction(e -> {
            stopAvailabilityChecker();
            currentUser = null;
            isLoginMode = true;
            showLoginScreen();
        });

        rightSide.getChildren().addAll(welcomeLabel, logoutBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        header.getChildren().addAll(logo, navigation, spacer, rightSide);

        return header;
    }

    private boolean isAdmin() {
        return currentUser != null && currentUser.getUsername().equals("ADMIN");
    }

    private void resetAllButtonStyles(Button... buttons) {
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #666; -fx-padding: 8 0;";
        for (Button btn : buttons) {
            btn.setStyle(inactiveStyle);
        }
    }

    private void resetAdminButtonStyles() {
        // This will be called when switching from admin tabs to regular tabs
    }

    private VBox createAdminCarsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("👑 Admin: Manage Cars");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addCarBtn = new Button("➕ Add New Car");
        addCarBtn.setStyle(
                "-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        addCarBtn.setOnAction(e -> showAddCarDialog());

        headerBox.getChildren().addAll(title, spacer, addCarBtn);

        // Cars management table
        TableView<Car> table = new TableView<>();
        table.setItems(cars);
        table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        TableColumn<Car, String> nameCol = new TableColumn<>("Car Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<Car, Double> priceCol = new TableColumn<>("Price/Day");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceCol.setPrefWidth(120);

        TableColumn<Car, Integer> seatsCol = new TableColumn<>("Seats");
        seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));
        seatsCol.setPrefWidth(80);

        TableColumn<Car, String> transmissionCol = new TableColumn<>("Transmission");
        transmissionCol.setCellValueFactory(new PropertyValueFactory<>("transmission"));
        transmissionCol.setPrefWidth(120);

        TableColumn<Car, String> fuelCol = new TableColumn<>("Fuel Type");
        fuelCol.setCellValueFactory(new PropertyValueFactory<>("fuelType"));
        fuelCol.setPrefWidth(100);

        TableColumn<Car, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<Car, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(200);

        actionsCol.setCellFactory(col -> new TableCell<Car, Void>() {
            private final HBox buttonBox = new HBox(5);
            private final Button editBtn = new Button("✏️ Edit");
            private final Button toggleBtn = new Button("🔄 Toggle Status");
            private final Button deleteBtn = new Button("🗑️ Delete");

            {
                editBtn.setStyle(
                        "-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                toggleBtn.setStyle(
                        "-fx-background-color: #ffc107; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                deleteBtn.setStyle(
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");

                editBtn.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    showEditCarDialog(car);
                });

                toggleBtn.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());
                    if (car.getStatus().equals("Available")) {
                        car.setStatus("Unavailable");
                    } else if (car.getStatus().equals("Unavailable")) {
                        car.setStatus("Available");
                    }
                    table.refresh();
                    showSuccessDialog("Car status updated to: " + car.getStatus());
                });

                deleteBtn.setOnAction(e -> {
                    Car car = getTableView().getItems().get(getIndex());

                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Delete Car");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Are you sure you want to delete " + car.getName() + "?");

                    if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                        cars.remove(car);
                        showSuccessDialog("Car deleted successfully!");
                    }
                });

                buttonBox.getChildren().addAll(editBtn, toggleBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });

        table.getColumns().addAll(nameCol, priceCol, seatsCol, transmissionCol, fuelCol, statusCol, actionsCol);

        content.getChildren().addAll(headerBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    private VBox createAdminUsersView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("👑 Admin: Manage Users");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label totalUsersLabel = new Label("Total Users: " + registeredUsers.size());
        totalUsersLabel.setStyle(
                "-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-padding: 8 15; -fx-background-radius: 4; -fx-font-weight: bold;");

        headerBox.getChildren().addAll(title, spacer, totalUsersLabel);

        // Users management table
        TableView<User> table = new TableView<>();
        table.setItems(FXCollections.observableArrayList(registeredUsers));
        table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        TableColumn<User, String> nameCol = new TableColumn<>("Full Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(200);

        TableColumn<User, String> emailCol = new TableColumn<>("Email");
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailCol.setPrefWidth(250);

        TableColumn<User, String> usernameCol = new TableColumn<>("Username");
        usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));
        usernameCol.setPrefWidth(150);

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(cellData -> {
            String username = cellData.getValue().getUsername();
            return new SimpleStringProperty(username.equals("ADMIN") ? "Administrator" : "Customer");
        });
        roleCol.setPrefWidth(120);

        TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);

        actionsCol.setCellFactory(col -> new TableCell<User, Void>() {
            private final HBox buttonBox = new HBox(5);
            private final Button viewBtn = new Button("👁️ View Details");
            private final Button deleteBtn = new Button("🗑️ Delete");

            {
                viewBtn.setStyle(
                        "-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                deleteBtn.setStyle(
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");

                viewBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showUserDetailsDialog(user);
                });

                deleteBtn.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());

                    if (user.getUsername().equals("ADMIN")) {
                        showAlert("Cannot Delete", "Cannot delete the admin account!");
                        return;
                    }

                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Delete User");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText("Are you sure you want to delete user " + user.getName() + "?");

                    if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                        registeredUsers.remove(user);
                        table.setItems(FXCollections.observableArrayList(registeredUsers));
                        showSuccessDialog("User deleted successfully!");

                        // Update header counter
                        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminUsersView());
                    }
                });

                buttonBox.getChildren().addAll(viewBtn, deleteBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    if (user.getUsername().equals("ADMIN")) {
                        deleteBtn.setDisable(true);
                        deleteBtn.setStyle(
                                "-fx-background-color: #cccccc; -fx-text-fill: #666; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                    } else {
                        deleteBtn.setDisable(false);
                        deleteBtn.setStyle(
                                "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                    }
                    setGraphic(buttonBox);
                }
            }
        });

        table.getColumns().addAll(nameCol, emailCol, usernameCol, roleCol, actionsCol);

        content.getChildren().addAll(headerBox, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        return content;
    }

    private VBox createAvailableCarsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Available Cars");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox searchBox = new HBox(10);
        searchBox.setAlignment(Pos.CENTER_RIGHT);

        TextField searchField = new TextField();
        searchField.setPromptText("Search cars...");
        searchField.setStyle("-fx-padding: 8 12; -fx-border-color: #ddd; -fx-border-radius: 4;");
        searchField.setPrefWidth(200);

        Button filterBtn = new Button("Filter");
        filterBtn.setStyle(
                "-fx-background-color: #f8f9fa; -fx-text-fill: #333; -fx-border-color: #ddd; -fx-border-radius: 4; -fx-padding: 8 15;");

        searchBox.getChildren().addAll(searchField, filterBtn);
        headerBox.getChildren().addAll(title, spacer, searchBox);

        // Cars grid in a scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        GridPane carsGrid = new GridPane();
        carsGrid.setHgap(20);
        carsGrid.setVgap(20);
        carsGrid.setPadding(new Insets(20, 0, 20, 0));

        int col = 0, row = 0;
        for (Car car : cars) {
            VBox carCard = createCarCard(car);
            carsGrid.add(carCard, col, row);

            col++;
            if (col >= 3) {
                col = 0;
                row++;
            }
        }

        scrollPane.setContent(carsGrid);

        content.getChildren().addAll(headerBox, scrollPane);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        return content;
    }

    private VBox createCarCard(Car car) {
    VBox card = new VBox(15);
    card.setPadding(new Insets(20));
    card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");
    card.setPrefWidth(300);

    // Car image
    VBox imageContainer = new VBox();
    imageContainer.setAlignment(Pos.CENTER);
    imageContainer.setPrefHeight(150);
    imageContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #e3f2fd, #bbdefb); -fx-background-radius: 4;");
    
    try {
        // Try to load the actual image
        String imagePath = "/images/cars/" + car.getImagePath();
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        
        if (!image.isError()) {
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(260);
            imageView.setFitHeight(150);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageContainer.getChildren().add(imageView);
        } else {
            throw new Exception("Image not found");
        }
    } catch (Exception e) {
        // Fallback to placeholder with car emoji
        Label carEmoji = new Label("🚗");
        carEmoji.setStyle("-fx-font-size: 48px;");
        Label placeholderText = new Label(car.getName());
        placeholderText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        VBox placeholder = new VBox(10);
        placeholder.setAlignment(Pos.CENTER);
        placeholder.getChildren().addAll(carEmoji, placeholderText);
        imageContainer.getChildren().add(placeholder);
    }

    // Car details
    Label nameLabel = new Label(car.getName());
    nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    nameLabel.setStyle("-fx-text-fill: #333;");

    Label yearLabel = new Label("2022");
    yearLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

    // Specifications
    HBox specsBox = new HBox(15);
    specsBox.setAlignment(Pos.CENTER_LEFT);

    Label seatsLabel = new Label("👥 " + car.getSeats());
    Label transmissionLabel = new Label("⚙️ " + car.getTransmission());
    Label fuelLabel = new Label("⛽ " + car.getFuelType());

    seatsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    transmissionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
    fuelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");

    specsBox.getChildren().addAll(seatsLabel, transmissionLabel, fuelLabel);

    // Status information with return date (UPDATED SECTION)
    VBox statusInfo = new VBox(5);
    statusInfo.setAlignment(Pos.CENTER_LEFT);
    
    Label statusLabel = new Label(car.getStatus());
    
    if (car.getStatus().equals("Available")) {
        statusLabel.setStyle(
                "-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32; -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
    } else if (car.getStatus().equals("Booked")) {
        statusLabel.setStyle(
                "-fx-background-color: #fff3e0; -fx-text-fill: #f57c00; -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
        
        // Add return date info for booked cars
        String returnInfo = getCarReturnInfo(car.getName());
        if (!returnInfo.isEmpty()) {
            Label returnLabel = new Label("📅 " + returnInfo);
            returnLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 10px; -fx-font-style: italic;");
            statusInfo.getChildren().add(returnLabel);
        }
    } else {
        statusLabel.setStyle(
                "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 4 8; -fx-background-radius: 12; -fx-font-size: 11px; -fx-font-weight: bold;");
    }
    
    statusInfo.getChildren().add(0, statusLabel); // Add status label first

    // Price and button
    HBox bottomBox = new HBox();
    bottomBox.setAlignment(Pos.CENTER_LEFT);

    VBox priceBox = new VBox(5);
    Label priceLabel = new Label(car.getPrice());
    priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    priceLabel.setStyle("-fx-text-fill: #4285f4;");

    Label perDayLabel = new Label("per day");
    perDayLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

    priceBox.getChildren().addAll(priceLabel, perDayLabel);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button actionButton;
    if (car.getStatus().equals("Available")) {
        actionButton = new Button("Reserve");
        actionButton.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 8 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        actionButton.setOnAction(e -> showReservationDialog(car));
    } else {
        actionButton = new Button("Unavailable");
        actionButton.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #999; -fx-padding: 8 20; -fx-background-radius: 4;");
        actionButton.setDisable(true);
    }

    bottomBox.getChildren().addAll(priceBox, spacer, actionButton);

    // Add all components to the card (UPDATED to use statusInfo instead of statusLabel)
    card.getChildren().addAll(imageContainer, nameLabel, yearLabel, specsBox, statusInfo, bottomBox);

    return card;
}

    private void showReservationDialog(Car car) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Reserve Car");

        // Create backdrop effect
        VBox backdrop = new VBox();
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        backdrop.setAlignment(Pos.CENTER);

        VBox dialogContent = new VBox(20);
        dialogContent.setPadding(new Insets(30));
        dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        dialogContent.setMaxWidth(500);

        // Car info
        HBox carInfo = new HBox(20);
        carInfo.setAlignment(Pos.CENTER_LEFT);

        Rectangle carImage = new Rectangle(100, 60);
        carImage.setFill(Color.LIGHTBLUE);

        VBox carDetails = new VBox(5);
        Label carName = new Label(car.getName() + " (2022)");
        carName.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        Label carSpecs = new Label(car.getSeats() + " • " + car.getTransmission() + " • " + car.getFuelType());
        carSpecs.setStyle("-fx-text-fill: #666;");

        Label carPrice = new Label(car.getPrice() + " per day");
        carPrice.setStyle("-fx-text-fill: #4285f4; -fx-font-weight: bold;");

        carDetails.getChildren().addAll(carName, carSpecs, carPrice);
        carInfo.getChildren().addAll(carImage, carDetails);

        // Date selection
        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(20);
        dateGrid.setVgap(10);

        Label startDateLabel = new Label("Start Date");
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setStyle("-fx-pref-width: 200;");

        Label endDateLabel = new Label("End Date");
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        endDatePicker.setStyle("-fx-pref-width: 200;");

        dateGrid.add(startDateLabel, 0, 0);
        dateGrid.add(startDatePicker, 0, 1);
        dateGrid.add(endDateLabel, 1, 0);
        dateGrid.add(endDatePicker, 1, 1);

        // Cost calculation
        VBox costBox = new VBox(10);

        Label daysLabel = new Label("Days: 0");
        Label dailyRateLabel = new Label("Daily Rate: " + car.getPrice());
        Label totalCostLabel = new Label("Total Cost: ₹0.00");
        totalCostLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        costBox.getChildren().addAll(daysLabel, dailyRateLabel, totalCostLabel);

        // Update cost calculation
        Runnable updateCost = () -> {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            if (start != null && end != null && !end.isBefore(start)) {
                long days = ChronoUnit.DAYS.between(start, end);
                if (days == 0)
                    days = 1;
                double dailyRate = Double.parseDouble(car.getPrice().replace("₹", "").replace(",", ""));
                double total = days * dailyRate;

                daysLabel.setText("Days: " + days);
                totalCostLabel.setText("Total Cost: ₹" + String.format("%.2f", total));
            }
        };

        startDatePicker.setOnAction(e -> updateCost.run());
        endDatePicker.setOnAction(e -> updateCost.run());
        updateCost.run();

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);

        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #333; -fx-padding: 10 20; -fx-background-radius: 4;");
        cancelBtn.setOnAction(e -> dialog.close());

        Button confirmBtn = new Button("✓ Confirm Reservation");
        confirmBtn.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            if (start != null && end != null && !end.isBefore(start)) {
                long days = ChronoUnit.DAYS.between(start, end);
                if (days == 0)
                    days = 1;
                double dailyRate = Double.parseDouble(car.getPrice().replace("₹", "").replace(",", ""));
                double total = days * dailyRate;

                // Create reservation
                Reservation reservation = new Reservation(
                        car.getName(),
                start,  // Pass LocalDate directly
                end,    // Pass LocalDate directly
                "₹" + String.format("%.2f", total),
                "Upcoming",
                currentUser.getName());
                reservations.add(reservation);

                // Create invoice
                String invoiceId = "INV-" + LocalDate.now().getYear() + "-"
                        + String.format("%03d", invoices.size() + 1);
                Invoice invoice = new Invoice(
                        invoiceId,
                        car.getName(),
                        start.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + " - "
                                + end.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                        "₹" + String.format("%.2f", total),
                        LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
                invoices.add(invoice);

                // Update car status
                car.setStatus("Booked");

                dialog.close();
                showSuccessDialog("Reservation created successfully! (Prototype - no actual dates saved)");

                // Refresh the cars view
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAvailableCarsView());
            }
        });

        buttonBox.getChildren().addAll(cancelBtn, confirmBtn);

        dialogContent.getChildren().addAll(carInfo, dateGrid, costBox, buttonBox);
        backdrop.getChildren().add(dialogContent);

        Scene dialogScene = new Scene(backdrop, 800, 600);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private VBox createReservationsView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My Reservations");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button clearAllBtn = new Button("🗑 Clear All Reservations");
        clearAllBtn.setStyle(
                "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 4;");
        clearAllBtn.setOnAction(e -> {
            // Return all cars to available status
            for (Reservation reservation : reservations) {
                for (Car car : cars) {
                    if (car.getName().equals(reservation.getCarName())) {
                        car.setStatus("Available");
                        break;
                    }
                }
            }
            reservations.clear();
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createReservationsView());
            showSuccessDialog("All reservations cleared! Cars are now available for booking.");
        });

        headerBox.getChildren().addAll(title, spacer, clearAllBtn);

        // Reservations table
        TableView<Reservation> table = new TableView<>();
        table.setItems(reservations);
        table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        TableColumn<Reservation, String> carCol = new TableColumn<>("Car");
        carCol.setCellValueFactory(new PropertyValueFactory<>("carName"));
        carCol.setPrefWidth(150);

        TableColumn<Reservation, String> startCol = new TableColumn<>("Start Date");
        startCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        startCol.setPrefWidth(150);

        TableColumn<Reservation, String> endCol = new TableColumn<>("End Date");
        endCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        endCol.setPrefWidth(150);

        TableColumn<Reservation, String> totalCol = new TableColumn<>("Total Cost");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        totalCol.setPrefWidth(120);

        TableColumn<Reservation, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(100);

        TableColumn<Reservation, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);

        actionsCol.setCellFactory(col -> new TableCell<Reservation, Void>() {
            private final HBox buttonBox = new HBox(5);
            private final Button viewInvoiceBtn = new Button("📄 View Invoice");
            private final Button cancelBtn = new Button("❌ Cancel");

            {
                viewInvoiceBtn.setStyle(
                        "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");
                cancelBtn.setStyle(
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");

                viewInvoiceBtn.setOnAction(e -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    showInvoiceDialog(reservation);
                });

                cancelBtn.setOnAction(e -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());

                    // Show confirmation dialog
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Cancel Reservation");
                    confirmAlert.setHeaderText(null);
                    confirmAlert.setContentText(
                            "Are you sure you want to cancel the reservation for " + reservation.getCarName() + "?");

                    if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                        // Return car to available status
                        for (Car car : cars) {
                            if (car.getName().equals(reservation.getCarName())) {
                                car.setStatus("Available");
                                break;
                            }
                        }

                        // Remove reservation
                        reservations.remove(reservation);

                        // Remove corresponding invoice
                        invoices.removeIf(invoice -> invoice.getCarName().equals(reservation.getCarName()));

                        showSuccessDialog("Reservation cancelled successfully! " + reservation.getCarName()
                                + " is now available for booking.");

                        // Refresh views
                        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createReservationsView());
                    }
                });

                buttonBox.getChildren().addAll(viewInvoiceBtn, cancelBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });

        table.getColumns().addAll(carCol, startCol, endCol, totalCol, statusCol, actionsCol);

        content.getChildren().addAll(headerBox, table);

        return content;
    }

    private void showInvoiceDialog(Reservation reservation) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Invoice");

        VBox backdrop = new VBox();
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        backdrop.setAlignment(Pos.CENTER);

        VBox dialogContent = new VBox(20);
        dialogContent.setPadding(new Insets(30));
        dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        dialogContent.setMaxWidth(500);

        // Header
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);

        VBox companyInfo = new VBox(5);
        Label companyName = new Label("🚗 RentWheels");
        companyName.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        companyName.setStyle("-fx-text-fill: #4285f4;");

        Label companyDesc = new Label("Premium Car Rental Service");
        companyDesc.setStyle("-fx-text-fill: #666;");

        companyInfo.getChildren().addAll(companyName, companyDesc);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox invoiceInfo = new VBox(5);
        invoiceInfo.setAlignment(Pos.TOP_RIGHT);

        Label invoiceTitle = new Label("INVOICE");
        invoiceTitle.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        String invoiceId = "INV-" + LocalDate.now().getYear() + "-001";
        Label invoiceIdLabel = new Label(invoiceId);
        Label issuedLabel = new Label("Issued: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")));
        issuedLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");

        invoiceInfo.getChildren().addAll(invoiceTitle, invoiceIdLabel, issuedLabel);
        header.getChildren().addAll(companyInfo, spacer, invoiceInfo);

        // Billed to
        VBox billedTo = new VBox(5);
        Label billedToLabel = new Label("Billed To:");
        billedToLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Label customerName = new Label(currentUser != null ? currentUser.getName() : "Regular User");
        Label customerEmail = new Label(currentUser != null ? currentUser.getEmail() : "user@example.com");
        customerEmail.setStyle("-fx-text-fill: #666;");

        billedTo.getChildren().addAll(billedToLabel, customerName, customerEmail);

        // Invoice details
        VBox details = new VBox(15);
        Label detailsLabel = new Label("Description");
        detailsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        HBox itemRow = new HBox();
        itemRow.setAlignment(Pos.CENTER_LEFT);

        Label itemDesc = new Label(reservation.getCarName() + " - Rental (" + reservation.getStartDate() + " - "
                + reservation.getEndDate() + ")");
        itemDesc.setPrefWidth(300);

        Region itemSpacer = new Region();
        HBox.setHgrow(itemSpacer, Priority.ALWAYS);

        Label itemAmount = new Label("Amount");
        itemAmount.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        itemRow.getChildren().addAll(itemDesc, itemSpacer, itemAmount);

        HBox amountRow = new HBox();
        amountRow.setAlignment(Pos.CENTER_LEFT);

        Label amount = new Label(reservation.getTotalCost());
        amount.setPrefWidth(300);

        Region amountSpacer = new Region();
        HBox.setHgrow(amountSpacer, Priority.ALWAYS);

        Label amountValue = new Label(reservation.getTotalCost());
        amountValue.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        amountRow.getChildren().addAll(amount, amountSpacer, amountValue);

        // Total
        Separator separator = new Separator();

        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);

        Label totalLabel = new Label("Total");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        totalLabel.setPrefWidth(300);

        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);

        Label totalAmount = new Label(reservation.getTotalCost());
        totalAmount.setFont(Font.font("Arial", FontWeight.BOLD, 16));

        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalAmount);

        details.getChildren().addAll(detailsLabel, itemRow, amountRow, separator, totalRow);

        // Thank you message
        Label thankYou = new Label("Thank you for choosing RentWheels!");
        thankYou.setStyle("-fx-text-fill: #666; -fx-font-style: italic; -fx-alignment: center;");
        thankYou.setAlignment(Pos.CENTER);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);

        Button closeBtn = new Button("Close");
        closeBtn.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #333; -fx-padding: 10 20; -fx-background-radius: 4;");
        closeBtn.setOnAction(e -> dialog.close());

        Button downloadPdfBtn = new Button("📄 Download PDF");
        downloadPdfBtn.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        downloadPdfBtn.setOnAction(e -> showAlert("Download", "PDF download would be implemented here"));

        Button downloadTxtBtn = new Button("📝 Download Text");
        downloadTxtBtn.setStyle(
                "-fx-background-color: #34a853; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        downloadTxtBtn.setOnAction(e -> showAlert("Download", "Text download would be implemented here"));

        buttonBox.getChildren().addAll(closeBtn, downloadPdfBtn, downloadTxtBtn);

        dialogContent.getChildren().addAll(header, billedTo, details, thankYou, buttonBox);
        backdrop.getChildren().add(dialogContent);

        Scene dialogScene = new Scene(backdrop, 800, 600);
        dialog.setScene(dialogScene);
        dialog.show();
    }

    private VBox createInvoicesView() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));

        // Header
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("My Invoices");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button clearAllBtn = new Button("🗑 Clear All Invoices");
        clearAllBtn.setStyle(
                "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 8 15; -fx-background-radius: 4;");
        clearAllBtn.setOnAction(e -> {
            invoices.clear();
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createInvoicesView());
        });

        headerBox.getChildren().addAll(title, spacer, clearAllBtn);

        // Invoices table
        TableView<Invoice> table = new TableView<>();
        table.setItems(invoices);
        table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");

        TableColumn<Invoice, String> invoiceCol = new TableColumn<>("Invoice #");
        invoiceCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        invoiceCol.setPrefWidth(120);

        TableColumn<Invoice, String> carCol = new TableColumn<>("Car");
        carCol.setCellValueFactory(new PropertyValueFactory<>("carName"));
        carCol.setPrefWidth(150);

        TableColumn<Invoice, String> periodCol = new TableColumn<>("Rental Period");
        periodCol.setCellValueFactory(new PropertyValueFactory<>("rentalPeriod"));
        periodCol.setPrefWidth(250);

        TableColumn<Invoice, String> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
        totalCol.setPrefWidth(120);

        TableColumn<Invoice, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        dateCol.setPrefWidth(150);

        TableColumn<Invoice, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(150);

        actionsCol.setCellFactory(col -> new TableCell<Invoice, Void>() {
            private final HBox buttonBox = new HBox(5);
            private final Button viewBtn = new Button("👁 View");
            private final Button pdfBtn = new Button("📄 PDF");

            {
                viewBtn.setStyle(
                        "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                pdfBtn.setStyle(
                        "-fx-background-color: #34a853; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");

                viewBtn.setOnAction(e -> {
                    Invoice invoice = getTableView().getItems().get(getIndex());
                    showInvoiceDetailsDialog(invoice);
                });

                pdfBtn.setOnAction(e -> showAlert("PDF", "PDF generation would be implemented here"));

                buttonBox.getChildren().addAll(viewBtn, pdfBtn);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonBox);
            }
        });

        table.getColumns().addAll(invoiceCol, carCol, periodCol, totalCol, dateCol, actionsCol);

        content.getChildren().addAll(headerBox, table);

        return content;
    }

    private void showInvoiceDetailsDialog(Invoice invoice) {
        // Similar to showInvoiceDialog but with invoice data
        showAlert("Invoice Details", "Invoice " + invoice.getInvoiceId() + " for " + invoice.getCarName() + "\nTotal: "
                + invoice.getTotal());
    }

    private void showSuccessDialog(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

// Data Models
class User {
    private String name;
    private String email;
    private String username;
    private String password;

    public User(String name, String email, String username, String password) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}

class Car {
    private String name;
    private String price;
    private String seats;
    private String transmission;
    private String fuelType;
    private String status;
    private String imagePath;

    public Car(String name, String price, String seats, String transmission, String fuelType, String status,
            String imagePath) {
        this.name = name;
        this.price = price;
        this.seats = seats;
        this.transmission = transmission;
        this.fuelType = fuelType;
        this.status = status;
        this.imagePath = imagePath;
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getSeats() {
        return seats;
    }

    public void setSeats(String seats) {
        this.seats = seats;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImagePath() {
        return imagePath;
    }
}

class Reservation {
    private String carName;
    private String startDate;
    private String endDate;
    private String totalCost;
    private String status;
    private String customerName;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    public Reservation(String carName, String startDate, String endDate, String totalCost, String status) {
        this.carName = carName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = status;
        this.customerName = ""; // Will be set when needed
    }

    public Reservation(String carName, String startDate, String endDate, String totalCost, String status,
            String customerName) {
        this.carName = carName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = status;
        this.customerName = customerName;
    }

    // NEW CONSTRUCTOR WITH LocalDate PARAMETERS:
    public Reservation(String carName, LocalDate startDate, LocalDate endDate, String totalCost, String status, String customerName) {
        this.carName = carName;
        this.actualStartDate = startDate;
        this.actualEndDate = endDate;
        this.startDate = startDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        this.endDate = endDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
        this.totalCost = totalCost;
        this.status = status;
        this.customerName = customerName;
    }

    // Getters
    public String getCarName() {
        return carName;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getTotalCost() {
        return totalCost;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public LocalDate getActualStartDate() { 
        return actualStartDate; 
    }
    
    public LocalDate getActualEndDate() { 
        return actualEndDate; 
    }

    //Setters

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setActualStartDate(LocalDate actualStartDate) { 
        this.actualStartDate = actualStartDate; 
        this.startDate = actualStartDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }
    
    public void setActualEndDate(LocalDate actualEndDate) { 
        this.actualEndDate = actualEndDate; 
        this.endDate = actualEndDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy"));
    }

}

class Invoice {
    private String invoiceId;
    private String carName;
    private String rentalPeriod;
    private String total;
    private String date;

    public Invoice(String invoiceId, String carName, String rentalPeriod, String total, String date) {
        this.invoiceId = invoiceId;
        this.carName = carName;
        this.rentalPeriod = rentalPeriod;
        this.total = total;
        this.date = date;
    }

    // Getters
    public String getInvoiceId() {
        return invoiceId;
    }

    public String getCarName() {
        return carName;
    }

    public String getRentalPeriod() {
        return rentalPeriod;
    }

    public String getTotal() {
        return total;
    }

    public String getDate() {
        return date;
    }
}
