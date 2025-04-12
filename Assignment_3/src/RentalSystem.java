import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class RentalSystem {
	private static RentalSystem theOne; 
	
    private List<Vehicle> vehicles = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();
    private RentalHistory rentalHistory = new RentalHistory();



    
    private RentalSystem() {loadData();}
    public static RentalSystem getInstance() {
        if (theOne == null) {
        	theOne = new RentalSystem();
        }
        return theOne;
    }

    public void addVehicle(Vehicle vehicle) {
        vehicles.add(vehicle);
        saveVehicle(vehicle);
        
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveCustomer(customer);
    }

    public void rentVehicle(Vehicle vehicle, Customer customer, LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, amount, "RENT"));
            System.out.println("Vehicle rented to " + customer.getCustomerName());
        }
        else {
            System.out.println("Vehicle is not available for renting.");
        }
        saveRecord(rentalHistory);
    }

    public void returnVehicle(Vehicle vehicle, Customer customer, LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            rentalHistory.addRecord(new RentalRecord(vehicle, customer, date, extraFees, "RETURN"));
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            saveRecord(rentalHistory);
        }
        else {
            System.out.println("Vehicle is not rented.");
        }
    }    

    public void displayVehicles(boolean onlyAvailable) {
    	System.out.println("|     Type         |\tPlate\t|\tMake\t|\tModel\t|\tYear\t|");
    	System.out.println("---------------------------------------------------------------------------------");
    	 
        for (Vehicle v : vehicles) {
            if (!onlyAvailable || v.getStatus() == Vehicle.VehicleStatus.AVAILABLE) {
                System.out.println("|     " + (v instanceof Car ? "Car          " : "Motorcycle   ") + "|\t" + v.getLicensePlate() + "\t|\t" + v.getMake() + "\t|\t" + v.getModel() + "\t|\t" + v.getYear() + "\t|\t");
            }
        }
        System.out.println();
    }
    
    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        for (RentalRecord record : rentalHistory.getRentalHistory()) {
            System.out.println(record.toString());
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(String id) {
        for (Customer c : customers)
            if (c.getCustomerId() == Integer.parseInt(id))
                return c;
        return null;
    }
    
    public void saveVehicle(Vehicle vehicle){
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("vehicles.txt",true));
			writer.write("\n"+vehicle.getInfo());
			writer.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    	
    }
    
    public void saveCustomer(Customer customer){
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("customers.txt",true));
			writer.write("\n"+customer.toString());
			writer.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    	
    }
    
    public void saveRecord(RentalHistory record){
    	try {
			BufferedWriter writer = new BufferedWriter(new FileWriter("rental_records.txt",true));
			writer.write("\n"+rentalHistory.getRentalHistory());
			writer.close();
		} catch (IOException e) {
		
			e.printStackTrace();
		}
    	
    }

    
   

	public void loadData() {
    	try (BufferedReader reader = new BufferedReader(new FileReader("vehicles.txt"))) {
            String line;
			try {
				line = reader.readLine();
			
            String[] parts = line.split("\\|");                
                    String plate = parts[1];
                    String make = parts[2];
                    String model = parts[3];
                    int year = Integer.parseInt(parts[4].trim());
					Vehicle.VehicleStatus status = parts[5].equalsIgnoreCase("RENTED") ? Vehicle.VehicleStatus.RENTED : Vehicle.VehicleStatus.AVAILABLE;
                    
                    String extraInfo = parts[6];
                    Vehicle vehicle=null;                   
                    if (extraInfo.startsWith("Seats:")) {
                        int seats = Integer.parseInt(extraInfo.split(":")[1]);
                        vehicle = new Car(make, model, year, seats);
                    } else if (extraInfo.startsWith("Sidecar:")) {
                        boolean sidecar = Boolean.parseBoolean(extraInfo.split(":")[1]);
                        vehicle = new Motorcycle(make, model, year, sidecar);
                    } else if (extraInfo.startsWith("Cargo:")) {
                        double cargo = Double.parseDouble(extraInfo.split(":")[1]);
                        vehicle = new Truck(make, model, year, cargo);
                    }

                    if (vehicle != null) {
                    	vehicle.setLicensePlate(plate);
	                    addVehicle(vehicle);
	                    
                    }
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			};
            } catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        
    	
    	
    }
}
