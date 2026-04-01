
import java.util.*;

public class SearchService {
    private Inventory inventory;
    private Map<String, Room> roomMap;

    public SearchService(Inventory inventory, Map<String, Room> roomMap) {
        this.inventory = inventory;
        this.roomMap = roomMap;
    }

    public void showAvailableRooms() {
        System.out.println("=== Available Rooms ===");

        for (String type : inventory.getRoomTypes()) {
            int count = inventory.getAvailability(type);

            if (count > 0) {
                Room room = roomMap.get(type);

                // Defensive check
                if (room != null) {
                    System.out.println("Room Type: " + room.getType());
                    System.out.println("Price: ₹" + room.getPrice());
                    System.out.println("Amenities: " + room.getAmenities());
                    System.out.println("Available Count: " + count);
                    System.out.println("----------------------");
                }
            }
        }
    }
}