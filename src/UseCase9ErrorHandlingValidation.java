import java.util.*;

// Custom Exception
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

// Reservation class
class Reservation {
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

// Queue
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNext() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Inventory Service
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoomType(String type, int count) {
        inventory.put(type, count);
    }

    public boolean isValidRoomType(String type) {
        return inventory.containsKey(type);
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) throws InvalidBookingException {
        int count = inventory.getOrDefault(type, 0);

        if (count <= 0) {
            throw new InvalidBookingException("Inventory cannot go negative for " + type);
        }

        inventory.put(type, count - 1);
    }
}

// Validator
class InvalidBookingValidator {

    public static void validate(Reservation r, InventoryService inventory)
            throws InvalidBookingException {

        if (r.getGuestName() == null || r.getGuestName().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty");
        }

        if (!inventory.isValidRoomType(r.getRoomType())) {
            throw new InvalidBookingException("Invalid room type: " + r.getRoomType());
        }

        if (!inventory.isAvailable(r.getRoomType())) {
            throw new InvalidBookingException("No rooms available for type: " + r.getRoomType());
        }
    }
}

// Booking Service
class BookingService {
    private InventoryService inventory;
    private Set<String> usedRoomIds = new HashSet<>();
    private int counter = 1;

    public BookingService(InventoryService inventory) {
        this.inventory = inventory;
    }

    public void process(BookingRequestQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.getNext();

            try {
                // VALIDATION (Fail-Fast)
                InvalidBookingValidator.validate(r, inventory);

                // Generate unique ID
                String roomId;
                do {
                    roomId = r.getRoomType().substring(0, 1).toUpperCase() + counter++;
                } while (usedRoomIds.contains(roomId));

                usedRoomIds.add(roomId);

                // Safe inventory update
                inventory.decrement(r.getRoomType());

                System.out.println("Booking Confirmed: " + r + " | RoomID=" + roomId);

            } catch (InvalidBookingException e) {
                // Graceful failure
                System.out.println("Booking Failed for " + r.getGuestName()
                        + " -> " + e.getMessage());
            }
        }
    }
}

// Main Class
public class UseCase9ErrorHandlingValidation {
    public static void main(String[] args) {

        // Step 1: Inventory setup
        InventoryService inventory = new InventoryService();
        inventory.addRoomType("Deluxe", 1);
        inventory.addRoomType("Standard", 1);

        // Step 2: Queue with valid + invalid cases
        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Alice", "Deluxe"));     // valid
        queue.addRequest(new Reservation("Bob", "Suite"));        // invalid type
        queue.addRequest(new Reservation("", "Standard"));        // invalid name
        queue.addRequest(new Reservation("Charlie", "Deluxe"));   // no availability

        // Step 3: Process
        BookingService service = new BookingService(inventory);
        service.process(queue);
    }
}