
package com.kiet.inventorymanagement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;
import java.sql.ResultSet;

public class InventoryApp {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        int choice;

        do {
            System.out.println("\n===== INVENTORY MANAGEMENT SYSTEM =====");
            System.out.println("1. Insert Product Records");
            System.out.println("2. Retrieve Products by Category");
            System.out.println("3. Update Product Stock");
            System.out.println("4. Search and Modify Product Price");
            System.out.println("5. Display Inventory Value and Delete Product");
            System.out.println("6. Exit");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(sc.nextLine());

                switch (choice) {

                    case 1:
                        insertProducts(sc);
                        break;

                    case 2:
                        retrieveProducts(sc);
                        break;

                    case 3:
                        updateStock(sc);
                        break;

                    case 4:
                        modifyProductPrice(sc);
                        break;

                    case 5:
                        inventoryValueAndDelete(sc);
                        break;
                    case 6:
                        System.out.println("Exiting application...");
                        break;

                    default:
                        System.out.println("Invalid choice! Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = 0;
            }

        } while (choice != 6);

        sc.close();
    }

    // PART A: INSERT THREE PRODUCT RECORDS

    public static void insertProducts(Scanner sc) {

        String sql = "INSERT INTO Product VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {


            for (int i = 1; i <= 3; i++) {

                System.out.println("\nEnter details for Product " + i);

                System.out.print("Product ID: ");
                int id = Integer.parseInt(sc.nextLine());

                System.out.print("Product Name: ");
                String name = sc.nextLine();

                System.out.print("Category: ");
                String category = sc.nextLine();

                System.out.print("Quantity: ");
                int quantity = Integer.parseInt(sc.nextLine());

                System.out.print("Price: ");
                double price = Double.parseDouble(sc.nextLine());

                ps.setInt(1, id);
                ps.setString(2, name);
                ps.setString(3, category);
                ps.setInt(4, quantity);
                ps.setDouble(5, price);

                try {
                    int rows = ps.executeUpdate();

                    if (rows > 0) {
                        System.out.println("Product inserted successfully!");
                    }

                } catch (SQLException e) {

                    if ("23000".equals(e.getSQLState())) {
                        System.out.println(
                                "Duplicate Product ID! Record not inserted."
                        );
                    } else {
                        System.out.println("Insertion failed: " + e.getMessage());
                    }
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid numeric input!");

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    public static void retrieveProducts(Scanner sc) {

        String sql = "SELECT * FROM Product " +
                "WHERE category = ? " +
                "AND quantity < 20 " +
                "ORDER BY price DESC";

        System.out.print("Enter category: ");
        String category = sc.nextLine();

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setString(1, category);

            ResultSet rs = ps.executeQuery();

            System.out.println("\n===== PRODUCT DETAILS =====");

            boolean found = false;

            while (rs.next()) {

                found = true;

                System.out.println("Product ID: " + rs.getInt("product_id"));
                System.out.println("Product Name: " + rs.getString("product_name"));
                System.out.println("Category: " + rs.getString("category"));
                System.out.println("Quantity: " + rs.getInt("quantity"));
                System.out.println("Price: " + rs.getDouble("price"));

                System.out.println("----------------------------");
            }

            if (!found) {
                System.out.println("No matching products found.");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void updateStock(Scanner sc) {

        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";

        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Enter quantity to add: ");
        int addedQuantity = Integer.parseInt(sc.nextLine());

        if (addedQuantity <= 0) {
            System.out.println("Quantity to add must be greater than zero.");
            return;
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {


            ps.setInt(1, addedQuantity);
            ps.setInt(2, id);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("Product stock updated successfully!");
            } else {
                System.out.println("Product does not exist!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    public static void modifyProductPrice(Scanner sc) {

        System.out.print("Enter Product ID: ");
        int id = Integer.parseInt(sc.nextLine());

        System.out.print("Enter new price: ");
        double newPrice = Double.parseDouble(sc.nextLine());

        if (!Double.isFinite(newPrice) || newPrice <= 0) {
            System.out.println("Price must be greater than zero!");
            return;
        }

        String searchSQL =
                "SELECT * FROM Product WHERE product_id = ?";

        String updateSQL =
                "UPDATE Product SET price = ? WHERE product_id = ?";

        String displaySQL =
                "SELECT * FROM Product WHERE product_id = ?";

        try (Connection con = DBConnection.getConnection()) {


            // Step 1: Search for product

            try (PreparedStatement ps = con.prepareStatement(searchSQL)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        System.out.println("Product does not exist!");
                        return;
                    }

                    System.out.println("\nProduct Found!");
                    System.out.println("Product Name: "
                            + rs.getString("product_name"));
                    System.out.println("Old Price: "
                            + rs.getDouble("price"));
                }
            }

            // Step 2: Update product price

            try (PreparedStatement ps = con.prepareStatement(updateSQL)) {

                ps.setDouble(1, newPrice);
                ps.setInt(2, id);

                ps.executeUpdate();
            }

            System.out.println("\nPrice updated successfully!");

            // Step 3: Display updated product details

            try (PreparedStatement ps = con.prepareStatement(displaySQL)) {

                ps.setInt(1, id);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {

                        System.out.println("\n===== UPDATED PRODUCT =====");

                        System.out.println("Product ID: "
                                + rs.getInt("product_id"));

                        System.out.println("Product Name: "
                                + rs.getString("product_name"));

                        System.out.println("Category: "
                                + rs.getString("category"));

                        System.out.println("Quantity: "
                                + rs.getInt("quantity"));

                        System.out.println("New Price: "
                                + rs.getDouble("price"));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }


    public static void inventoryValueAndDelete(Scanner sc) {

        String totalSQL =
                "SELECT SUM(quantity * price) AS total_value FROM Product";

        String deleteSQL =
                "DELETE FROM Product WHERE product_id = ? AND quantity = 0";

        try (Connection con = DBConnection.getConnection()) {


            // Step 1: Calculate total inventory value

            try (PreparedStatement ps = con.prepareStatement(totalSQL);
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    double total = rs.getDouble("total_value");

                    System.out.println("\n===== INVENTORY VALUE =====");
                    System.out.println("Total Inventory Value: " + total);
                }
            }

            // Step 2: Accept Product ID

            System.out.print("\nEnter Product ID to delete: ");
            int id = Integer.parseInt(sc.nextLine());

            // Step 3: Delete only if quantity is zero

            try (PreparedStatement ps = con.prepareStatement(deleteSQL)) {

                ps.setInt(1, id);

                int rows = ps.executeUpdate();

                if (rows > 0) {
                    System.out.println("Product deleted successfully!");
                } else {
                    System.out.println(
                            "Product cannot be deleted! It either does not exist "
                                    + "or its quantity is not zero."
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());

        } catch (NumberFormatException e) {
            System.out.println("Invalid Product ID!");
        }
    }




}
