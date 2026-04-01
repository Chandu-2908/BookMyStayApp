import java.util.*;

public class UseCase4RoomSearch {
    public static void main(String[] args) {

        // Inventory setup
        Inventory inventory = new Inventory();
        inventory.addRoom("Single", 3);
        inventory.addRoom("Double", 0); // won't show
        inventory.addRoom("Suite", 2);

        // Room details
        Map<String, Room> roomMap = new HashMap<>();

        roomMap.put("Single", new Room("Single", 2000,
                Arrays.asList("WiFi", "AC", "TV")));

        roomMap.put("Double", new Room("Double", 3500,
                Arrays.asList("WiFi", "AC", "TV", "MiniBar")));

        roomMap.put("Suite", new Room("Suite", 6000,
                Arrays.asList("WiFi", "AC", "TV", "MiniBar", "Jacuzzi")));

        // Search service
        SearchService service = new SearchService(inventory, roomMap);

        // Execute search
        service.showAvailableRooms();
    }
}