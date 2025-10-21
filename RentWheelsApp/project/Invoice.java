public class Invoice {
    private String invoiceId;
    private String carName;
    private String rentalPeriod;
    private String total;
    private String date;
    private String customerName;
    private String paymentMethod;

    public Invoice(String invoiceId, String carName, String rentalPeriod, String total, String date, String customerName, String paymentMethod) {
        this.invoiceId = invoiceId;
        this.carName = carName;
        this.rentalPeriod = rentalPeriod;
        this.total = total;
        this.date = date;
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
    }

    public Invoice(String invoiceId, String carName, String rentalPeriod, String total, String date, String customerName) {
        this(invoiceId, carName, rentalPeriod, total, date, customerName, "N/A");
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

    public String getCustomerName() {
        return customerName;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }
}

