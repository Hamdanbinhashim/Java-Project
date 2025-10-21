public class Car {
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    
    public String getSeats() { return seats; }
    public void setSeats(String seats) { this.seats = seats; }
    
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getImagePath() { return imagePath; }
}