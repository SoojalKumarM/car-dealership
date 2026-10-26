
package kumar.Main;
import kumar.model.*;

import java.io.*;
import java.util.*;

public class Dealership {
    private TreeSet<Vehicle> vehicles = new TreeSet<>();

    public void loadVehicles(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            createDefaultVehiclesFile(filename);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;

                String[] parts = line.split("#");
                String type = parts[0].trim();

                if (type.equalsIgnoreCase("VEHICLE")) {
                    String brandModel = parts[1].trim();
                    String[] bm = brandModel.split(" ", 2);
                    String brand = bm[0];
                    String model = bm.length > 1 ? bm[1] : "";

                    double basePrice = Double.parseDouble(parts[2].trim());
                    int age = Integer.parseInt(parts[3].trim());
                    double km = Double.parseDouble(parts[4].trim());
                    FuelType fuelType = FuelType.valueOf(parts[5].trim().toUpperCase());

                    vehicles.add(new Vehicle(brand, model, basePrice, age, km, fuelType));

                } else if (type.equalsIgnoreCase("ELECTRIC")) {
                    String brandModel = parts[1].trim();
                    String[] bm = brandModel.split(" ", 2);
                    String brand = bm[0];
                    String model = bm.length > 1 ? bm[1] : "";

                    double basePrice = Double.parseDouble(parts[2].trim());
                    int age = Integer.parseInt(parts[3].trim());
                    double km = Double.parseDouble(parts[4].trim());
                    FuelType fuelType = FuelType.valueOf(parts[5].trim().toUpperCase());
                    int batteryCapacity = Integer.parseInt(parts[6].trim());
                    boolean hasHeatPump = parts[7].trim().equalsIgnoreCase("yes");

                    vehicles.add(new ElectricVehicle(brand, model, basePrice, age, km, fuelType, batteryCapacity, hasHeatPump));
                }
            }
            System.out.println("Successfully loaded " + vehicles.size() + " vehicles from " + filename);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    private void createDefaultVehiclesFile(String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.println("VEHICLE#Toyota Corolla#5500000#4#75000#HYBRID");
            writer.println("VEHICLE#Volkswagen Golf#4200000#6#120000#DIESEL");
            writer.println("VEHICLE#Ford Focus#3100000#8#145000#PETROL");
            writer.println("VEHICLE#BMW 320d#7800000#3#60000#DIESEL");
            writer.println("VEHICLE#Opel Astra#2200000#11#190000#PETROL");
            writer.println("ELECTRIC#Nissan Leaf#4800000#5#65000#HYBRID#40#no");
            writer.println("ELECTRIC#Tesla Model 3#11500000#2#38000#HYBRID#75#yes");
            writer.println("ELECTRIC#Renault Zoe#3900000#6#82000#HYBRID#52#yes");
            writer.println("ELECTRIC#Kia EV6#13200000#1#15000#HYBRID#77#yes");
            writer.println("ELECTRIC#VW ID.3#7100000#4#54000#HYBRID#58#no");
            System.out.println("Created default " + filename + " file.");
        } catch (IOException e) {
            System.err.println("Could not create default file: " + e.getMessage());
        }
    }

    public void processAndReport(String outputFilename) {
        StringBuilder output = new StringBuilder();

        // 1. Fleet Average Mileage
        double totalKm = 0;
        for (Vehicle v : vehicles) {
            totalKm += v.getKm();
        }
        double fleetAvgMileage = vehicles.isEmpty() ? 0 : totalKm / vehicles.size();
        appendAndPrint(output, "1. Fleet Average Mileage: " + String.format("%.2f km", fleetAvgMileage));

        // 2. Cheapest Vehicle
        Vehicle cheapest = vehicles.isEmpty() ? null : vehicles.first();
        appendAndPrint(output, "2. Cheapest Vehicle:\n   " + (cheapest != null ? cheapest.toString() : "None"));

        // 3. Top Electric Vehicle
        ElectricVehicle topElectric = null;
        for (Vehicle v : vehicles) {
            if (v instanceof ElectricVehicle) {
                ElectricVehicle ev = (ElectricVehicle) v;
                if (topElectric == null || ev.getFinalPrice() > topElectric.getFinalPrice()) {
                    topElectric = ev;
                }
            }
        }
        String topElectricStr = topElectric != null ?
                String.format("Battery Capacity: %d kWh | Average Annual Km: %.2f km/yr", topElectric.getBatteryCapacity(), topElectric.averageKmPerYear()) :
                "No electric vehicles found.";
        appendAndPrint(output, "3. Top Electric Vehicle:\n   " + topElectricStr);

        // 4. Total Fleet Inventory Value
        long totalInventoryValue = 0;
        for (Vehicle v : vehicles) {
            totalInventoryValue += v.getFinalPrice();
        }
        appendAndPrint(output, String.format("4. Total Fleet Inventory Value: %,d HUF", totalInventoryValue));

        // 5. Filtered Hybrid Inventory (Hybrid vehicles with final price <= overall average final price)
        double avgFinalPrice = vehicles.isEmpty() ? 0 : (double) totalInventoryValue / vehicles.size();
        appendAndPrint(output, String.format("5. Filtered Hybrid Inventory (Price <= Average: %.2f HUF):", avgFinalPrice));
        int hybridCount = 0;
        for (Vehicle v : vehicles) {
            if (v.getFuelType() == FuelType.HYBRID && v.getFinalPrice() <= avgFinalPrice) {
                appendAndPrint(output, "   - " + v.toString());
                hybridCount++;
            }
        }
        if (hybridCount == 0) appendAndPrint(output, "   (None found)");

        // 6. Young Inventory Count (age < 5)
        long youngCount = vehicles.stream().filter(v -> v.getAge() < 5).count();
        appendAndPrint(output, "6. Young Inventory Count (Age < 5 years): " + youngCount);

        // 7. Discount Simulation (Apply 10% discount to the first available vehicle)
        if (!vehicles.isEmpty()) {
            Vehicle sampleVehicle = vehicles.first();
            int priceBefore = sampleVehicle.getFinalPrice();
            sampleVehicle.applyDiscount(10);
            int priceAfter = sampleVehicle.getFinalPrice();
            appendAndPrint(output, String.format("7. Discount Simulation (10%% discount on %s %s):\n   Price before: %,d HUF -> Updated Final Price: %,d HUF",
                    sampleVehicle.getBrand(), sampleVehicle.getModel(), priceBefore, priceAfter));
        } else {
            appendAndPrint(output, "7. Discount Simulation: No vehicles available.");
        }

        // Write to output file
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilename))) {
            writer.write(output.toString());
            System.out.println("\nReport successfully written to " + outputFilename);
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }

    private void appendAndPrint(StringBuilder sb, String text) {
        System.out.println(text);
        sb.append(text).append("\n");
    }

    public static void main(String[] args) {
        Dealership dealership = new Dealership();
        dealership.loadVehicles("vehicles.txt");
        System.out.println("\n--- DEALERSHIP ANALYTICS REPORT ---\n");
        dealership.processAndReport("outputDealership.txt");



    }
}
