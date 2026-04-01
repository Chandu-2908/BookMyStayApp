import java.util.*;

// Reservation class
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private String roomId;
    private boolean active;

    public Reservation(String reservationId, String guestName, String roomType) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.active = true;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public boolean isActive() {
        return active;
    }

    public void cancel() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "ID=" + reservationId + ", Guest=" + guestName +
               ", RoomType=" + roomType + ", RoomID=" + roomId +
               ", Status=" + (active ? "CONFIRMED" : "CANCELLED");
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public void increment(String type) {
        inventory.put(type, inventory.get(type) + 1);
    }

    public void display() {
        System.out.println("\nInventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// Booking Service (simple confirm)
class BookingService {
    private InventoryService inventory;
    private Map<String, Reservation> confirmedBookings = new HashMap<>();
    private int counter = 1;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void confirmBooking(Reservation r) {
        if (inventory.isAvailable(r.getRoomType())) {
            String roomId = r.getRoomType().substring(0, 1).toUpperCase() + counter++;
            r.setRoomId(roomId);

            inventory.decrement(r.getRoomType());
            confirmedBookings.put(r.getReservationId(), r);

            System.out.println("Booking Confirmed: " + r);
        } else {
            System.out.println("Booking Failed for " + r.getGuestName());
        }
    }

    public Reservation getReservation(String id) {
        return confirmedBookings.get(id);
    }

    public Collection<Reservation> getAllBookings() {
        return confirmedBookings.values();
    }
}

// Cancellation Service (Rollback logic)
class CancellationService {
    private InventoryService inventory;

    // Stack for rollback tracking (LIFO)
    private Stack<String> releasedRoomIds = new Stack<>();

    public CancellationService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void cancelReservation(Reservation r) {

        if (r == null) {
            System.out.println("Cancellation Failed: Reservation does not exist.");
            return;
        }

        if (!r.isActive()) {
            System.out.println("Cancellation Failed: Already cancelled -> " + r.getReservationId());
            return;
        }

        // Step 1: Push room ID to stack (rollback tracking)
        releasedRoomIds.push(r.getRoomId());

        // Step 2: Restore inventory
        inventory.increment(r.getRoomType());

        // Step 3: Mark as cancelled
        r.cancel();

        System.out.println("Cancelled Successfully: " + r.getReservationId()
                + " | Room Released: " + r.getRoomId());
    }

    public void showRollbackStack() {
        System.out.println("\nRollback Stack (Recent First): " + releasedRoomIds);
    }
}

// Main Class
public class UseCase10BookingCancellation {
    public static void main(String[] args) {

        // Step 1: Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Deluxe", 2);
        inventory.addRoom("Standard", 1);

        // Step 2: Booking Service
        BookingService bookingService = new BookingService(inventory);

        // Step 3: Create Reservations
        Reservation r1 = new Reservation("R101", "Alice", "Deluxe");
        Reservation r2 = new Reservation("R102", "Bob", "Standard");

        // Step 4: Confirm Bookings
        bookingService.confirmBooking(r1);
        bookingService.confirmBooking(r2);

        // Step 5: Cancellation Service
        CancellationService cancelService = new CancellationService(inventory);

        // Step 6: Cancel booking
        cancelService.cancelReservation(bookingService.getReservation("R101"));

        // Invalid cancellation
        cancelService.cancelReservation(bookingService.getReservation("R999"));

        // Duplicate cancellation
        cancelService.cancelReservation(bookingService.getReservation("R101"));

        // Step 7: Display final state
        System.out.println("\nAll Bookings:");
        for (Reservation r : bookingService.getAllBookings()) {
            System.out.println(r);
        }

        inventory.display();
        cancelService.showRollbackStack();
    }
}