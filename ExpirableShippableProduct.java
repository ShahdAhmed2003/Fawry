public class ExpirableShippableProduct implements Product, ExpirableProduct, ShippableProduct {
    private final String name;
    private final double price;
    private int quantity;
    private final boolean isExpired;
    private final double weight;

    public ExpirableShippableProduct(String name, double price, int quantity, boolean isExpired, double weight) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.isExpired = isExpired;
        this.weight = weight;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return price;
    }

    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public void reduceQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Cannot reduce quantity more than available");
        }
        quantity -= amount;
    }

    @Override
    public boolean isExpired() {
        return isExpired;
    }

    @Override
    public double getWeight() {
        return weight;
    }
}
