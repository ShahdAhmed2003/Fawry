import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ECommerceSystem {
    private final ShippingService shippingService;

    public ECommerceSystem(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    public void checkout(Customer customer, Cart cart) {
        if (cart.isEmpty()) {
            throw new IllegalStateException("Cannot checkout with empty cart");
        }

        // Check all items before processing
        for (Map.Entry<Product, Integer> entry : cart.getItems().entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();

            if (product.getQuantity() < quantity) {
                throw new IllegalStateException("Product " + product.getName() + " is out of stock");
            }

            if (product instanceof ExpirableProduct && ((ExpirableProduct) product).isExpired()) {
                throw new IllegalStateException("Product " + product.getName() + " is expired");
            }
        }

        // Calculate subtotal
        double subtotal = cart.getItems().entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();

        // Prepare shippable items
        List<ShippingService.ShippableItem> shippableItems = new ArrayList<>();
        cart.getItems().forEach((product, quantity) -> {
            if (product instanceof ShippableProduct) {
                ShippableProduct shippable = (ShippableProduct) product;
                for (int i = 0; i < quantity; i++) {
                    shippableItems.add(new ShippingService.ShippableItem() {
                        @Override
                        public String getName() {
                            return shippable.getName();
                        }

                        @Override
                        public double getWeight() {
                            return shippable.getWeight();
                        }
                    });
                }
            }
        });

        // Calculate shipping (simplified: $10 per kg)
        double shippingFee = shippableItems.stream()
                .mapToDouble(ShippingService.ShippableItem::getWeight)
                .sum() * 10;

        double total = subtotal + shippingFee;

        // Check customer balance
        if (customer.getBalance() < total) {
            throw new IllegalStateException("Insufficient balance");
        }

        // Process shipping
        if (!shippableItems.isEmpty()) {
            shippingService.shipItems(shippableItems);
        }

        // Process payment
        customer.deductBalance(total);

        // Update inventory
        cart.getItems().forEach((product, quantity) -> product.reduceQuantity(quantity));

        // Print receipt
        printReceipt(cart, subtotal, shippingFee, total, customer);
    }

    private void printReceipt(Cart cart, double subtotal, double shippingFee, double total, Customer customer) {
        System.out.println("** Checkout receipt **");
        cart.getItems().forEach((product, quantity) -> {
            System.out.printf("%dx %s %.0f%n", quantity, product.getName(), product.getPrice() * quantity);
        });
        System.out.println("----------------------");
        System.out.printf("Subtotal %.0f%n", subtotal);
        System.out.printf("Shipping %.0f%n", shippingFee);
        System.out.printf("Amount %.0f%n", total);
        System.out.printf("Remaining balance %.0f%n", customer.getBalance());
    }

    public static void main(String[] args) {
        // Setup
        ShippingService shippingService = new ShippingServiceImpl();
        ECommerceSystem system = new ECommerceSystem(shippingService);

        // Create products
        Product cheese = new ExpirableShippableProduct("Cheese", 100, 10, false, 0.4);
        Product biscuits = new ExpirableShippableProduct("Biscuits", 150, 5, false, 0.7);
        Product tv = new RegularProduct("TV", 1000, 3);
        Product scratchCard = new DigitalProduct("Scratch Card", 50, 100);

        // Create customer
        Customer customer = new Customer("John Doe", 2000);

        // Test case 1: Example from requirements
        Cart cart1 = new Cart();
        cart1.add(cheese, 2);
        cart1.add(biscuits, 1);
        system.checkout(customer, cart1);

        // Test case 2: Digital product
        Cart cart2 = new Cart();
        cart2.add(scratchCard, 3);
        cart2.add(tv, 1);
        system.checkout(customer, cart2);

        // Test case 3: Error cases (uncomment to test)
        // Cart cart3 = new Cart();
        // system.checkout(customer, cart3); // Empty cart

        // Cart cart4 = new Cart();
        // cart4.add(cheese, 100); // Insufficient quantity
        // system.checkout(customer, cart4);

        // Customer poorCustomer = new Customer("Poor", 10);
        // Cart cart5 = new Cart();
        // cart5.add(cheese, 1);
        // system.checkout(poorCustomer, cart5); // Insufficient balance
    }
}