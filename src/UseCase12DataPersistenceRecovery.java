import java.io.*;
import java.util.*;

// Reservation (Serializable)
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    @Override
    public String toString() {
        return "Guest=" + guestName + ", RoomType=" + roomType;
    }
}

// Wrapper class to persist system state
class SystemState implements Serializable {
    private static final long serialVersionUID = 1L;

    List<Reservation> bookingHistory;
    Map<String, Integer> inventory;

    public SystemState(List<Reservation> bookingHistory, Map<String, Integer> inventory) {
        this.bookingHistory = bookingHistory;
        this.inventory = inventory;
    }
}

// Persistence Service
class PersistenceService {

    private static final String FILE_NAME = "system_state.ser";

    // SAVE
    public static void save(SystemState state) {
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {

            oos.writeObject(state);
            System.out.println("\nSystem state saved successfully.");

        } catch (IOException e) {
            System.out.println("Error saving state: " + e.getMessage());
        }
    }

    // LOAD
    public static SystemState load() {
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(FILE_NAME))) {

            SystemState state = (SystemState) ois.readObject();
            System.out.println("\nSystem state loaded successfully.");
            return state;

        } catch (FileNotFoundException e) {
            System.out.println("\nNo previous state found. Starting fresh.");
        } catch (Exception e) {
            System.out.println("\nError loading state. Starting fresh.");
        }
        return null;
    }
}

// Main Class
public class UseCase12DataPersistenceRecovery {

    public static void main(String[] args) {

        // Step 1: Try loading previous state
        SystemState state = PersistenceService.load();

        List<Reservation> history;
        Map<String, Integer> inventory;

        if (state != null) {
            // Restore state
            history = state.bookingHistory;
            inventory = state.inventory;

            System.out.println("\nRecovered Booking History:");
            for (Reservation r : history) {
                System.out.println(r);
            }

        } else {
            // Fresh start
            history = new ArrayList<>();
            inventory = new HashMap<>();

            inventory.put("Deluxe", 2);
            inventory.put("Standard", 1);

            // Simulate new bookings
            history.add(new Reservation("Alice", "Deluxe"));
            history.add(new Reservation("Bob", "Standard"));

            inventory.put("Deluxe", 1);
            inventory.put("Standard", 0);

            System.out.println("\nNew bookings created.");
        }

        // Display current inventory
        System.out.println("\nCurrent Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }

        // Step 2: Save state before shutdown
        SystemState newState = new SystemState(history, inventory);
        PersistenceService.save(newState);
    }
}