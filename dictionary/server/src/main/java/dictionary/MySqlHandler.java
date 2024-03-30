package dictionary;

import java.sql.Connection;
import java.sql.DriverManager;
//import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class MySqlHandler {
    // Database connection details
    private static final String MYSQL_USERNAME = "server_user";
    private static final String MYSQL_PASSWORD = "abcde";
    private static final String MYSQL_DATABASE = "ds_dictionary";
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/" + MYSQL_DATABASE;

    // Database connection
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public ArrayList<String> lookupWord(String word) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("SELECT * FROM dictionary WHERE word = ?");
            preparedStatement.setString(1, word);
            resultSet = preparedStatement.executeQuery();

            // Process the results
            ArrayList<String> meanings = new ArrayList<String>();
            while (resultSet.next()) {
                meanings.add(resultSet.getString("meaning"));
            }

            // Close the connections
            close_mysql();

            return meanings;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addWord(String word, String meaning) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("INSERT INTO dictionary (word, meaning) VALUES (?, ?)");
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, meaning);
            preparedStatement.executeUpdate();

            close_mysql();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeWord(String word) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("DELETE FROM dictionary WHERE word = ?");
            preparedStatement.setString(1, word);
            preparedStatement.executeUpdate();
            close_mysql();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeMeaning(String word, String meaning) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("DELETE FROM dictionary WHERE word = ? AND meaning = ?");
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, meaning);
            preparedStatement.executeUpdate();
            close_mysql();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void close_mysql() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (statement != null) {
                statement.close();
            }

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        } catch (Exception e) {
            System.out.println("Error in closing mySQL connection: " + e);
        }
    }
}
