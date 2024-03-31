package dictionary;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;

public class Message {
    // these maximums should be the same as in the database
    final static int MAX_MEANING_LENGTH = 1000;
    final static int MAX_WORD_LENGTH = 100;

    // Constants for the different types of messages
    final static String RESPONSE_SUFFIX = "-response";
    final static String QUERY = "query";
    final static String QUERY_RESPONSE = QUERY + RESPONSE_SUFFIX;
    final static String ADD = "add";
    final static String ADD_RESPONSE = ADD + RESPONSE_SUFFIX;
    final static String REMOVE_WORD = "remove-word";
    final static String REMOVE_WORD_RESPONSE = REMOVE_WORD + RESPONSE_SUFFIX;
    final static String REMOVE_MEANING = "remove-meaning";
    final static String REMOVE_MEANING_RESPONSE = REMOVE_MEANING + RESPONSE_SUFFIX;
    final static String UPDATE = "update";
    final static String UPDATE_RESPONSE = UPDATE + RESPONSE_SUFFIX;

    // these would be cool to implement but aren't in the assignment guidelines
    // and might not be possible to implement before the deadline TODO: remove
    // final static String VIEW_LOG = "view-log";
    // final static String VIEW_LOG_RESPONSE = VIEW_LOG + RESPONSE_SUFFIX;
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
    final static String ERROR_MISSING_COMMAND_MSG = "Missing command";
    final static String ERROR_DATABASE_FAILURE_MSG = "Database failure";
    final static String ERROR_MULTIPLE_MEANING_MSG = "Multiple meanings sent";
    final static String ERROR_INVALID_MEANING_MSG = "Invalid meaning. Must be at least 1 and less than 1000 characters";
    final static String ERROR_INVALID_WORD_MSG = "Invalid word. Must be at least 1 and less than 100 characters";
    final static String ERROR_MEANING_NOT_FOUND_MSG = "Meaning not found";

    // Instance variables
    private String command;
    private String word;
    private String meaning;
    private ArrayList<String> meanings;
    private String error;
    private Boolean success;
    private Boolean valid;

    // Constructor
    public Message() {
        this.command = "";
        this.word = "";
        this.meaning = "";
        this.meanings = new ArrayList<String>();
        this.error = null;
        this.success = false;
        this.valid = false;
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

    public Boolean isValid() {
        // at the point this is called the success will be false
        if (this.command == null || this.command.isEmpty()) {
            this.error = ERROR_MISSING_COMMAND_MSG;
            return false;
        }
        if (this.word == null || this.word.isEmpty()) {
            this.error = ERROR_MISSING_WORD_MSG;
            return false;
        }
        if (this.word.length() > MAX_WORD_LENGTH) {
            this.error = ERROR_INVALID_WORD_MSG;
            return false;
        }
        if (this.command == ADD || this.command == UPDATE || 
            this.command == REMOVE_MEANING) {
            if (this.meaning == null || this.meaning.isEmpty()) {
                this.error = ERROR_MISSING_MEANING_MSG;
                return false;
            }
            if (this.meaning.length() > MAX_MEANING_LENGTH) {
                this.error = ERROR_INVALID_MEANING_MSG;
                return false;
            }
        }
        return true;
    }

    public Message makeResponse() {
        Message response = new Message();
        response.setCommand(this.command + RESPONSE_SUFFIX);
        response.setWord(this.word);
        response.setMeaning(this.meaning);
        response.setMeanings(this.meanings);
        response.setSuccess(this.success);
        response.setError(this.error);
        return response;
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
    public String getMeaning() {
        return meaning;
    }
    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }
    public ArrayList<String> getMeanings() {
        return meanings;
    }
    public void addMeaning(String meaning) {
        this.meanings.add(meaning);
    }
    public void removeMeaning(String meaning) {
        this.meanings.remove(meaning);
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
