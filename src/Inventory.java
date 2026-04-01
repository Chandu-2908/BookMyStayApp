import java.util.*;

public class Inventory {
    private Map<String, Integer> availability = new HashMap<>();

    public void addRoom(String type, int count) {
        availability.put(type, count);
    }

    // Read-only
    public int getAvailability(String type) {
        return availability.getOrDefault(type, 0);
    }

    public Set<String> getRoomTypes() {
        return availability.keySet();
    }
}