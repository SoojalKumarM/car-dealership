package kumar.model;





import kumar.Interfaces.VehicleInterface;

public class Vehicle implements VehicleInterface, Comparable<Vehicle> {
    protected String brand;
    protected String model;
    protected double basePrice;
    protected int age;
    protected double km;
    protected FuelType fuelType;

    public Vehicle(String brand, String model, double basePrice, int age, double km, FuelType fuelType) {
        this.brand = brand;
        this.model = model;
        this.basePrice = basePrice;
        this.age = age;
        this.km = km;
        this.fuelType = fuelType;
    }

    @Override
    public void applyDiscount(int percentage) {
        this.basePrice -= this.basePrice * (percentage / 100.0);
    }

    @Override
    public int getFinalPrice() {
        double calculated = basePrice - (age * 150000.0);
        if (calculated < 200000.0) {
            calculated = 200000.0;
        }
        if (fuelType == FuelType.HYBRID) {
            calculated *= 1.15;
        } else if (fuelType == FuelType.DIESEL) {
            calculated *= 1.05;
        }
        return (int) Math.round(calculated);
    }

    @Override
    public double averageKmPerYear() {
        if (age == 0) {
            return km;
        }
        return km / age;
    }

    @Override
    public String toString() {
        return String.format("%s %s [%s] | Age: %d yrs | Odometer: %.1f km | Fuel: %s | Final Price: %,d HUF | Avg Km/Yr: %.1f",
                brand, model, getClass().getSimpleName(), age, km, fuelType, getFinalPrice(), averageKmPerYear());
    }

    @Override
    public int compareTo(Vehicle other) {
        int priceCompare = Integer.compare(this.getFinalPrice(), other.getFinalPrice());
        if (priceCompare != 0) {
            return priceCompare;
        }
        return this.toString().compareTo(other.toString());
    }

    // Getters and Setters
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public double getBasePrice() { return basePrice; }
    public void setBasePrice(double basePrice) { this.basePrice = basePrice; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public double getKm() { return km; }
    public void setKm(double km) { this.km = km; }

    public FuelType getFuelType() { return fuelType; }
    public void setFuelType(FuelType fuelType) { this.fuelType = fuelType; }
}