import java.sql.*; 
import java.util.Scanner; 
 
public class RealEstateSystem { 
 
    // Database connection details     private static final String DB_URL = "jdbc:mysql://localhost:3306/RealEstate";     private static final String USER = "root"; 
    private static final String PASSWORD = "Samurana@4554"; 
 
    public static void main(String[] args) { 
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, 
PASSWORD)) { 
            System.out.println("Connected to the database!"); 
 
            Scanner scanner = new Scanner(System.in);             int choice; 
             do { 
                System.out.println("\n--- Real Estate System ---"); 
                System.out.println("1. Add User"); 
                System.out.println("2. Add Property Listing"); 
                System.out.println("3. View Properties"); 
                System.out.println("4. Add Inquiry"); 
                System.out.println("5. View Inquiries"); 
                System.out.println("6. Exit"); 
                System.out.print("Enter your choice: ");                 choice = scanner.nextInt(); 
                scanner.nextLine(); // Consume newline 
 
                switch (choice) {                     case 1: 
                        addUser(connection, scanner); 
                        break;                     case 2: 
                        addProperty(connection, scanner);                         break;                     case 3: 
                        viewProperties(connection);                         break;                     case 4: 
                        addInquiry(connection, scanner);                         break;                     case 5: 
                        viewInquiries(connection);                         break;                     case 6: 
                        System.out.println("Exiting...");                         break;                     default: 
                        System.out.println("Invalid choice. Please try again."); 
                } 
            } while (choice != 6); 
 
        } catch (SQLException e) {             e.printStackTrace(); 
        }     }  
    // Add a user (Agent or Customer) 
    private static void addUser(Connection connection, Scanner scanner) throws SQLException { 
        System.out.print("\nEnter Name: "); 
        String name = scanner.nextLine(); 
        System.out.print("Enter Email: "); 
        String email = scanner.nextLine(); 
        System.out.print("Enter User Type (Agent/Customer): "); 
        String userType = scanner.nextLine(); 
 
        String query = "INSERT INTO Users (Name, Email, UserType) VALUES (?, ?, ?)";         try (PreparedStatement pstmt = connection.prepareStatement(query)) {             pstmt.setString(1, name);             pstmt.setString(2, email);             pstmt.setString(3, userType);             pstmt.executeUpdate(); 
            System.out.println("User added successfully!"); 
        }     } 
 
    // Add a property listing 
    private static void addProperty(Connection connection, Scanner scanner) throws SQLException { 
        System.out.print("\nEnter Property Title: "); 
        String title = scanner.nextLine(); 
        System.out.print("Enter Description: "); 
        String description = scanner.nextLine();         System.out.print("Enter Price: ");         double price = scanner.nextDouble();         scanner.nextLine(); // Consume newline         System.out.print("Enter Location: "); 
        String location = scanner.nextLine();         System.out.print("Enter Agent ID: ");         int agentID = scanner.nextInt();         scanner.nextLine(); // Consume newline 
 
        // Check if Agent exists 
        String checkAgentQuery = "SELECT * FROM Users WHERE UserID = ? AND UserType = 'Agent'"; 
        try (PreparedStatement checkStmt = connection.prepareStatement(checkAgentQuery)) 
{ 
            checkStmt.setInt(1, agentID);             try (ResultSet rs = checkStmt.executeQuery()) {                 if (!rs.next()) { 
                    System.out.println("Error: Agent ID does not exist or is not an Agent.");                     return; 
                } 
            }         } 
 
        String query = "INSERT INTO Properties (Title, Description, Price, Location, AgentID) VALUES (?, ?, ?, ?, ?)";         try (PreparedStatement pstmt = connection.prepareStatement(query)) {             pstmt.setString(1, title);             pstmt.setString(2, description);             pstmt.setDouble(3, price);             pstmt.setString(4, location);             pstmt.setInt(5, agentID);             pstmt.executeUpdate(); 
            System.out.println("Property listing added successfully!"); 
        }     } 
 
    // View all property listings 
    private static void viewProperties(Connection connection) throws SQLException {         String query = "SELECT * FROM Properties";         try (Statement stmt = connection.createStatement();              ResultSet rs = stmt.executeQuery(query)) { 
 
            System.out.println("\n--- Property Listings ---");             while (rs.next()) { 
                System.out.printf("ID: %d, Title: %s, Description: %s, Price: %.2f, Location: %s, AgentID: %d\n", 
                        rs.getInt("PropertyID"), rs.getString("Title"), rs.getString("Description"),                         rs.getDouble("Price"), rs.getString("Location"), rs.getInt("AgentID")); 
            } 
        }     }  
    // Add an inquiry for a property 
    private static void addInquiry(Connection connection, Scanner scanner) throws SQLException { 
        System.out.print("\nEnter Property ID: ");         int propertyID = scanner.nextInt();         System.out.print("Enter Customer ID: ");         int customerID = scanner.nextInt();         scanner.nextLine(); // Consume newline 
        System.out.print("Enter Inquiry Message: ");         String message = scanner.nextLine(); 
 
        // Check if Property and Customer exist 
        String checkPropertyQuery = "SELECT * FROM Properties WHERE PropertyID = ?"; 
        String checkCustomerQuery = "SELECT * FROM Users WHERE UserID = ? AND UserType = 'Customer'"; 
 
        try (PreparedStatement checkPropertyStmt = connection.prepareStatement(checkPropertyQuery);              PreparedStatement checkCustomerStmt = connection.prepareStatement(checkCustomerQuery)) { 
 
            checkPropertyStmt.setInt(1, propertyID);             checkCustomerStmt.setInt(1, customerID); 
 
            try (ResultSet propertyRs = checkPropertyStmt.executeQuery();                  ResultSet customerRs = checkCustomerStmt.executeQuery()) { 
 
                if (!propertyRs.next()) { 
                    System.out.println("Error: Property ID does not exist.");                     return; 
                } 
                if (!customerRs.next()) { 
                    System.out.println("Error: Customer ID does not exist.");                     return; 
                } 
            }         } 
 
        String query = "INSERT INTO Inquiries (PropertyID, CustomerID, Message) VALUES (?, ?, ?)";         try (PreparedStatement pstmt = connection.prepareStatement(query)) {             pstmt.setInt(1, propertyID);             pstmt.setInt(2, customerID);             pstmt.setString(3, message);             pstmt.executeUpdate(); 
            System.out.println("Inquiry added successfully!"); 
        }     } 
 
    // View all inquiries 
    private static void viewInquiries(Connection connection) throws SQLException { 
        String query = "SELECT i.InquiryID, p.Title AS PropertyTitle, u.Name AS 
CustomerName, i.Message " + 
                "FROM Inquiries i " + 
                "JOIN Properties p ON i.PropertyID = p.PropertyID " + 
                "JOIN Users u ON i.CustomerID = u.UserID"; 
 
        try (Statement stmt = connection.createStatement();              ResultSet rs = stmt.executeQuery(query)) { 
 
            System.out.println("\n--- Inquiries ---");             while (rs.next()) { 
                System.out.printf("Inquiry ID: %d, Property: %s, Customer: %s, Message: %s\n",                         rs.getInt("InquiryID"), rs.getString("PropertyTitle"),                         rs.getString("CustomerName"), rs.getString("Message")); 
            } 
        } 
    } } 
