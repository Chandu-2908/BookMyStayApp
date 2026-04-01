public class UC2RoomInitialization {

    public static void main(String[] args) {

        // Creating room objects using polymorphism
        Room singleRoom = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suiteRoom = new SuiteRoom();

        // Static availability variables
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display details
        System.out.println("===== Room Availability =====");

        singleRoom.displayDetails();
        System.out.println("Available: " + singleAvailable);
        System.out.println("------------------------");

        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailable);
        System.out.println("------------------------");

        suiteRoom.displayDetails();
        System.out.println("Available: " + suiteAvailable);
        System.out.println("------------------------");

        System.out.println("Application Terminated.");
    }
}

/**
 * Abstract Room class representing common properties of all rooms.
 */
abstract class Room {

    protected int beds;
    protected double price;
    protected String type;

    // Constructor
    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    // Method to display room details
    public void displayDetails() {
        System.out.println("Room Type: " + type);
        System.out.println("Beds: " + beds);
        System.out.println("Price per night: $" + price);
    }
}

class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 1000.0);
    }
}

class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 2000.0);
    }
}


class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 5000.0);
    }
}
