// Dictionary Server SQL handler class

package dictionary;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import io.github.cdimascio.dotenv.Dotenv;

public class MySqlHandler {
    // Database connection details
    private static final Dotenv dotenv = Dotenv.load();
    private static final String MYSQL_USERNAME = dotenv.get("MYSQL_USERNAME");
    private static final String MYSQL_PASSWORD = dotenv.get("MYSQL_PASSWORD");
    private static final String MYSQL_DATABASE = dotenv.get("MYSQL_DATABASE");
    private static final String MYSQL_URL = dotenv.get("MYSQL_URL"); 

    // Database connection
    private Connection connection;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public Boolean lookupWord(Message message, Message response) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("SELECT * FROM dictionary WHERE word = ?");
            preparedStatement.setString(1, message.getWord());
            resultSet = preparedStatement.executeQuery();

            // Process the results
            ArrayList<String> meanings = new ArrayList<String>();
            while (resultSet.next()) {
                meanings.add(resultSet.getString("meaning"));
            }

            // check if the word was found
            if (meanings.size() == 0) {
                response.setError(Message.ERROR_WORD_NOT_FOUND_MSG);
                response.setSuccess(false);
            } else {
                response.setSuccess(true);
                response.setMeanings(meanings);
            }

            // Close the connections
            close_mysql();

            return response.getSuccess();
        }
        catch (SQLException e) {
            // This includes SQLTimeoutException
            e.printStackTrace();
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            return false;
        }
        catch (Exception e) {
            // Includes: LinkageError, ExceptionInInitializerError, ClassNotFoundException
            // which may occur if driver is not found or loaded
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            e.printStackTrace();
            return false;
        }
    }

    public boolean addWord(Message message, Message response) {
        // used for both add word and update but need to check command validity first
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String word = message.getWord();
            String newMeaning = message.getMeaning();

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);

            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("INSERT INTO dictionary (word, meaning) VALUES (?, ?)");
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, newMeaning);
            preparedStatement.executeUpdate();

            close_mysql();

            // operation was successful so add meaning to response for client display
            response.setSuccess(true);
            response.addMeaning(newMeaning);
            return true;
        } 
        catch (IndexOutOfBoundsException e) {
            // meaning should be single element in array
            response.setError(Message.ERROR_MISSING_MEANING_MSG);
            response.setSuccess(false);
            e.printStackTrace();
            return false;
        }
        catch (SQLException e) {
            // This includes SQLTimeoutException
            e.printStackTrace();
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            return false;
        }
        catch (Exception e) {
            // Includes: LinkageError, ExceptionInInitializerError, ClassNotFoundException
            // which may occur if driver is not found or loaded
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeWord(Message message, Message response) {
        // this function is idempotent, checking for word being in the database happens elsewhere
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String word = message.getWord();

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("DELETE FROM dictionary WHERE word = ?");
            preparedStatement.setString(1, word);
            preparedStatement.executeUpdate();
            close_mysql();

            // success
            response.setSuccess(true);

            return true;
        }
        catch (SQLException e) {
            // This includes SQLTimeoutException
            e.printStackTrace();
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            return false;
        }
        catch (Exception e) {
            // Includes: LinkageError, ExceptionInInitializerError, ClassNotFoundException
            // which may occur if driver is not found or loaded
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            e.printStackTrace();
            return false;
        }
    }

    public boolean removeMeaning(Message message, Message response) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String word = message.getWord();
            String meaning = message.getMeaning();

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("DELETE FROM dictionary WHERE word = ? AND meaning = ?");
            preparedStatement.setString(1, word);
            preparedStatement.setString(2, meaning);
            preparedStatement.executeUpdate();
            close_mysql();
            
            // success and remove meaning from response
            response.setSuccess(true);
            response.removeMeaning(meaning);

            return true;
        }
        catch (SQLException e) {
            // This includes SQLTimeoutException
            e.printStackTrace();
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            return false;
        }
        catch (Exception e) {
            // Includes: LinkageError, ExceptionInInitializerError, ClassNotFoundException
            // which may occur if driver is not found or loaded
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            e.printStackTrace();
            return false;
        }
    }

    public boolean editMeaning(Message message, Message response) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            String word = message.getWord();
            String oldMeaning = message.getOldMeaning();
            String newMeaning = message.getMeaning();

            // Connect to the database
            connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USERNAME, MYSQL_PASSWORD);
            
            // Prepare and execute the SQL query
            preparedStatement = connection.prepareStatement("UPDATE dictionary SET meaning = ? WHERE word = ? AND meaning = ?");
            preparedStatement.setString(1, newMeaning);
            preparedStatement.setString(2, word);
            preparedStatement.setString(3, oldMeaning);
            preparedStatement.executeUpdate();
            close_mysql();

            // success and update meaning in response
            response.setSuccess(true);
            response.changeMeaning(oldMeaning, newMeaning);

            return true;
        }
        catch (SQLException e) {
            // This includes SQLTimeoutException
            e.printStackTrace();
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            return false;
        }
        catch (Exception e) {
            // Includes: LinkageError, ExceptionInInitializerError, ClassNotFoundException
            // which may occur if driver is not found or loaded
            response.setError(Message.ERROR_DATABASE_FAILURE_MSG);
            response.setSuccess(false);
            e.printStackTrace();
            return false;
        }
    }

    private void close_mysql() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }

            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (connection != null) {
                connection.close();
            }
        }
        catch (Exception e) {
            System.out.println("Error in closing mySQL connection: " + e);
        }
    }
}
