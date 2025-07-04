import java.util.List;

public interface ShippingService {
    void shipItems(List<ShippableItem> items);
    
    interface ShippableItem {
        String getName();
        double getWeight();
    }
}

class ShippingServiceImpl implements ShippingService {
    @Override
    public void shipItems(List<ShippableItem> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        System.out.println("** Shipment notice **");
        double totalWeight = 0;
        
        for (ShippableItem item : items) {
            System.out.printf("%dx %s %.0fg%n", 
                countOccurrences(items, item), 
                item.getName(), 
                item.getWeight() * 1000);
            totalWeight += item.getWeight();
        }
        
        System.out.printf("Total package weight %.1fkg%n", totalWeight);
    }

    private long countOccurrences(List<ShippableItem> items, ShippableItem target) {
        return items.stream().filter(item -> 
            item.getName().equals(target.getName()) && 
            item.getWeight() == target.getWeight()).count();
    }
}
