public interface Product {
    String getName();
    double getPrice();
    int getQuantity();
    void reduceQuantity(int amount);
}