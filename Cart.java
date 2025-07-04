import java.util.HashMap;
import java.util.Map;

public class Cart {
    private final Map<Product, Integer> items = new HashMap<>();

    public void add(Product product, int quantity) {
        if (product.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough quantity available for " + product.getName());
        }
        
        if (product instanceof ExpirableProduct && ((ExpirableProduct) product).isExpired()) {
            throw new IllegalArgumentException("Product " + product.getName() + " is expired");
        }

        items.merge(product, quantity, Integer::sum);
    }

    public Map<Product, Integer> getItems() {
        return new HashMap<>(items);
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
