# Bus Ticketing System

This is a **Java Swing-based Bus Ticketing System** that uses **MySQL** for data storage. The project can be run using **NetBeans IDE** and **XAMPP** for MySQL management.

---

## Prerequisites

Before setting up the project, ensure you have:

- [Java JDK](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)
- [NetBeans IDE](https://netbeans.apache.org/download/)
- [XAMPP](https://www.apachefriends.org/index.html) (for Apache and MySQL)
- MySQL Database (via XAMPP’s phpMyAdmin)
- MySQL Connector/J (JDBC driver for MySQL)

---

## Project Setup

### 1. Download or Clone Project

1. Clone the repository or download the project ZIP.
2. Extract the project folder to a preferred location.

### 2. Open Project in NetBeans

1. Launch **NetBeans IDE**.
2. Go to **File → Open Project**.
3. Navigate to the extracted project folder.
4. Select the project and click **Open Project**.

### 3. Setup Database

1. Open **XAMPP Control Panel**.
2. Start **Apache** and **MySQL**.
3. Open **phpMyAdmin** by visiting [http://localhost/phpmyadmin](http://localhost/phpmyadmin).
4. Create a new database:





5. Import the database structure if a `.sql` file is provided:

   - Click **Import → Choose File → Go**.

6. If no `.sql` file is provided, create the necessary tables manually. Example:

```sql
CREATE DATABASE bus_ticketing_db;
USE bus_ticketing_db;

CREATE TABLE tickets (
    ticket_id INT AUTO_INCREMENT PRIMARY KEY,
    passenger_name VARCHAR(100),
    passenger_type ENUM('Regular', 'Student', 'Senior'),
    bus_number VARCHAR(20),
    destination VARCHAR(100),
    price DOUBLE,
    discount DOUBLE,
    total DOUBLE,
    date_time DATETIME DEFAULT CURRENT_TIMESTAMP
);



---

5. Add JDBC Driver to Project

Download MySQL Connector/J from MySQL Connector/J Download
.

In NetBeans:

Right-click your project → Properties → Libraries → Add JAR/Folder.

Select the downloaded MySQL Connector/J JAR file.

6. Run Project

Right-click the project → Run.

The Bus Ticketing System GUI should appear.

