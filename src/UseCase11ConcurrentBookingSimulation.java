import java.util.*;

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
}

// Shared Booking Queue (Thread-Safe Access)
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public synchronized void addRequest(Reservation r) {
        queue.offer(r);
        System.out.println(Thread.currentThread().getName() +
                " added request for " + r.getGuestName());
    }

    public synchronized Reservation getRequest() {
        return queue.poll();
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}

// Inventory Service (Critical Section)
class InventoryService {
    private Map<String, Integer> inventory = new HashMap<>();

    public void addRoom(String type, int count) {
        inventory.put(type, count);
    }

    // synchronized → prevents race condition
    public synchronized boolean allocateRoom(String type) {
        int count = inventory.getOrDefault(type, 0);

        if (count > 0) {
            inventory.put(type, count - 1);
            return true;
        }
        return false;
    }

    public synchronized void display() {
        System.out.println("\nFinal Inventory:");
        for (String type : inventory.keySet()) {
            System.out.println(type + " -> " + inventory.get(type));
        }
    }
}

// Booking Processor (Thread)
class BookingProcessor extends Thread {

    private BookingRequestQueue queue;
    private InventoryService inventory;
    private static int roomCounter = 1;

    public BookingProcessor(String name, BookingRequestQueue queue, InventoryService inventory) {
        super(name);
        this.queue = queue;
        this.inventory = inventory;
    }

    @Override
    public void run() {
        while (true) {

            Reservation r;

            // synchronized queue access
            synchronized (queue) {
                if (queue.isEmpty()) break;
                r = queue.getRequest();
            }

            if (r != null) {
                processBooking(r);
            }
        }
    }

    private void processBooking(Reservation r) {

        // Critical section → inventory update
        synchronized (inventory) {
            boolean success = inventory.allocateRoom(r.getRoomType());

            if (success) {
                String roomId = r.getRoomType().charAt(0) + "" + (roomCounter++);
                System.out.println(Thread.currentThread().getName() +
                        " CONFIRMED " + r.getGuestName() +
                        " | RoomID=" + roomId);
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " FAILED for " + r.getGuestName() +
                        " (No rooms)");
            }
        }
    }
}

// Main Class
public class UseCase11ConcurrentBookingSimulation {
    public static void main(String[] args) {

        // Shared Queue
        BookingRequestQueue queue = new BookingRequestQueue();

        // Add requests
        queue.addRequest(new Reservation("Alice", "Deluxe"));
        queue.addRequest(new Reservation("Bob", "Deluxe"));
        queue.addRequest(new Reservation("Charlie", "Deluxe"));
        queue.addRequest(new Reservation("David", "Standard"));
        queue.addRequest(new Reservation("Eve", "Standard"));

        // Inventory
        InventoryService inventory = new InventoryService();
        inventory.addRoom("Deluxe", 2);
        inventory.addRoom("Standard", 1);

        // Multiple threads (simulate users)
        BookingProcessor t1 = new BookingProcessor("Thread-1", queue, inventory);
        BookingProcessor t2 = new BookingProcessor("Thread-2", queue, inventory);
        BookingProcessor t3 = new BookingProcessor("Thread-3", queue, inventory);

        // Start threads
        t1.start();
        t2.start();
        t3.start();

        // Wait for completion
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Final inventory
        inventory.display();
    }
}