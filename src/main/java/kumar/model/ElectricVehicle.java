

package kumar.model;

import kumar.Interfaces.ElectricInterface;

public class ElectricVehicle extends Vehicle implements ElectricInterface {
    private int batteryCapacity;
    private boolean hasHeatPump;

    public ElectricVehicle(String brand, String model, double basePrice, int age, double km, FuelType fuelType, int batteryCapacity, boolean hasHeatPump) {
        super(brand, model, basePrice, age, km, fuelType);
        this.batteryCapacity = batteryCapacity;
        this.hasHeatPump = hasHeatPump;
    }

    @Override
    public int getFinalPrice() {
        double baseCalc = super.getFinalPrice();
        if (batteryCapacity >= 60) {
            baseCalc *= 1.10;
        }
        if (hasHeatPump) {
            baseCalc *= 1.05;
        }
        return (int) Math.round(baseCalc);
    }

    @Override
    public String toString() {
        String baseStr = super.toString();
        // Insert battery & heat pump info right before the closing bracket
        int idx = baseStr.lastIndexOf(']');
        if (idx != -1) {
            return baseStr.substring(0, idx) + String.format(" | Battery: %d kWh | Heat Pump: %s]", batteryCapacity, hasHeatPump ? "Yes" : "No");
        }
        return baseStr;
    }

    @Override
    public boolean hasSameRangeCategory(Vehicle other) {
        if (other instanceof ElectricVehicle) {
            ElectricVehicle ev = (ElectricVehicle) other;
            return Math.abs(this.batteryCapacity - ev.batteryCapacity) <= 5;
        }
        return false;
    }

    @Override
    public int batteryCostShare() {
        return batteryCapacity * 25000;
    }

    public int getBatteryCapacity() { return batteryCapacity; }
    public void setBatteryCapacity(int batteryCapacity) { this.batteryCapacity = batteryCapacity; }

    public boolean isHasHeatPump() { return hasHeatPump; }
    public void setHasHeatPump(boolean hasHeatPump) { this.hasHeatPump = hasHeatPump; }
}