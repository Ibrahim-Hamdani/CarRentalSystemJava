import java.io.*;
import java.util.*;

//  Variable's name and type with The Class Car
class Car {
    int id_;
    String type_;
    double cost_;
    boolean gpsAvailable_;
    boolean sunroofAvailable_;
    String renter;

        \\variables...
    public Car(int id_, String type_, double cost_, boolean gpsAvailable_, boolean sunroofAvailable_, String renter) {
        this.id_ = id_;
        this.type_ = type_;
        this.cost_ = cost_;
        this.gpsAvailable_ = gpsAvailable_;
        this.sunroofAvailable_ = sunroofAvailable_;
        this.renter = renter.equalsIgnoreCase("free") ? "free" : renter;
    }

    // get details functions to avoid rewritting the details at every end of the function.
    public String getDetails() {
        return id_ + " " + type_ + " " + cost_ + " " +
               (gpsAvailable_ ? "T" : "F") + " " +
               (sunroofAvailable_ ? "T" : "F") + " " + renter;
    }
}

// Class Car rental Service consist of all the functions.
public class Car_rental_Service {
    private List<Car> carList = new ArrayList<>();

    // Extract data form the file and store it into the list.
    public void manageCarData(String filename, boolean save) {
        if (save) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                for (Car car : carList) {
                    writer.write(car.getDetails());
                    writer.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving cars: " + e.getMessage());
            }
        } else {
            try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] details = line.split(" ");
                    int id_ = Integer.parseInt(details[0]);
                    String type_ = details[1];
                    double cost_ = Double.parseDouble(details[2]);
                    boolean gpsAvailable_ = details[3].equalsIgnoreCase("T");
                    boolean sunroofAvailable_ = details[4].equalsIgnoreCase("T");
                    String renter = details[5].equalsIgnoreCase("free") ? "free" : details[5];
                    carList.add(new Car(id_, type_, cost_, gpsAvailable_, sunroofAvailable_, renter));
                }
            } catch (IOException e) {
                System.out.println("Error loading cars: " + e.getMessage());
            }
        }
    }

    // Rent Car function.
    public void rentCar(String type_, double maxCost, boolean gps, boolean sunroof, String email) {
        for (Car car : carList) {
            if (car.renter.equals("free") &&
                car.type_.equalsIgnoreCase(type_) &&
                car.cost <= maxCost &&
                car.gpsAvailable_ == gps &&
                car.sunroofAvailable_ == sunroof) {
                car.renter = email;
                System.out.println("Car rented successfully: " + car.getDetails());
                return;
            }
        }

        // Check for next possible match.
        List<Car> potentialMatches = new ArrayList<>();
        for (Car car : carList) {
            if (car.renter.equals("free")) {
                int matchCount = 0;
                if (car.type_.equalsIgnoreCase(type_)) matchCount++;
                if (car.cost <= maxCost) matchCount++;
                if (car.gpsAvailable_ == gps) matchCount++;
                if (car.sunroofAvailable == sunroof) matchCount++;
                if (matchCount >= 3) {
                    potentialMatches.add(car);
                }
            }
        }

        // Display the next possible match to the user.
        if (!potentialMatches.isEmpty()) {
            System.out.println("No cars matching all criteria were found. Here are the next best matches:");
            for (Car car : potentialMatches) {
                System.out.println(car.getDetails());
            }

            // if user is interested in the suggest match assign car to the user and if not interested return back to the main menu.
            System.out.print("Would you like to rent one of these cars? (Y/N): ");
            Scanner scanner = new Scanner(System.in);
            String response = scanner.nextLine().trim().toUpperCase();

            if (response.equals("Y")) {
                System.out.print("Enter the car ID you want to rent: ");
                int selectedId = Integer.parseInt(scanner.nextLine().trim());

                for (Car car : potentialMatches) {
                    if (car.id_ == selectedId) {
                        car.renter = email;
                        System.out.println("Car rented successfully: " + car.getDetails());
                        return;
                    }
                }

                System.out.println("Invalid car ID selected. Returning to main menu.");
                return;
            }
        }

        System.out.println("No suitable cars available. Returning to main menu.");
    }

    // Ask the user car id and cancel the rental.
    public void cancelRental(int id_) {
        for (Car car : carList) {
            if (car.id_ == id && !car.renter.equals("free")) {
                car.renter = "free";
                System.out.println("Rental canceled successfully.");
                return;
            }
        }
        System.out.println("Car not rented or invalid car ID.");
    }

    // Display all the rental details.
    public void displayRentals() {
        for (Car car : carList) {
            System.out.println(car.getDetails());
        }
    }

    // Executing the program.
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Car_rental_Service rentalService = new Car_rental_Service();
        rentalService.manageCarData("cars.txt", false); // Load cars from file

        while (true) {
            System.out.println("\n1 - Rent a Car");
            System.out.println("2 - Cancel Rental");
            System.out.println("3 - View Rentals");
            System.out.println("4 - Exit");
            System.out.print("Enter your choice: ");
            int choice = Integer.parseInt(scanner.nextLine().trim());

            //  Assigning car to a user and show the data avaiailabe data to user
            if (choice == 1) {
                Set<String> availableTypes = new HashSet<>();
                for (Car car : rentalService.carList) {
                    if (car.renter.equals("free")) {
                        availableTypes.add(car.type_.toLowerCase());
                    }
                }

                if (availableTypes.isEmpty()) {
                    System.out.println("No car types available.");
                    continue;
                }

                System.out.println("Available car types: " + availableTypes);
                System.out.print("Enter Car Type: ");
                String type = scanner.nextLine().trim().toLowerCase();

                if (!availableTypes.contains(type)) {
                    System.out.println("No cars of type '" + type + "' are available.");
                    continue;
                }

                System.out.print("Enter your maximum price: ");
                double maxCost = Double.parseDouble(scanner.nextLine().trim());

                System.out.print("Do you want GPS? (T/F): ");
                boolean gps = scanner.nextLine().trim().equalsIgnoreCase("T");

                System.out.print("Do you want a Sunroof? (T/F): ");
                boolean sunroof = scanner.nextLine().trim().equalsIgnoreCase("T");

                System.out.print("Enter your Email: ");
                String email = scanner.nextLine().trim();

                rentalService.rentCar(type, maxCost, gps, sunroof, email);

                // Use cancel rental function.
            } else if (choice == 2) {
                System.out.print("Enter Car ID: ");
                int id_ = Integer.parseInt(scanner.nextLine().trim());
                rentalService.cancelRental(id_);

                // Use Display rental function.
            } else if (choice == 3) {
                rentalService.displayRentals();

            } else if (choice == 4) {
                rentalService.manageCarData("cars.txt", true); // Save cars to file
                System.out.println("Data saved. Exiting...");
                break;

            } else {
                System.out.println("Invalid choice. Try again.");
            }
        }
        scanner.close();
    }
}
