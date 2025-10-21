import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DB_NAME = "rentwheels.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_NAME;
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            createTables();
            insertDefaultData();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
    
    // Users table
    String createUsersTable = """
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT NOT NULL UNIQUE,
                    username TEXT NOT NULL UNIQUE,
                    password TEXT NOT NULL,
                    created_date DATE DEFAULT CURRENT_DATE
                )
            """;

    // Cars table
    String createCarsTable = """
                CREATE TABLE IF NOT EXISTS cars (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    price TEXT NOT NULL,
                    seats TEXT NOT NULL,
                    transmission TEXT NOT NULL,
                    fuel_type TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'Available',
                    image_path TEXT,
                    created_date DATE DEFAULT CURRENT_DATE
                )
            """;

    // Reservations table
    String createReservationsTable = """
                CREATE TABLE IF NOT EXISTS reservations (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    car_name TEXT NOT NULL,
                    customer_name TEXT NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    total_cost TEXT NOT NULL,
                    status TEXT NOT NULL DEFAULT 'Upcoming',
                    created_date DATE DEFAULT CURRENT_DATE
                )
            """;

    // Invoices table with customer_name
    String createInvoicesTable = """
                CREATE TABLE IF NOT EXISTS invoices (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    invoice_id TEXT NOT NULL UNIQUE,
                    car_name TEXT NOT NULL,
                    rental_period TEXT NOT NULL,
                    total TEXT NOT NULL,
                    issue_date TEXT NOT NULL,
                    customer_name TEXT NOT NULL,
                    payment_method TEXT,
                    created_date DATE DEFAULT CURRENT_DATE
                )
            """;

    try (Statement stmt = connection.createStatement()) {
        stmt.execute(createUsersTable);
        stmt.execute(createCarsTable);
        stmt.execute(createReservationsTable);
        stmt.execute(createInvoicesTable);
        
        System.out.println("✓ All database tables created successfully");
    }
}

    private void insertDefaultData() throws SQLException {
    // Check if admin user exists
    if (!userExists("ADMIN")) {
        System.out.println("Creating ADMIN user...");
        insertUser(new User("Admin User", "admin@rentwheels.com", "ADMIN", "password"));
        System.out.println("✓ ADMIN user created");
    } else {
        System.out.println("✓ ADMIN user already exists");
    }

    // Check if cars exist
    if (getAllCars().isEmpty()) {
        System.out.println("Inserting default cars...");
        insertDefaultCars();
        System.out.println("✓ Default cars inserted");
    } else {
        System.out.println("✓ Cars already exist: " + getAllCars().size() + " cars");
    }
}

    private void insertDefaultCars() throws SQLException {
        List<Car> defaultCars = List.of(
                new Car("Tesla Model S", "₹10020.00", "5 Seats", "Automatic", "Electric", "Available","tesla_model_s.jpg"),
                new Car("BMW X5", "₹7932.00", "5 Seats", "Automatic", "Hybrid", "Available", "bmw_x5.jpg"),
                new Car("Mercedes-Benz E-Class", "₹9185.00", "5 Seats", "Automatic", "Petrol", "Available","mercedes_e_class.jpg"),
                new Car("Audi A4", "₹7097.00", "5 Seats", "Automatic", "Diesel", "Available", "audi_a4.jpg"),
                new Car("Toyota Camry", "₹6845.00", "5 Seats", "Automatic", "Hybrid", "Available", "toyota_camry.jpg"),
                new Car("Ford Mustang", "₹8350.00", "4 Seats", "Manual", "Petrol", "Available", "ford_mustang.jpg"),
                new Car("BMW M340i", "₹9350.00", "5 Seats", "Automatic", "Petrol", "Available","bmw_m340i.jpg"),
                new Car("Lamborghini Urus", "₹25200.00", "5 Seats", "Automatic", "Petrol", "Available", "lamborghini_urus.jpg"),
                new Car("Porsche 911 Turbo S (992)", "₹20580.00", "2 Seats", "Automatic", "Petrol", "Available","porsche_911_turbo_s.jpg"),
                new Car("Audi Q8", "₹8200.00", "5 Seats", "Automatic", "Petrol", "Available", "audi_q8.jpg"),
                new Car("BYD Seal", "₹6845.00", "5 Seats", "Automatic", "Electric", "Available", "byd_seal.jpg"),
                new Car("Ferrari 488 Pista", "₹24300.00", "2 Seats", "Automatic", "Petrol", "Available", "ferrari_488_pista.jpg"),
                new Car("Porsche Taycan", "₹10020.00", "4 Seats", "Automatic", "Electric", "Available","porsche_tycan.jpg"),
                new Car("Lexus ES 300h", "₹7999.00", "4 Seats", "Automatic", "Hybrid", "Available", "lexus_es_300h.jpg"),
                new Car("Range Rover Velar", "₹8800.00", "5 Seats", "Automatic", "Petrol", "Available","range_rover_velar.jpg"),
                new Car("Toyota Land Cruiser", "₹10133.00", "5 Seats", "Automatic", "Diesel", "Available", "toyota_land_cruiser.jpg"),
                new Car("Range Rover SVR", "₹7990.00", "5 Seats", "Automatic", "Petrol", "Available", "range_rover_svr.jpg"),
                new Car(" BMW M2 ", "₹12350.00", "4 Seats", "Manual", "Petrol", "Available", "bmw_m2.jpg"),
                new Car("Audi RS7", "₹13500.00", "5 Seats", "Automatic", "Petrol", "Available","audi_rs7.jpg"),
                new Car("Jaguar f-pace", "₹7800.00", "5 Seats", "Automatic", "Petrol", "Available", "jaguar_f-pace.jpg"),
                new Car("Lamborghini Huracan Sterrato", "₹24340.00", "2 Seats", "Automatic", "Petrol", "Available","lamborghini_huracan.jpg"),
                new Car("Toyota Vellfire ", "₹8900.00", "7 Seats", "Automatic", "Petrol", "Available", "toyota_vellfire.jpg"),
                new Car(" Maserati Quattroporte", "₹6788.00", "4 Seats", "Automatic", "Petrol", "Available", "maserati_quattroporte.jpg"),
                new Car("BMW M4", "₹28352.00", "2 Seats", "Automatic", "Petrol", "Available", "bmw_m4.jpg"),
                new Car(" Mercedes G Wagon ", "₹23456.00", "5 Seats", "Automatic", "Petrol", "Available", "mercedes_g_wagon.jpg"),
                new Car("Mini Cooper S", "₹8998.00", "4 Seats", "Automatic", "Petrol", "Available","mii_cooper_s.jpg"),
                new Car("Jeep Wrangler", "₹8500.00", "5 Seats", "Automatic", "Petrol", "Available", "jeep_wrangler.jpg"));

        for (Car car : defaultCars) {
            insertCar(car);
        }
    }

    // User operations
    public boolean insertUser(User user) {
        String sql = "INSERT INTO users (name, email, username, password) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getUsername());
            pstmt.setString(4, user.getPassword());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean userExists(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_date DESC";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                users.add(new User(
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("username"),
                        rs.getString("password")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public boolean deleteUser(String username) {
        if (username.equals("ADMIN"))
            return false; // Protect admin account

        String sql = "DELETE FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Car operations
    public boolean insertCar(Car car) {
        String sql = "INSERT INTO cars (name, price, seats, transmission, fuel_type, status, image_path) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, car.getName());
            pstmt.setString(2, car.getPrice());
            pstmt.setString(3, car.getSeats());
            pstmt.setString(4, car.getTransmission());
            pstmt.setString(5, car.getFuelType());
            pstmt.setString(6, car.getStatus());
            pstmt.setString(7, car.getImagePath());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        String sql = "SELECT * FROM cars ORDER BY created_date DESC";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                cars.add(new Car(
                        rs.getString("name"),
                        rs.getString("price"),
                        rs.getString("seats"),
                        rs.getString("transmission"),
                        rs.getString("fuel_type"),
                        rs.getString("status"),
                        rs.getString("image_path")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cars;
    }

    public boolean updateCar(Car car) {
        String sql = "UPDATE cars SET price = ?, seats = ?, transmission = ?, fuel_type = ?, status = ?, image_path = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, car.getPrice());
            pstmt.setString(2, car.getSeats());
            pstmt.setString(3, car.getTransmission());
            pstmt.setString(4, car.getFuelType());
            pstmt.setString(5, car.getStatus());
            pstmt.setString(6, car.getImagePath());
            pstmt.setString(7, car.getName());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCar(String carName) {
        String sql = "DELETE FROM cars WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateCarStatus(String carName, String status) {
        String sql = "UPDATE cars SET status = ? WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, carName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Reservation operations
    public boolean insertReservation(Reservation reservation) {
        String sql = "INSERT INTO reservations (car_name, customer_name, start_date, end_date, total_cost, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, reservation.getCarName());
            pstmt.setString(2, reservation.getCustomerName());
            pstmt.setDate(3, Date.valueOf(reservation.getActualStartDate()));
            pstmt.setDate(4, Date.valueOf(reservation.getActualEndDate()));
            pstmt.setString(5, reservation.getTotalCost());
            pstmt.setString(6, reservation.getStatus());
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations ORDER BY created_date DESC";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                reservations.add(new Reservation(
                        rs.getString("car_name"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getString("total_cost"),
                        rs.getString("status"),
                        rs.getString("customer_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public List<Reservation> getUserReservations(String customerName) {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservations WHERE customer_name = ? ORDER BY created_date DESC";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customerName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                reservations.add(new Reservation(
                        rs.getString("car_name"),
                        rs.getDate("start_date").toLocalDate(),
                        rs.getDate("end_date").toLocalDate(),
                        rs.getString("total_cost"),
                        rs.getString("status"),
                        rs.getString("customer_name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservations;
    }

    public boolean deleteReservation(String carName, String customerName) {
        String sql = "DELETE FROM reservations WHERE car_name = ? AND customer_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, carName);
            pstmt.setString(2, customerName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateReservationStatus(String carName, String customerName, String status) {
        String sql = "UPDATE reservations SET status = ? WHERE car_name = ? AND customer_name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setString(2, carName);
            pstmt.setString(3, customerName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Invoice operations
    public boolean insertInvoice(Invoice invoice) {
    String sql = "INSERT INTO invoices (invoice_id, car_name, rental_period, total, issue_date, customer_name, payment_method) VALUES (?, ?, ?, ?, ?, ?, ?)";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, invoice.getInvoiceId());
        pstmt.setString(2, invoice.getCarName());
        pstmt.setString(3, invoice.getRentalPeriod());
        pstmt.setString(4, invoice.getTotal());
        pstmt.setString(5, invoice.getDate());
        pstmt.setString(6, invoice.getCustomerName());
        pstmt.setString(7, invoice.getPaymentMethod());
        pstmt.executeUpdate();
        return true;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String sql = "SELECT * FROM invoices ORDER BY created_date DESC";
        try (Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                invoices.add(new Invoice(
                        rs.getString("invoice_id"),
                        rs.getString("car_name"),
                        rs.getString("rental_period"),
                        rs.getString("total"),
                        rs.getString("issue_date"),
                        rs.getString("customer_name"),
                        rs.getString("payment_method")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return invoices;
    }

    public List<Invoice> getUserInvoices(String customerName) {
    List<Invoice> invoices = new ArrayList<>();
    String sql = "SELECT * FROM invoices WHERE customer_name = ? ORDER BY created_date DESC";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, customerName);
        ResultSet rs = pstmt.executeQuery();

        while (rs.next()) {
            invoices.add(new Invoice(
                    rs.getString("invoice_id"),
                    rs.getString("car_name"),
                    rs.getString("rental_period"),
                    rs.getString("total"),
                    rs.getString("issue_date"),
                    rs.getString("customer_name"),
                    rs.getString("payment_method")));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return invoices;
}

public Invoice getInvoiceByReservation(String carName, String customerName) {
    String sql = "SELECT * FROM invoices WHERE car_name = ? AND customer_name = ? ORDER BY created_date DESC LIMIT 1";
    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
        pstmt.setString(1, carName);
        pstmt.setString(2, customerName);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return new Invoice(
                    rs.getString("invoice_id"),
                    rs.getString("car_name"),
                    rs.getString("rental_period"),
                    rs.getString("total"),
                    rs.getString("issue_date"),
                    rs.getString("customer_name"),
                    rs.getString("payment_method"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    public boolean deleteInvoice(String invoiceId) {
        String sql = "DELETE FROM invoices WHERE invoice_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, invoiceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Utility methods
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}

