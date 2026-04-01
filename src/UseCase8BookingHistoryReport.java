import java.util.*;

// Reservation class
class Reservation {
    private String guestName;
    private String roomType;
    private String roomId;

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

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public String toString() {
        return "Guest=" + guestName + ", RoomType=" + roomType + ", RoomID=" + roomId;
    }
}

// Queue for requests
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

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }
}

// Booking History (List → preserves order)
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    public void add(Reservation r) {
        history.add(r);
    }

    public List<Reservation> getAll() {
        return history;
    }
}

// Booking Service (Allocation + Save History)
class BookingService {
    private InventoryService inventory;
    private BookingHistory history;

    private Set<String> usedRoomIds = new HashSet<>();
    private int counter = 1;

    public BookingService(InventoryService inventory, BookingHistory history) {
        this.inventory = inventory;
        this.history = history;
    }

    public void process(BookingRequestQueue queue) {
        while (!queue.isEmpty()) {
            Reservation r = queue.getNext();

            if (inventory.isAvailable(r.getRoomType())) {

                // Generate unique ID
                String roomId;
                do {
                    roomId = r.getRoomType().substring(0, 1).toUpperCase() + counter++;
                } while (usedRoomIds.contains(roomId));

                usedRoomIds.add(roomId);
                inventory.decrement(r.getRoomType());

                r.setRoomId(roomId);

                // Add to history
                history.add(r);

                System.out.println("Confirmed: " + r);
            } else {
                System.out.println("Failed: No rooms for " + r.getGuestName());
            }
        }
    }
}

// Report Service
class BookingReportService {

    public void printAllBookings(List<Reservation> list) {
        System.out.println("\n--- Booking History ---");
        for (Reservation r : list) {
            System.out.println(r);
        }
    }

    public void summaryByRoomType(List<Reservation> list) {
        Map<String, Integer> summary = new HashMap<>();

        for (Reservation r : list) {
            summary.put(r.getRoomType(),
                    summary.getOrDefault(r.getRoomType(), 0) + 1);
        }

        System.out.println("\n--- Booking Summary (Room Type) ---");
        for (String type : summary.keySet()) {
            System.out.println(type + " -> " + summary.get(type));
        }
    }
}

// Main Class
public class UseCase8BookingHistoryReport {
    public static void main(String[] args) {

        // Step 1: Queue
        BookingRequestQueue queue = new BookingRequestQueue();
        queue.addRequest(new Reservation("Alice", "Deluxe"));
        queue.addRequest(new Reservation("Bob", "Standard"));
        queue.addRequest(new Reservation("Charlie", "Deluxe"));
        queue.addRequest(new Reservation("David", "Suite"));

        // Step 2: Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Deluxe", 2);
        inventory.addRoom("Standard", 1);
        inventory.addRoom("Suite", 0);

        // Step 3: History
        BookingHistory history = new BookingHistory();

        // Step 4: Booking Service
        BookingService service = new BookingService(inventory, history);
        service.process(queue);

        // Step 5: Reporting
        BookingReportService report = new BookingReportService();

        report.printAllBookings(history.getAll());
        report.summaryByRoomType(history.getAll());
    }
}