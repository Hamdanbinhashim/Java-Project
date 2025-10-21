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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import javafx.application.Platform;

// Additional imports for database
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RentWheelsApp extends Application {

    private Stage primaryStage;
    private User currentUser;
    private DatabaseManager dbManager;
    private boolean isLoginMode = true;
    private Timeline availabilityChecker;
    private ScrollPane carsScrollPane;
    private List<Car> allCars;
    private List<Button> navButtons = new ArrayList<>(); // To manage all nav buttons

    // Filter controls as instance variables for real-time updates
    private TextField carSearchField;
    private ComboBox<String> seatsFilterCombo;
    private ComboBox<String> transmissionFilterCombo;
    private ComboBox<String> fuelFilterCombo;
    private TextField maxPriceFilterField;

    private void initializeData() {
        // Initialize database
        dbManager = DatabaseManager.getInstance();
        allCars = dbManager.getAllCars();
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        initializeData();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (dbManager != null) {
                dbManager.closeConnection();
            }
        }));

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
            User foundUser = dbManager.authenticateUser(usernameField.getText(), passwordField.getText());

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
            if (dbManager.userExists(usernameField.getText())) {
                showAlert("Registration Failed", "Username already exists! Please choose a different username.");
                return;
            }

            // Registration successful - add new user
            User newUser = new User(nameField.getText(), emailField.getText(), usernameField.getText(),
                    passwordField.getText());

            if (dbManager.insertUser(newUser)) {
                showSuccessDialog("Registration successful! Welcome " + nameField.getText()
                        + "!\n\nYou can now login with:\nUsername: " + usernameField.getText() + "\nPassword: "
                        + passwordField.getText());

                // Switch back to login tab
                isLoginMode = true;
                showLoginScreen();
            } else {
                showAlert("Registration Failed", "Error creating account. Please try again.");
            }
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

        comprehensiveDebug();

        // Header
        HBox header = createHeader();
        root.setTop(header);

        // Main content - Available Cars by default
        BorderPane mainContent = createAvailableCarsView();
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

                if (dbManager.insertCar(newCar)) {
                    dialog.close();
                    showSuccessDialog("Car added successfully!");
                    refreshAvailableCarsView(); // Real-time update
                    ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminCarsView());
                } else {
                    showAlert("Error", "Failed to add car to database!");
                }

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

        List<Reservation> allReservations = dbManager.getAllReservations();

        for (Reservation reservation : allReservations) {
            if (reservation.getActualEndDate() != null &&
                    !today.isBefore(reservation.getActualEndDate()) &&
                    "Upcoming".equals(reservation.getStatus())) {

                if (dbManager.updateReservationStatus(reservation.getCarName(),
                        reservation.getCustomerName(), "Completed") &&
                        dbManager.updateCarStatus(reservation.getCarName(), "Available")) {
                    anyCarReturned = true;
                }
            }
        }

        if (anyCarReturned) {
            refreshAvailableCarsView(); // Real-time update
        }
    }

    private String getCarReturnInfo(String carName) {
        List<Reservation> allReservations = dbManager.getAllReservations();

        for (Reservation reservation : allReservations) {
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
                    return "Overdue return";
                }
            }
        }
        return "";
    }

    private void startAvailabilityChecker() {
        if (availabilityChecker != null) {
            availabilityChecker.stop();
        }
    
        availabilityChecker = new Timeline(new KeyFrame(Duration.seconds(30), e -> checkAndUpdateCarAvailability()));
        availabilityChecker.setCycleCount(Timeline.INDEFINITE);
        availabilityChecker.play();
    }

    private void stopAvailabilityChecker() {
        if (availabilityChecker != null) {
            availabilityChecker.stop();
        }
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

                car.setName(nameField.getText());
                car.setPrice(formattedPrice);
                car.setSeats(seatsCombo.getValue());
                car.setTransmission(transmissionCombo.getValue());
                car.setFuelType(fuelCombo.getValue());
                car.setStatus(statusCombo.getValue());

                if (dbManager.updateCar(car)) {
                    dialog.close();
                    showSuccessDialog("Car updated successfully!");
                    refreshAvailableCarsView(); // Real-time update
                    ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminCarsView());
                } else {
                    showAlert("Error", "Failed to update car in database!");
                }

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

    List<Reservation> userReservations = dbManager.getUserReservations(user.getName());
    Label reservationsLabel = new Label("Total Reservations: " + userReservations.size());

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

    private void setActiveNavButton(Button activeButton) {
        String activeStyle = "-fx-background-color: transparent; -fx-text-fill: #4285f4; -fx-font-weight: bold; -fx-border-color: transparent transparent #4285f4 transparent; -fx-border-width: 0 0 2 0; -fx-padding: 8 0;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #666; -fx-padding: 8 0;";

        for (Button btn : navButtons) {
            btn.setStyle(inactiveStyle);
        }
        if (activeButton != null) {
            activeButton.setStyle(activeStyle);
        }
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");
        navButtons.clear(); // Clear previous buttons before creating new ones

        // Logo
        Label logo = new Label("🚗 RentWheels  ");
        logo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4285f4;");

        // Navigation
        HBox navigation = new HBox(30);
        navigation.setAlignment(Pos.CENTER_LEFT);

        Button availableCarsBtn = new Button("Available Cars");
        Button reservationsBtn = new Button("My Reservations");
        Button invoicesBtn = new Button("My Invoices");

        navButtons.addAll(List.of(availableCarsBtn, reservationsBtn, invoicesBtn));

        availableCarsBtn.setOnAction(e -> {
            setActiveNavButton(availableCarsBtn);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAvailableCarsView());
        });

        reservationsBtn.setOnAction(e -> {
            setActiveNavButton(reservationsBtn);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createReservationsView());
        });

        invoicesBtn.setOnAction(e -> {
            setActiveNavButton(invoicesBtn);
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createInvoicesView());
        });

        navigation.getChildren().addAll(availableCarsBtn, reservationsBtn, invoicesBtn);

        // Admin navigation (only show if admin)
        if (isAdmin()) {
            Button adminCarsBtn = new Button("Admin: Manage Cars");
            Button adminUsersBtn = new Button("Admin: Manage Users");
            
            navButtons.addAll(List.of(adminCarsBtn, adminUsersBtn));

            adminCarsBtn.setOnAction(e -> {
                setActiveNavButton(adminCarsBtn);
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminCarsView());
            });

            adminUsersBtn.setOnAction(e -> {
                setActiveNavButton(adminUsersBtn);
                ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminUsersView());
            });

            navigation.getChildren().addAll(adminCarsBtn, adminUsersBtn);
        }
        
        setActiveNavButton(availableCarsBtn); // Set the default active button

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

    List<Car> carsList = dbManager.getAllCars();

    TableView<Car> table = new TableView<>();
    ObservableList<Car> observableCarsList = FXCollections.observableArrayList(carsList);
    table.setItems(observableCarsList);
    table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
    
    if (carsList.isEmpty()) {
        Label noDataLabel = new Label("No cars in database. Click 'Add New Car' to add cars.");
        noDataLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-padding: 50px;");
        table.setPlaceholder(noDataLabel);
    }

    TableColumn<Car, String> nameCol = new TableColumn<>("Car Name");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameCol.setPrefWidth(200);

    TableColumn<Car, String> priceCol = new TableColumn<>("Price/Day");
    priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
    priceCol.setPrefWidth(120);

    TableColumn<Car, String> seatsCol = new TableColumn<>("Seats");
    seatsCol.setCellValueFactory(new PropertyValueFactory<>("seats"));
    seatsCol.setPrefWidth(100);

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
    actionsCol.setPrefWidth(250);

    actionsCol.setCellFactory(col -> new TableCell<Car, Void>() {
        private final HBox buttonBox = new HBox(5);
        private final Button editBtn = new Button("✏️ Edit");
        private final Button toggleBtn = new Button("🔄 Toggle Status");
        private final Button deleteBtn = new Button("🗑️ Delete");

        {
            editBtn.setStyle(
                    "-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 10px;");
            toggleBtn.setStyle(
                    "-fx-background-color: #ffc107; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 10px;");
            deleteBtn.setStyle(
                    "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 10px;");

            editBtn.setOnAction(e -> {
                Car car = getTableView().getItems().get(getIndex());
                showEditCarDialog(car);
            });

            toggleBtn.setOnAction(e -> {
                Car car = getTableView().getItems().get(getIndex());
                String newStatus = car.getStatus().equals("Available") ? "Unavailable" : "Available";
                
                if (dbManager.updateCarStatus(car.getName(), newStatus)) {
                    car.setStatus(newStatus);
                    getTableView().refresh();
                    showSuccessDialog("Car status updated to: " + newStatus);
                    refreshAvailableCarsView(); // Real-time update
                } else {
                    showAlert("Error", "Failed to update car status");
                }
            });

            deleteBtn.setOnAction(e -> {
                Car car = getTableView().getItems().get(getIndex());
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Delete Car");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Are you sure you want to delete " + car.getName() + "?");

                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    if (dbManager.deleteCar(car.getName())) {
                        getTableView().getItems().remove(car);
                        showSuccessDialog("Car deleted successfully!");
                        refreshAvailableCarsView(); // Real-time update
                    } else {
                        showAlert("Error", "Failed to delete car");
                    }
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

    HBox headerBox = new HBox();
    headerBox.setAlignment(Pos.CENTER_LEFT);

    Label title = new Label("👑 Admin: Manage Users");
    title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
    title.setStyle("-fx-text-fill: #333;");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    List<User> users = dbManager.getAllUsers();
    Label totalUsersLabel = new Label("Total Users: " + users.size());
    totalUsersLabel.setStyle(
            "-fx-background-color: #e3f2fd; -fx-text-fill: #1976d2; -fx-padding: 8 15; -fx-background-radius: 4; -fx-font-weight: bold;");

    headerBox.getChildren().addAll(title, spacer, totalUsersLabel);

    TableView<User> table = new TableView<>();
    table.setItems(FXCollections.observableArrayList(users));
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
    roleCol.setCellValueFactory(cellData -> 
        new SimpleStringProperty(cellData.getValue().getUsername().equals("ADMIN") ? "Administrator" : "Customer"));
    roleCol.setPrefWidth(120);

    TableColumn<User, Void> actionsCol = new TableColumn<>("Actions");
    actionsCol.setPrefWidth(160);

    actionsCol.setCellFactory(col -> new TableCell<User, Void>() {
        private final HBox buttonBox = new HBox(5);
        private final Button viewBtn = new Button("👁️ View Details");
        private final Button deleteBtn = new Button("🗑️ Delete");

        {
            viewBtn.setStyle(
                    "-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
            deleteBtn.setStyle(
                    "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");

            viewBtn.setOnAction(e -> showUserDetailsDialog(getTableView().getItems().get(getIndex())));

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
                    if (dbManager.deleteUser(user.getUsername())) {
                        showSuccessDialog("User deleted successfully!");
                        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createAdminUsersView());
                    } else {
                        showAlert("Error", "Failed to delete user. Please try again.");
                    }
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
                deleteBtn.setDisable(user.getUsername().equals("ADMIN"));
                deleteBtn.setStyle(user.getUsername().equals("ADMIN") ?
                        "-fx-background-color: #cccccc; -fx-text-fill: #666; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;" :
                        "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 3 8; -fx-background-radius: 3; -fx-font-size: 10px;");
                setGraphic(buttonBox);
            }
        }
    });

    table.getColumns().addAll(nameCol, emailCol, usernameCol, roleCol, actionsCol);

    content.getChildren().addAll(headerBox, table);
    VBox.setVgrow(table, Priority.ALWAYS);

    return content;
}

    private BorderPane createAvailableCarsView() {
        BorderPane content = new BorderPane();
        content.setPadding(new Insets(30));
    
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Available Cars");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #333;");
        headerBox.getChildren().add(title);
        content.setTop(headerBox);
    
        VBox filterPanel = new VBox(15);
        filterPanel.setPadding(new Insets(20));
        filterPanel.setPrefWidth(250);
        filterPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e0e0e0; -fx-border-width: 0 1 0 0;");

        Label filterTitle = new Label("Filter & Search");
        filterTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
    
        carSearchField = new TextField();
        carSearchField.setPromptText("Search by car name...");
    
        seatsFilterCombo = new ComboBox<>();
        seatsFilterCombo.setPromptText("All Seats");
        seatsFilterCombo.getItems().addAll("All Seats", "2 Seats", "4 Seats", "5 Seats", "7 Seats", "8 Seats");
        seatsFilterCombo.setValue("All Seats");
        seatsFilterCombo.setMaxWidth(Double.MAX_VALUE);
    
        transmissionFilterCombo = new ComboBox<>();
        transmissionFilterCombo.setPromptText("All Transmissions");
        transmissionFilterCombo.getItems().addAll("All", "Manual", "Automatic");
        transmissionFilterCombo.setValue("All");
        transmissionFilterCombo.setMaxWidth(Double.MAX_VALUE);
    
        fuelFilterCombo = new ComboBox<>();
        fuelFilterCombo.setPromptText("All Fuel Types");
        fuelFilterCombo.getItems().addAll("All", "Petrol", "Diesel", "Electric", "Hybrid");
        fuelFilterCombo.setValue("All");
        fuelFilterCombo.setMaxWidth(Double.MAX_VALUE);

        maxPriceFilterField = new TextField();
        maxPriceFilterField.setPromptText("Max Price/day");
        
        filterPanel.getChildren().addAll(
            filterTitle,
            new Label("Car Name:"), carSearchField,
            new Label("Seats:"), seatsFilterCombo,
            new Label("Transmission:"), transmissionFilterCombo,
            new Label("Fuel Type:"), fuelFilterCombo,
            new Label("Max Price:"), maxPriceFilterField
        );
        content.setLeft(filterPanel);

        Runnable filterAction = () -> filterAndDisplayCars(
            carSearchField.getText(),
            seatsFilterCombo.getValue(),
            transmissionFilterCombo.getValue(),
            fuelFilterCombo.getValue(),
            maxPriceFilterField.getText()
        );

        carSearchField.textProperty().addListener((obs, old, aNew) -> filterAction.run());
        seatsFilterCombo.valueProperty().addListener((obs, old, aNew) -> filterAction.run());
        transmissionFilterCombo.valueProperty().addListener((obs, old, aNew) -> filterAction.run());
        fuelFilterCombo.valueProperty().addListener((obs, old, aNew) -> filterAction.run());
        maxPriceFilterField.textProperty().addListener((obs, old, aNew) -> filterAction.run());
    
        carsScrollPane = new ScrollPane();
        carsScrollPane.setFitToWidth(true);
        carsScrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        carsScrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        carsScrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        BorderPane.setMargin(carsScrollPane, new Insets(0, 0, 0, 20));
    
        updateCarsGrid(allCars);
        content.setCenter(carsScrollPane);
    
        return content;
    }
    
    private void filterAndDisplayCars(String name, String seats, String transmission, String fuel, String maxPriceStr) {
        List<Car> filteredCars = new ArrayList<>(allCars);
    
        if (name != null && !name.trim().isEmpty()) {
            filteredCars = filteredCars.stream()
                .filter(car -> car.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        }
    
        if (seats != null && !seats.equals("All Seats")) {
            filteredCars = filteredCars.stream()
                .filter(car -> car.getSeats().equals(seats))
                .collect(Collectors.toList());
        }
    
        if (transmission != null && !transmission.equals("All")) {
            filteredCars = filteredCars.stream()
                .filter(car -> car.getTransmission().equals(transmission))
                .collect(Collectors.toList());
        }
    
        if (fuel != null && !fuel.equals("All")) {
            filteredCars = filteredCars.stream()
                .filter(car -> car.getFuelType().equals(fuel))
                .collect(Collectors.toList());
        }

        if (maxPriceStr != null && !maxPriceStr.trim().isEmpty()) {
            try {
                double maxPrice = Double.parseDouble(maxPriceStr);
                filteredCars = filteredCars.stream()
                    .filter(car -> {
                        double carPrice = Double.parseDouble(car.getPrice().replace("₹", "").replace(",", ""));
                        return carPrice <= maxPrice;
                    })
                    .collect(Collectors.toList());
            } catch (NumberFormatException e) {
                // Ignore
            }
        }
    
        updateCarsGrid(filteredCars);
    }
    
    private void updateCarsGrid(List<Car> cars) {
        GridPane carsGrid = new GridPane();
        carsGrid.setHgap(20);
        carsGrid.setVgap(20);
        carsGrid.setPadding(new Insets(20, 10, 20, 10));
    
        int numColumns = 4;
        for (int i = 0; i < numColumns; i++) {
            ColumnConstraints colConstraints = new ColumnConstraints();
            colConstraints.setPercentWidth(100.0 / numColumns);
            colConstraints.setHgrow(Priority.ALWAYS);
            carsGrid.getColumnConstraints().add(colConstraints);
        }
    
        int col = 0, row = 0;
        for (Car car : cars) {
            VBox carCard = createResponsiveCarCard(car);
            carsGrid.add(carCard, col, row);
    
            col++;
            if (col >= numColumns) {
                col = 0;
                row++;
            }
        }
    
        carsScrollPane.setContent(carsGrid);
    }

    private VBox createResponsiveCarCard(Car car) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 8, 0, 0, 2);");

        card.setMaxWidth(Double.MAX_VALUE);
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);

        VBox imageContainer = new VBox();
        imageContainer.setAlignment(Pos.CENTER);
        imageContainer.setPrefHeight(140);
        imageContainer.setMaxWidth(Double.MAX_VALUE);
        imageContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom, #e3f2fd, #bbdefb); -fx-background-radius: 4;");

        try {
            String imagePath = "/images/cars/" + car.getImagePath();
            Image image = new Image(getClass().getResourceAsStream(imagePath));
            if (!image.isError()) {
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(140);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageContainer.getChildren().add(imageView);
            } else {
                throw new Exception("Image not found");
            }
        } catch (Exception e) {
            Label carEmoji = new Label("🚗");
            carEmoji.setStyle("-fx-font-size: 42px;");
            Label placeholderText = new Label(car.getName());
            placeholderText.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
            VBox placeholder = new VBox(10, carEmoji, placeholderText);
            placeholder.setAlignment(Pos.CENTER);
            imageContainer.getChildren().add(placeholder);
        }

        Label nameLabel = new Label(car.getName());
        nameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-text-fill: #333;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(Double.MAX_VALUE);

        Label yearLabel = new Label("2022");
        yearLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 13px;");

        VBox specsBox = new VBox(5);
        specsBox.setAlignment(Pos.CENTER_LEFT);
        Label seatsLabel = new Label("👥 " + car.getSeats());
        Label transmissionLabel = new Label("⚙️ " + car.getTransmission());
        Label fuelLabel = new Label("⛽ " + car.getFuelType());
        seatsLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        transmissionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        fuelLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #666;");
        specsBox.getChildren().addAll(seatsLabel, transmissionLabel, fuelLabel);

        VBox statusInfo = new VBox(5);
        statusInfo.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label(car.getStatus());
        if (car.getStatus().equals("Available")) {
            statusLabel.setStyle(
                    "-fx-background-color: #e8f5e8; -fx-text-fill: #2e7d32; -fx-padding: 5 10; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        } else if (car.getStatus().equals("Booked")) {
            statusLabel.setStyle(
                    "-fx-background-color: #fff3e0; -fx-text-fill: #f57c00; -fx-padding: 5 10; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
            String returnInfo = getCarReturnInfo(car.getName());
            if (!returnInfo.isEmpty()) {
                Label returnLabel = new Label("📅 " + returnInfo);
                returnLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 11px; -fx-font-style: italic;");
                statusInfo.getChildren().add(returnLabel);
            }
        } else {
            statusLabel.setStyle(
                    "-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-padding: 5 10; -fx-background-radius: 12; -fx-font-size: 12px; -fx-font-weight: bold;");
        }
        statusInfo.getChildren().add(0, statusLabel);

        VBox bottomSection = new VBox(10);
        bottomSection.setAlignment(Pos.CENTER);
        VBox priceBox = new VBox(3);
        priceBox.setAlignment(Pos.CENTER);
        Label priceLabel = new Label(car.getPrice());
        priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        priceLabel.setStyle("-fx-text-fill: #4285f4;");
        Label perDayLabel = new Label("per day");
        perDayLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
        priceBox.getChildren().addAll(priceLabel, perDayLabel);

        Button actionButton;
        if (car.getStatus().equals("Available")) {
            actionButton = new Button("Reserve Now");
            actionButton.setStyle(
                    "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 0; -fx-background-radius: 4; -fx-font-weight: bold; -fx-font-size: 13px;");
            actionButton.setMaxWidth(Double.MAX_VALUE);
            actionButton.setOnAction(e -> showReservationDialog(car));
        } else {
            actionButton = new Button("Unavailable");
            actionButton.setStyle(
                    "-fx-background-color: #f5f5f5; -fx-text-fill: #999; -fx-padding: 10 0; -fx-background-radius: 4; -fx-font-size: 13px;");
            actionButton.setMaxWidth(Double.MAX_VALUE);
            actionButton.setDisable(true);
        }
        bottomSection.getChildren().addAll(priceBox, actionButton);

        card.getChildren().addAll(imageContainer, nameLabel, yearLabel, specsBox, statusInfo, bottomSection);

        return card;
    }

    private void showReservationDialog(Car car) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Reserve Car");

        VBox backdrop = new VBox(new VBox());
        backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
        backdrop.setAlignment(Pos.CENTER);

        VBox dialogContent = new VBox(20);
        dialogContent.setPadding(new Insets(30));
        dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
        dialogContent.setMaxWidth(500);

        HBox carInfo = new HBox(20);
        carInfo.setAlignment(Pos.CENTER_LEFT);
        Rectangle carImage = new Rectangle(100, 60, Color.LIGHTBLUE);
        VBox carDetails = new VBox(5);
        Label carName = new Label(car.getName() + " (2022)");
        carName.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        Label carSpecs = new Label(car.getSeats() + " • " + car.getTransmission() + " • " + car.getFuelType());
        carSpecs.setStyle("-fx-text-fill: #666;");
        Label carPrice = new Label(car.getPrice() + " per day");
        carPrice.setStyle("-fx-text-fill: #4285f4; -fx-font-weight: bold;");
        carDetails.getChildren().addAll(carName, carSpecs, carPrice);
        carInfo.getChildren().addAll(carImage, carDetails);

        GridPane dateGrid = new GridPane();
        dateGrid.setHgap(20);
        dateGrid.setVgap(10);
        DatePicker startDatePicker = new DatePicker(LocalDate.now());
        startDatePicker.setStyle("-fx-pref-width: 200;");
        DatePicker endDatePicker = new DatePicker(LocalDate.now().plusDays(1));
        endDatePicker.setStyle("-fx-pref-width: 200;");
        dateGrid.add(new Label("Start Date"), 0, 0);
        dateGrid.add(startDatePicker, 0, 1);
        dateGrid.add(new Label("End Date"), 1, 0);
        dateGrid.add(endDatePicker, 1, 1);

        VBox costBox = new VBox(10);
        Label daysLabel = new Label("Days: 0");
        Label dailyRateLabel = new Label("Daily Rate: " + car.getPrice());
        Label totalCostLabel = new Label("Total Cost: ₹0.00");
        totalCostLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        costBox.getChildren().addAll(daysLabel, dailyRateLabel, totalCostLabel);

        Runnable updateCost = () -> {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            if (start != null && end != null && !end.isBefore(start)) {
                long days = Math.max(1, ChronoUnit.DAYS.between(start, end));
                double dailyRate = Double.parseDouble(car.getPrice().replace("₹", "").replace(",", ""));
                double total = days * dailyRate;
                daysLabel.setText("Days: " + days);
                totalCostLabel.setText("Total Cost: ₹" + String.format("%.2f", total));
            }
        };

        startDatePicker.setOnAction(e -> updateCost.run());
        endDatePicker.setOnAction(e -> updateCost.run());
        updateCost.run();

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle(
                "-fx-background-color: #f5f5f5; -fx-text-fill: #333; -fx-padding: 10 20; -fx-background-radius: 4;");
        cancelBtn.setOnAction(e -> dialog.close());
        Button confirmBtn = new Button("✓ Proceed to Payment");
        confirmBtn.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        confirmBtn.setOnAction(e -> {
            LocalDate start = startDatePicker.getValue();
            LocalDate end = endDatePicker.getValue();
            if (start != null && end != null && !end.isBefore(start)) {
                long days = Math.max(1, ChronoUnit.DAYS.between(start, end));
                double dailyRate = Double.parseDouble(car.getPrice().replace("₹", "").replace(",", ""));
                String totalCost = "₹" + String.format("%.2f", days * dailyRate);
                dialog.close();
                showBillingAndPaymentDialog(car, start, end, totalCost);
            }
        });
        buttonBox.getChildren().addAll(cancelBtn, confirmBtn);

        dialogContent.getChildren().addAll(carInfo, dateGrid, costBox, buttonBox);
        backdrop.getChildren().add(dialogContent);

        dialog.setScene(new Scene(backdrop, 800, 600));
        dialog.show();
    }
    
    private void showBillingAndPaymentDialog(Car car, LocalDate startDate, LocalDate endDate, String totalCost) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Billing and Payment");
    
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #f5f5f5;");
    
        VBox summaryPane = new VBox(20);
        summaryPane.setPadding(new Insets(40));
        summaryPane.setStyle("-fx-background-color: white;");
        summaryPane.setPrefWidth(350);
        Label summaryTitle = new Label("Reservation Summary");
        summaryTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        VBox carDetails = new VBox(10);
        Label carNameLabel = new Label(car.getName());
        carNameLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        Label carSpecsLabel = new Label(car.getSeats() + " | " + car.getTransmission() + " | " + car.getFuelType());
        carSpecsLabel.setStyle("-fx-text-fill: #666;");
        carDetails.getChildren().addAll(carNameLabel, carSpecsLabel);
        VBox rentalDetails = new VBox(10);
        rentalDetails.setPadding(new Insets(15, 0, 15, 0));
        Label periodLabel = new Label("Rental Period");
        periodLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label datesLabel = new Label(startDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")) + " — " + endDate.format(DateTimeFormatter.ofPattern("MMM d, yyyy")));
        rentalDetails.getChildren().addAll(periodLabel, datesLabel);
        VBox totalDetails = new VBox(5);
        Label totalLabel = new Label("Total Amount");
        totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        Label totalAmountLabel = new Label(totalCost);
        totalAmountLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        totalAmountLabel.setStyle("-fx-text-fill: #4285f4;");
        totalDetails.getChildren().addAll(totalLabel, totalAmountLabel);
        summaryPane.getChildren().addAll(summaryTitle, carDetails, new Separator(), rentalDetails, new Separator(), totalDetails);
        root.setLeft(summaryPane);
    
        VBox paymentPane = new VBox(25);
        paymentPane.setPadding(new Insets(40));
        paymentPane.setAlignment(Pos.CENTER_LEFT);
        Label paymentTitle = new Label("Select Payment Method");
        paymentTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        ToggleGroup paymentGroup = new ToggleGroup();
        RadioButton upiRadio = createPaymentRadioButton("UPI / Google Pay / PhonePe", "/images/icons/upi.png", paymentGroup);
        RadioButton qrRadio = createPaymentRadioButton("Scan QR Code", "/images/icons/qr.png", paymentGroup);
        RadioButton cashOnDeliveryRadio = createPaymentRadioButton("Cash on Delivery", "/images/icons/cod.png", paymentGroup);
        RadioButton cashOnReturnRadio = createPaymentRadioButton("Cash on Return", "/images/icons/cash.png", paymentGroup);
        upiRadio.setSelected(true);
        Button confirmAndPayBtn = new Button("✓ Confirm and Pay");
        confirmAndPayBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 15 30; -fx-background-radius: 8; -fx-font-weight: bold; -fx-font-size: 16px;");
        confirmAndPayBtn.setMaxWidth(Double.MAX_VALUE);
        paymentPane.getChildren().addAll(paymentTitle, upiRadio, qrRadio, cashOnDeliveryRadio, cashOnReturnRadio, confirmAndPayBtn);
        root.setCenter(paymentPane);
    
        confirmAndPayBtn.setOnAction(e -> {
            RadioButton selected = (RadioButton) paymentGroup.getSelectedToggle();
            if (selected == null) {
                showAlert("Payment Error", "Please select a payment method.");
                return;
            }
    
            String invoiceId = "INV-" + LocalDate.now().getYear() + "-" + String.format("%03d", dbManager.getAllInvoices().size() + 1);
            Invoice invoice = new Invoice(invoiceId, car.getName(),
                startDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")) + " - " + endDate.format(DateTimeFormatter.ofPattern("MMMM d, yyyy")),
                totalCost, LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM d, yyyy")), currentUser.getName(), selected.getText());
            Reservation reservation = new Reservation(car.getName(), startDate, endDate, totalCost, "Upcoming", currentUser.getName());
    
            if (dbManager.insertReservation(reservation) && dbManager.insertInvoice(invoice)) {
                dbManager.updateCarStatus(car.getName(), "Booked");
                dialog.close();
                showPaymentSuccessDialog();
                refreshAvailableCarsView(); // Real-time update
            } else {
                showAlert("Error", "Failed to create reservation. Please try again.");
            }
        });
    
        dialog.setScene(new Scene(root, 800, 500));
        dialog.show();
    }

    private void showPaymentSuccessDialog() {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(primaryStage);
        dialog.setTitle("Payment Successful");

        VBox content = new VBox(20);
        content.setPadding(new Insets(40));
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-background-color: white;");

        Label iconLabel = new Label("✅");
        iconLabel.setFont(Font.font(48));

        Label title = new Label("Reservation Confirmed!");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 24));

        Label message = new Label("Thank you for your booking. Your reservation has been successfully recorded.");
        message.setWrapText(true);
        message.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button downloadBillBtn = new Button("📄 Download Bill");
        downloadBillBtn.setStyle(
                "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 4; -fx-font-weight: bold;");
        downloadBillBtn.setOnAction(e -> showAlert("Download Bill", "This is a placeholder for bill download functionality."));

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> dialog.close());

        HBox buttonBox = new HBox(15, downloadBillBtn, closeBtn);
        buttonBox.setAlignment(Pos.CENTER);

        content.getChildren().addAll(iconLabel, title, message, buttonBox);

        Scene scene = new Scene(content, 600, 300);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
    
    private RadioButton createPaymentRadioButton(String text, String iconPath, ToggleGroup group) {
        ImageView icon = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream(iconPath));
            icon.setImage(image);
            icon.setFitWidth(24);
            icon.setFitHeight(24);
        } catch (Exception e) {
            System.err.println("Could not load icon: " + iconPath);
        }
    
        RadioButton radioButton = new RadioButton(text);
        radioButton.setToggleGroup(group);
        radioButton.setGraphic(icon);
        radioButton.setGraphicTextGap(10);
        radioButton.setFont(Font.font("Arial", 14));
        return radioButton;
    }

    private VBox createReservationsView() {
    VBox content = new VBox(20);
    content.setPadding(new Insets(30));

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
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear All Reservations");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to clear all reservations?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            List<Reservation> userReservations = isAdmin() ? 
                dbManager.getAllReservations() : 
                dbManager.getUserReservations(currentUser.getName());
                
            for (Reservation reservation : userReservations) {
                dbManager.updateCarStatus(reservation.getCarName(), "Available");
                dbManager.deleteReservation(reservation.getCarName(), reservation.getCustomerName());
            }

            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createReservationsView());
            showSuccessDialog("All reservations cleared! Cars are now available for booking.");
            refreshAvailableCarsView();
        }
    });

    headerBox.getChildren().addAll(title, spacer, clearAllBtn);

    List<Reservation> reservationsList = isAdmin() ? 
        dbManager.getAllReservations() : 
        dbManager.getUserReservations(currentUser.getName());
    
    TableView<Reservation> table = new TableView<>();
    table.setItems(FXCollections.observableArrayList(reservationsList));
    table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
    
    if (reservationsList.isEmpty()) {
        Label noDataLabel = new Label("No reservations found. Book a car to see your reservations here!");
        noDataLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-padding: 50px;");
        table.setPlaceholder(noDataLabel);
    }

    TableColumn<Reservation, String> carCol = new TableColumn<>("Car");
    carCol.setCellValueFactory(new PropertyValueFactory<>("carName"));
    carCol.setPrefWidth(200);

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
    actionsCol.setPrefWidth(200);

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
                Reservation r = getTableView().getItems().get(getIndex());
                Invoice i = dbManager.getInvoiceByReservation(r.getCarName(), r.getCustomerName());
                if (i != null) showInvoiceDetailsDialog(i, r); else showAlert("Error", "Invoice not found.");
            });

            cancelBtn.setOnAction(e -> {
                Reservation reservation = getTableView().getItems().get(getIndex());
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Cancel Reservation");
                confirmAlert.setHeaderText(null);
                confirmAlert.setContentText("Are you sure you want to cancel the reservation for " + reservation.getCarName() + "?");

                if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                    if (dbManager.updateCarStatus(reservation.getCarName(), "Available") &&
                            dbManager.deleteReservation(reservation.getCarName(), reservation.getCustomerName())) {
                        showSuccessDialog("Reservation cancelled successfully!");
                        ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createReservationsView());
                        refreshAvailableCarsView(); // Real-time update
                    } else {
                        showAlert("Error", "Failed to cancel reservation.");
                    }
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
    VBox.setVgrow(table, Priority.ALWAYS);

    return content;
}

    private VBox createInvoicesView() {
    VBox content = new VBox(20);
    content.setPadding(new Insets(30));

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
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Clear All Invoices");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to clear all invoices?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            List<Invoice> invoicesToDelete = isAdmin() ? 
                dbManager.getAllInvoices() : 
                dbManager.getUserInvoices(currentUser.getName());
            for (Invoice invoice : invoicesToDelete) {
                dbManager.deleteInvoice(invoice.getInvoiceId());
            }
            ((BorderPane) primaryStage.getScene().getRoot()).setCenter(createInvoicesView());
            showSuccessDialog("Invoices cleared successfully!");
        }
    });

    headerBox.getChildren().addAll(title, spacer, clearAllBtn);

    List<Invoice> invoicesList = isAdmin() ? 
        dbManager.getAllInvoices() : 
        dbManager.getUserInvoices(currentUser.getName());
    
    TableView<Invoice> table = new TableView<>();
    table.setItems(FXCollections.observableArrayList(invoicesList));
    table.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0;");
    
    if (invoicesList.isEmpty()) {
        Label noDataLabel = new Label("No invoices found. Your invoices will appear here after making reservations.");
        noDataLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 14px; -fx-padding: 50px;");
        table.setPlaceholder(noDataLabel);
    }

    TableColumn<Invoice, String> invoiceCol = new TableColumn<>("Invoice #");
    invoiceCol.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
    invoiceCol.setPrefWidth(150);

    TableColumn<Invoice, String> carCol = new TableColumn<>("Car");
    carCol.setCellValueFactory(new PropertyValueFactory<>("carName"));
    carCol.setPrefWidth(200);

    TableColumn<Invoice, String> periodCol = new TableColumn<>("Rental Period");
    periodCol.setCellValueFactory(new PropertyValueFactory<>("rentalPeriod"));
    periodCol.setPrefWidth(300);

    TableColumn<Invoice, String> totalCol = new TableColumn<>("Total");
    totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));
    totalCol.setPrefWidth(120);

    TableColumn<Invoice, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
    dateCol.setPrefWidth(150);

    TableColumn<Invoice, Void> actionsCol = new TableColumn<>("Actions");
    actionsCol.setPrefWidth(250);
    
    actionsCol.setCellFactory(col -> new TableCell<Invoice, Void>() {
        private final Button viewBtn = new Button("👁️ View Details");
        private final Button downloadBtn = new Button("📄 Download Bill");
        private final HBox buttonBox = new HBox(5, viewBtn, downloadBtn);
        
        {
            viewBtn.setStyle(
                    "-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");
            downloadBtn.setStyle(
                    "-fx-background-color: #4285f4; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 11px;");
            
            viewBtn.setOnAction(e -> showInvoiceDetailsDialog(getTableView().getItems().get(getIndex()), null));
            downloadBtn.setOnAction(e -> showAlert("Download Bill", "This is a placeholder for bill download functionality."));
        }
        
        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            setGraphic(empty ? null : buttonBox);
        }
    });

    table.getColumns().addAll(invoiceCol, carCol, periodCol, totalCol, dateCol, actionsCol);
    content.getChildren().addAll(headerBox, table);
    VBox.setVgrow(table, Priority.ALWAYS);

    return content;
}

    private void showInvoiceDetailsDialog(Invoice invoice, Reservation reservation) {
    Stage dialog = new Stage();
    dialog.initModality(Modality.APPLICATION_MODAL);
    dialog.initOwner(primaryStage);
    dialog.setTitle("Invoice Details");

    VBox backdrop = new VBox();
    backdrop.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
    backdrop.setAlignment(Pos.CENTER);

    VBox dialogContent = new VBox(20);
    dialogContent.setPadding(new Insets(30));
    dialogContent.setStyle("-fx-background-color: white; -fx-background-radius: 8;");
    dialogContent.setMaxWidth(500);

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
    Label invoiceIdLabel = new Label(invoice.getInvoiceId());
    Label issuedLabel = new Label("Issued: " + invoice.getDate());
    issuedLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 12px;");
    invoiceInfo.getChildren().addAll(invoiceTitle, invoiceIdLabel, issuedLabel);
    header.getChildren().addAll(companyInfo, spacer, invoiceInfo);

    VBox billedTo = new VBox(5);
    Label billedToLabel = new Label("Billed To:");
    billedToLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
    String customerName = invoice.getCustomerName() != null ? invoice.getCustomerName() : (currentUser != null ? currentUser.getName() : "Customer");
    Label customerNameLabel = new Label(customerName);
    String customerEmail = "";
    for (User user : dbManager.getAllUsers()) {
        if (user.getName().equals(customerName)) {
            customerEmail = user.getEmail();
            break;
        }
    }
    Label customerEmailLabel = new Label(customerEmail);
    customerEmailLabel.setStyle("-fx-text-fill: #666;");
    billedTo.getChildren().addAll(billedToLabel, customerNameLabel, customerEmailLabel);

    VBox details = new VBox(15);
    details.getChildren().add(new Label("Description") {{ setFont(Font.font("Arial", FontWeight.BOLD, 14)); }});
    HBox itemRow = new HBox();
    itemRow.setAlignment(Pos.CENTER_LEFT);
    Label itemDesc = new Label(invoice.getCarName() + " - Rental (" + invoice.getRentalPeriod() + ")");
    itemDesc.setPrefWidth(300);
    Region itemSpacer = new Region();
    HBox.setHgrow(itemSpacer, Priority.ALWAYS);
    itemRow.getChildren().addAll(itemDesc, itemSpacer, new Label("Amount") {{ setFont(Font.font("Arial", FontWeight.BOLD, 14)); }});
    HBox amountRow = new HBox();
    amountRow.setAlignment(Pos.CENTER_LEFT);
    Label amount = new Label(invoice.getCarName());
    amount.setPrefWidth(300);
    Region amountSpacer = new Region();
    HBox.setHgrow(amountSpacer, Priority.ALWAYS);
    amountRow.getChildren().addAll(amount, amountSpacer, new Label(invoice.getTotal()) {{ setFont(Font.font("Arial", FontWeight.BOLD, 14)); }});
    HBox totalRow = new HBox();
    totalRow.setAlignment(Pos.CENTER_LEFT);
    Label totalLabel = new Label("Total");
    totalLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    totalLabel.setPrefWidth(300);
    Region totalSpacer = new Region();
    HBox.setHgrow(totalSpacer, Priority.ALWAYS);
    Label totalAmount = new Label(invoice.getTotal());
    totalAmount.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    totalAmount.setStyle("-fx-text-fill: #4285f4;");
    totalRow.getChildren().addAll(totalLabel, totalSpacer, totalAmount);
    details.getChildren().addAll(itemRow, amountRow, new Separator(), totalRow);

    Label thankYou = new Label("Thank you for choosing RentWheels!");
    thankYou.setStyle("-fx-text-fill: #666; -fx-font-style: italic;");
    thankYou.setAlignment(Pos.CENTER);

    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER);
    Button closeBtn = new Button("Close");
    closeBtn.setStyle("-fx-background-color: #f5f5f5; -fx-text-fill: #333; -fx-padding: 10 20; -fx-background-radius: 4;");
    closeBtn.setOnAction(e -> dialog.close());
    buttonBox.getChildren().add(closeBtn);

    dialogContent.getChildren().addAll(header, billedTo, details, thankYou, buttonBox);
    backdrop.getChildren().add(dialogContent);
    dialog.setScene(new Scene(backdrop, 800, 600));
    dialog.show();
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

    private void comprehensiveDebug() {
    System.out.println("\n========== COMPREHENSIVE DATABASE DEBUG ==========");
    List<Car> cars = dbManager.getAllCars();
    System.out.println("Total Cars: " + cars.size());
    cars.stream().limit(3).forEach(car -> 
        System.out.println("  Car: " + car.getName() + " | Price: " + car.getPrice() + " | Status: " + car.getStatus()));
    
    List<User> users = dbManager.getAllUsers();
    System.out.println("\nTotal Users: " + users.size());
    users.forEach(user -> 
        System.out.println("  User: " + user.getName() + " | Username: " + user.getUsername()));
    
    List<Reservation> reservations = dbManager.getAllReservations();
    System.out.println("\nTotal Reservations: " + reservations.size());
    reservations.forEach(res -> 
        System.out.println("  Reservation: " + res.getCarName() + " | Customer: " + res.getCustomerName()));
    
    List<Invoice> invoices = dbManager.getAllInvoices();
    System.out.println("\nTotal Invoices: " + invoices.size());
    invoices.forEach(inv -> 
        System.out.println("  Invoice: " + inv.getInvoiceId() + " | Customer: " + inv.getCustomerName()));
    System.out.println("==================================================\n");
}

    private void refreshAvailableCarsView() {
        Platform.runLater(() -> {
            allCars = dbManager.getAllCars();
            if (carSearchField != null) { // Ensure controls are initialized
                filterAndDisplayCars(
                    carSearchField.getText(),
                    seatsFilterCombo.getValue(),
                    transmissionFilterCombo.getValue(),
                    fuelFilterCombo.getValue(),
                    maxPriceFilterField.getText()
                );
            }
        });
    }
}


