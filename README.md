

````markdown
# 🛒 Fawry Rise Journey – Full Stack Internship Challenge

## 📌 Overview

This is a Java-based implementation of a simplified **e-commerce system** developed for the **Fawry Rise Journey – Full Stack Development Internship Challenge**.

The system allows customers to:
- Add products to a shopping cart
- Perform a checkout with proper validation
- Handle expired items, stock limits, and shipping
- View printed checkout and shipping details as per challenge specification

---

## ✅ Features

### 🔸 Product System
- Each product includes:
  - `name`
  - `price`
  - `quantity`
  - `expired` status
- Products may:
  - Expire (e.g., Cheese, Biscuits)
  - Require shipping (e.g., Cheese, TV) with `weight`
  - Or neither (e.g., Scratch Cards)

### 🔸 Cart Management
- Users can:
  - Add products with specific quantity (must not exceed stock)
  - Calculate subtotal and shipping weight
- Validates:
  - Product existence
  - Quantity > 0
  - Stock availability

### 🔸 Checkout Process
- Displays:
  - Shipment notice: Shippable items and their weights
  - Checkout receipt: Quantity, product names, total prices
- Verifies:
  - Cart is not empty
  - Products are not expired
  - Customer has enough balance
- Performs:
  - Stock deduction
  - Balance deduction

---

## 🧪 Sample Usage

```java
Product cheese = new ShippableProduct("Cheese", 100, 10, false, 0.2);
Product biscuits = new ShippableProduct("Biscuits", 150, 5, false, 0.7);
Product scratchCard = new SimpleProduct("Scratch Card", 50, 20, false);

Customer customer = new Customer("Ali", 1000);

Cart cart = new Cart();
cart.add(cheese, 2);
cart.add(biscuits, 1);
cart.add(scratchCard, 1);

CheckoutService.checkout(customer, cart);
````

### ✅ Console Output (Expected)

```
** Shipment notice **
2x Cheese        
400g
1x Biscuits      
700g
Total package weight 1.1 kg

** Checkout receipt **
2x Cheese        
200
1x Biscuits      
150
1x Scratch Card  
50
Subtotal 400
Shipping 30
Amount 430
```

---


## 🛠️ How to Run

1. Open the project in a Java IDE (IntelliJ, Eclipse, etc.) or compile via terminal:

```bash
javac FawryChallenge.java
java FawryChallenge
```

2. The program will print test case results to the console.

---

## 🔐 Assumptions

* Shipping fee is fixed at **30 units** if any item in the cart requires shipping.
* Weight is handled in kilograms but printed in **grams**.
* Prices are printed without decimal places (rounded).
* If the cart is empty or any product is expired/out of stock, an error is thrown.

---

## ✅ Implemented Validations

* ✔️ Product name, price, quantity, weight must be valid
* ✔️ Customer must have non-negative balance
* ✔️ No checkout allowed if:

  * Cart is empty
  * Product is expired
  * Stock is insufficient
  * Balance is insufficient

---

