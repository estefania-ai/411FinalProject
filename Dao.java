import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JOptionPane;

//import jdk.javadoc.internal.doclets.formats.html.resources.standard;

public class Dao {
	// instance fields
	static Connection connect = null;
	Statement statement = null;

	// constructor
	public Dao() {
	  try{
      Class.forName("com.mysql.cj.jdbc.Driver");
    } catch(Exception e){
      e.printStackTrace();
    }
	}

	public Connection getConnection() {
		// Setup the connection with the DB
		try {
      Class.forName("com.mysql.cj.jdbc.Driver");
			connect = DriverManager
					.getConnection("jdbc:mysql://www.papademas.net:3307/tickets?autoReconnect=true&useSSL=false"
							+ "&user=fp411&password=411");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connect;
	}

	// CRUD implementation

	public void createTables() {
		// variables for SQL Query table creations
		final String createTicketsTable = "CREATE TABLE este_tickets(ticket_id INT AUTO_INCREMENT PRIMARY KEY, ticket_issuer VARCHAR(30), ticket_description VARCHAR(200), start_date DATE, end_date DATE, ticket_priority VARCHAR(6))";
		final String createUsersTable = "CREATE TABLE este_users(uid INT AUTO_INCREMENT PRIMARY KEY, uname VARCHAR(30), upass VARCHAR(30), admin int)";

		try {

			// execute queries to create tables

			statement = getConnection().createStatement();

			statement.executeUpdate(createTicketsTable);
			statement.executeUpdate(createUsersTable);
			System.out.println("Created tables in given database...");

			// end create table
			// close connection/statement object
			statement.close();
			connect.close();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		// add users to user table
    System.out.println("adding users");
		addUsers();
	}

	public void addUsers() {
		// add list of users from userlist.csv file to users table

		// variables for SQL Query inserts
		String sql;

		Statement statement;
		BufferedReader br;
		List<List<String>> array = new ArrayList<>(); // list to hold (rows & cols)

		// read data from file
		try {
			br = new BufferedReader(new FileReader(new File("./userlist.csv")));

			String line;
			while ((line = br.readLine()) != null) {
				array.add(Arrays.asList(line.split(",")));
        System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println("There was a problem loading the file");
		}

		try {

			// Setup the connection with the DB

			statement = getConnection().createStatement();

			// create loop to grab each array index containing a list of values
			// and PASS (insert) that data into your User table
			for (List<String> rowData : array) {

				sql = "insert into este_users(uname,upass,admin) " + "values('" + rowData.get(0) + "'," + " '"
						+ rowData.get(1) + "','" + rowData.get(2) + "');";
				statement.executeUpdate(sql);
			}
			System.out.println("ts completed in the given database...");

			// close statement object
			statement.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public int insertRecords(String ticketName, String ticketDesc, String priority) {
		int id = 0;
		try {
			statement = getConnection().createStatement();
			statement.executeUpdate("Insert into este_tickets" + "(ticket_issuer, ticket_description, start_date, ticket_priority) values(" 
      + " '" + ticketName + "','" + ticketDesc + "','" + java.time.LocalDate.now() + "','" + priority + "')", Statement.RETURN_GENERATED_KEYS);

			// retrieve ticket id number newly auto generated upon record insertion
			ResultSet resultSet = null;
			resultSet = statement.getGeneratedKeys();
			if (resultSet.next()) {
				// retrieve first field in table
				id = resultSet.getInt(1);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return id;

	}

	public ResultSet readRecords() {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM este_tickets");
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
  public ResultSet readRecords(int id) {
		ResultSet results = null;
		try {
			statement = connect.createStatement();
			results = statement.executeQuery("SELECT * FROM este_users WHERE uid  = " + id);
			//connect.close();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}
		return results;
	}
	// continue coding for updateRecords implementation
  public void updateRecords(int id, String updatedDesc){
    System.out.println("Creating update statement...");
    try {
      statement = connect.createStatement();
      String sql = "UPDATE este_tickets " +
                  "SET ticket_description = ' " + updatedDesc + " '  WHERE ticket_id  = " + id;
      System.out.println("Update query is "+ sql);
      statement.executeUpdate(sql);
    }catch(SQLException e2){
      e2.printStackTrace();
    }
  }

  public void updateRecords(int id, LocalDate ld){
    System.out.println("Creating update statement...");
    try {
      statement = connect.createStatement();
      String sql = "UPDATE este_tickets " +
                  "SET end_date = ' " + ld + " '  WHERE ticket_id  = " + id;
      System.out.println("Update query is "+ sql);
      statement.executeUpdate(sql);
    }catch(SQLException e2){
      e2.printStackTrace();
    }
  }
	// continue coding for deleteRecords implementation
  public void deleteRecords(int id){
    // Execute delete  query
      System.out.println("Creating statement...");
      try{
        statement = connect.createStatement();
      }catch(SQLException e){
        e.printStackTrace();
      }
      String sql = "DELETE FROM este_tickets  " +
                   "WHERE ticket_id = " + id;
      System.out.println("Delete query is "+ sql);
     int response = JOptionPane.showConfirmDialog(null, " Delete ticket # " + id + "?",
                               "Confirm",  JOptionPane.YES_NO_OPTION, 
                               JOptionPane.QUESTION_MESSAGE);
     if (response == JOptionPane.NO_OPTION) {
       System.out.println("No record deleted");
    } else if (response == JOptionPane.YES_OPTION) {
      try{
        statement.executeUpdate(sql);
      }catch(SQLException e){
        e.printStackTrace();
      }
      System.out.println("Record deleted");
    } else if (response == JOptionPane.CLOSED_OPTION) {
      System.out.println("Request cancelled");
    }
  }
}
