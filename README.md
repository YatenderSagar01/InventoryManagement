# Inventory Management System

A Java console application that manages products in a MySQL database using JDBC and prepared statements.

## Features

1. Insert three product records and report duplicate product IDs.
2. List products in a selected category with stock below 20, ordered by price from high to low.
3. Add stock to an existing product.
4. Find a product, update its price when the new price is positive, and display its details.
5. Show the total inventory value and delete a product only when its quantity is zero.

## Requirements

- Java 21
- Maven
- MySQL Server

The MySQL Connector/J dependency is declared in `pom.xml` and will be resolved by Maven.

## Database setup

Run the SQL in [`sql/schema.sql`](sql/schema.sql) to create the `retail_db` database and `Product` table.

## Database configuration

The application reads connection settings from environment variables. It defaults to `jdbc:mysql://localhost:3306/retail_db` and user `root`; set the database password before starting the application:

- `DB_PASSWORD` — required; your local MySQL password
- `DB_USER` — optional; defaults to `root`
- `DB_URL` — optional; defaults to `jdbc:mysql://localhost:3306/retail_db`

In IntelliJ IDEA, open **Run | Edit Configurations**, choose the `InventoryApp` run configuration, and add the variables under **Environment variables**. Keep actual passwords out of source files and version control.

## Build and run

Open the project as a Maven project in IntelliJ IDEA, let Maven load the dependencies, configure the environment variables, then run `com.kiet.inventorymanagement.InventoryApp`.

You can compile the project from a terminal with:

```shell
mvn clean package
```
