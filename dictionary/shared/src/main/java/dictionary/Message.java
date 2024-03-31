package dictionary;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

public class Message {
    // Constants for the different types of messages
    final static String QUERY = "query";
    final static String QUERY_RESPONSE = "query-response";
    final static String ADD = "add";
    final static String ADD_RESPONSE = "add-response";
    final static String REMOVE_WORD = "remove-word";
    final static String REMOVE_WORD_RESPONSE = "remove-word-response";
    final static String REMOVE_MEANING = "remove-meaning";
    final static String REMOVE_MEANING_RESPONSE = "remove-meaning-response";
    final static String UPDATE = "update";
    final static String UPDATE_RESPONSE = "update-response";
    final static String VIEW_LOG = "view-log";
    final static String VIEW_LOG_RESPONSE = "view-log-response";

    // these would be cool to implement but aren't in the assignment guidelines
    // and might not be possible to implement before the deadline TODO: remove
    // final static String BAN_CONTRIB = "ban-contributor";
    // final static String BAN_CONTRIB_RESPONSE = "ban-contributor-response";
    // final static String UNBAN_CONTRIB = "unban-contributor";
    // final static String UNBAN_CONTRIB_RESPONSE = "unban-contributor-response";
    // final static String REGISTER = "register";
    // final static String REGISTER_RESPONSE = "register-response";
    // final static String LOGIN = "login";
    // final static String LOGIN_RESPONSE = "login-response";
    // final static String LOGOUT = "logout";
    // final static String LOGOUT_RESPONSE = "logout-response";
    // final static String SHUTDOWN = "shutdown";
    // final static String SHUTDOWN_RESPONSE = "shutdown-response";

    // Error messages
    final static String ERROR_INVALID_COMMAND_MSG = "Invalid command";
    final static String ERROR_WORD_NOT_FOUND_MSG = "Word not found";
    final static String ERROR_WORD_EXISTS_MSG = "Word already exists";
    final static String ERROR_MEANING_EXISTS_MSG = "Meaning already exists";
    final static String ERROR_MISSING_MEANING_MSG = "Missing meaning";
    final static String ERROR_MISSING_WORD_MSG = "Missing word";


    // Instance variables
    private String command;
    private String word;
    private ArrayList<String> meanings;
    private String error;
    private Boolean success;

    // Constructor
    public Message() {
        this.command = "";
        this.word = "";
        this.meanings = new ArrayList<String>();
        this.error = null;
        this.success = false;
    }

    // Convert from JSON string to a message object
    public static Message fromJson(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(json, Message.class);
        } 
        catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    // Convert the message object to a JSON string
    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
    
    // Getters and setters
    public String getCommand() {
        return command;
    }
    public void setCommand(String command) {
        this.command = command;
    }
    public String getWord() {
        return word;
    }
    public void setWord(String word) {
        this.word = word;
    }
    public ArrayList<String> getMeanings() {
        return meanings;
    }
    public void setMeanings(ArrayList<String> meanings) {
        this.meanings = meanings;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
    public Boolean getSuccess() {
        return success;
    }
    public void setSuccess(Boolean success) {
        this.success = success;
    }
}
