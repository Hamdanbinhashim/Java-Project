import javafx.animation.Timeline;
import javafx.util.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;





public class Reservation {
    private String carName;
    private String startDate;
    private String endDate;
    private String totalCost;
    private String status;
    private String customerName;
    private LocalDate actualStartDate;
    private LocalDate actualEndDate;

    // NEW CONSTRUCTOR WITH LocalDate PARAMETERS:
    public Reservation(String carName, LocalDate startDate, LocalDate endDate, String totalCost, String status,
            String customerName) {
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

    // Setters

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