import java.util.*;

interface Shippable {
    String getName();

    double getWeight();
}

abstract class Product {
    protected String name;
    protected double price;
    protected int quantity;
    protected boolean expired;

    public Product(String name, double price, int quantity, boolean expired) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price must be non-negative");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be non-negative");
        }
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.expired = expired;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isExpired() {
        return expired;
    }

    public void reduceQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Not enough stock for " + name);
        }
        quantity -= amount;
    }

    public abstract boolean requiresShipping();
}

class ShippableProduct extends Product implements Shippable {
    private double weight;

    public ShippableProduct(String name, double price, int quantity, boolean expired, double weight) {
        super(name, price, quantity, expired);
        if (weight < 0) {
            throw new IllegalArgumentException("Weight must be non-negative");
        }
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public boolean requiresShipping() {
        return true;
    }
}

class SimpleProduct extends Product {
    public SimpleProduct(String name, double price, int quantity, boolean expired) {
        super(name, price, quantity, expired);
    }

    public boolean requiresShipping() {
        return false;
    }
}

class Customer {
    private String name;
    private double balance;

    public Customer(String name, double balance) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be null or empty");
        }
        if (balance < 0) {
            throw new IllegalArgumentException("Balance must be non-negative");
        }
        this.name = name;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public double getBalance() {
        return balance;
    }

    public void deduct(double amount) {
        if (amount > balance) {
            throw new IllegalStateException("Insufficient balance for customer " + name);
        }
        balance -= amount;
    }
}

class Cart {
    private Map<Product, Integer> items;

    public Cart() {
        this.items = new HashMap<>();
    }

    public void add(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (quantity > product.getQuantity()) {
            throw new IllegalArgumentException("Quantity exceeds stock for " + product.getName());
        }
        items.merge(product, quantity, Integer::sum);
    }

    public Map<Product, Integer> getItems() {
        return new HashMap<>(items);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public double getSubtotal() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    public double getTotalWeight() {
        return items.entrySet().stream()
                .filter(entry -> entry.getKey().requiresShipping())
                .mapToDouble(entry -> entry.getKey() instanceof Shippable
                        ? ((Shippable) entry.getKey()).getWeight() * entry.getValue()
                        : 0)
                .sum();
    }
}

class CheckoutService {
    private static final double SHIPPING_RATE = 30.0;

    public static void checkout(Customer customer, Cart cart) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        if (cart == null) {
            throw new IllegalArgumentException("Cart cannot be null");
        }

        Map<Product, Integer> items = cart.getItems();

        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            if (product.isExpired()) {
                throw new IllegalStateException(product.getName() + " is expired");
            }
            if (quantity > product.getQuantity()) {
                throw new IllegalStateException(product.getName() + " out of stock");
            }
        }

        double subtotal = cart.getSubtotal();
        double totalWeight = cart.getTotalWeight();
        double shipping = totalWeight > 0 ? SHIPPING_RATE : 0;
        double total = subtotal + shipping;
        customer.deduct(total);

        System.out.println("** Shipment notice **");
        if (items.isEmpty()) {
            System.out.println("No items in cart");
        } else {
            boolean hasShippable = false;
            for (Map.Entry<Product, Integer> entry : items.entrySet()) {
                Product product = entry.getKey();
                if (product.requiresShipping() && product instanceof Shippable) {
                    hasShippable = true;
                    int quantity = entry.getValue();
                    double weight = ((Shippable) product).getWeight() * quantity;
                    System.out.printf("%dx %-12s %.0fg%n", quantity, product.getName(), weight * 1000);
                }
            }
            if (!hasShippable) {
                System.out.println("No items in cart");
            } else {
                System.out.printf("Total package weight %.1f kg%n", totalWeight);
            }
        }

        System.out.println("\n** Checkout receipt **");
        if (items.isEmpty()) {
            System.out.println("No items to checkout");
            System.out.println("Subtotal 0");
            System.out.println("Shipping 0");
            System.out.println("Amount 0");
            
        } else {
            for (Map.Entry<Product, Integer> entry : items.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                System.out.printf("%dx %-12s %.0f%n", quantity, product.getName(), product.getPrice() * quantity);
            }
            System.out.printf("Subtotal %.0f%n", subtotal);
            System.out.printf("Shipping %.0f%n", shipping);
            System.out.printf("Amount %.0f%n", total);
        }
        

        for (Map.Entry<Product, Integer> entry : items.entrySet()) {
            entry.getKey().reduceQuantity(entry.getValue());
        }
    }
}

public class FawryChallenge {
    public static void main(String[] args) {
        try {
            Product cheese = new ShippableProduct("Cheese", 100, 10, false, 0.2);
            Product biscuits = new ShippableProduct("Biscuits", 150, 5, false, 0.7);
            Product scratchCard = new SimpleProduct("Scratch Card", 50, 20, false);

            Customer customer = new Customer("Ali", 1000);

            Cart cart = new Cart();
            cart.add(cheese, 2);
            cart.add(biscuits, 1);
            cart.add(scratchCard, 1);

            CheckoutService.checkout(customer, cart);

            System.out.println("\nTesting empty cart:");
            Cart emptyCart = new Cart();
            CheckoutService.checkout(customer, emptyCart);

            System.out.println("\nTesting expired product:");
            Product expiredCheese = new ShippableProduct("Expired Cheese", 100, 10, true, 0.2);
            Cart cartWithExpired = new Cart();
            try {
                cartWithExpired.add(expiredCheese, 1);
                CheckoutService.checkout(customer, cartWithExpired);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }

            System.out.println("\nTesting out of stock:");
            Product milk = new ShippableProduct("Milk", 50, 1, false, 0.1);
            Cart cartOutOfStock = new Cart();
            try {
                cartOutOfStock.add(milk, 2);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }

            System.out.println("\nTesting invalid inputs:");
            try {
                cart.add(null, 1);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
            try {
                cart.add(cheese, 0);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
            try {
                CheckoutService.checkout(null, cart);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
            try {
                Product invalidProduct = new ShippableProduct("", 100, 10, false, 0.2);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
            try {
                Product negativePrice = new ShippableProduct("Invalid", -100, 10, false, 0.2);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
            try {
                Product negativeWeight = new ShippableProduct("Invalid", 100, 10, false, -0.2);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
            try {
                Customer invalidCustomer = new Customer("", 1000);
            } catch (Exception e) {
                System.out.println("Caught expected error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}