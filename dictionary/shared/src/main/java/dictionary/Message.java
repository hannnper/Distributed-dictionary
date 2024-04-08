// Message class for the dictionary application
// Han Perry 693878

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
    final static String EDIT_MEANING = "edit-meaning";
    final static String EDIT_MEANING_RESPONSE = EDIT_MEANING + RESPONSE_SUFFIX;

    // Error messages
    final static String ERROR_INVALID_COMMAND_MSG = "Invalid command";
    final static String ERROR_WORD_NOT_FOUND_MSG = "Word not found";
    final static String ERROR_WORD_EXISTS_MSG = "Word already exists";
    final static String ERROR_MEANING_EXISTS_MSG = "Meaning already exists";
    final static String ERROR_MISSING_MEANING_MSG = "Meaning field cannot be empty";
    final static String ERROR_MISSING_WORD_MSG = "Word field cannot be empty";
    final static String ERROR_MISSING_COMMAND_MSG = "Command field cannot be empty";
    final static String ERROR_DATABASE_FAILURE_MSG = "Database failure";
    final static String ERROR_MULTIPLE_MEANING_MSG = "Multiple meanings sent, only one is expected";
    final static String ERROR_INVALID_MEANING_MSG = "Invalid meaning. Must be at least 1 and less than 1000 characters";
    final static String ERROR_INVALID_WORD_MSG = "Invalid word. Must be at least 1 and less than 100 characters";
    final static String ERROR_MEANING_NOT_FOUND_MSG = "Meaning not found";
    final static String ERROR_MEANING_SAME_MSG = "Meaning is the same as the existing meaning";

    // Instance variables
    private String command;
    private String word;
    private String meaning;
    private String oldMeaning;
    private ArrayList<String> meanings;
    private String error;
    private Boolean success;
    public Boolean valid;

    // Constructor
    public Message() {
        this.command = null;
        this.word = null;
        this.meaning = null;
        this.oldMeaning = null;
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
        if (this.command.equals(ADD) || this.command.equals(UPDATE) || 
            this.command.equals(REMOVE_MEANING) || this.command.equals(EDIT_MEANING)) {
            if (this.meaning == null || this.meaning.isEmpty()) {
                this.error = ERROR_MISSING_MEANING_MSG;
                return false;
            }
            if (this.meaning.length() > MAX_MEANING_LENGTH) {
                this.error = ERROR_INVALID_MEANING_MSG;
                return false;
            }
        }
        if (this.command.equals(EDIT_MEANING)) {
            if (this.oldMeaning == null || this.oldMeaning.isEmpty()) {
                this.error = ERROR_MISSING_MEANING_MSG;
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
        response.setOldMeaning(this.oldMeaning);
        response.setMeanings(this.meanings);
        response.setSuccess(this.success);
        response.setError(this.error);
        return response;
    }

    public static Message makeQuery(String word) {
        Message message = new Message();
        message.setCommand(Message.QUERY);
        message.setWord(word);
        return message;
    }

    public static Message makeAdd(String word, String meaning) {
        Message message = new Message();
        message.setCommand(Message.ADD);
        message.setWord(word);
        message.setMeaning(meaning);
        return message;
    }

    public static Message makeRemoveWord(String word) {
        Message message = new Message();
        message.setCommand(Message.REMOVE_WORD);
        message.setWord(word);
        return message;
    }

    public static Message makeRemoveMeaning(String word, String meaning) {
        Message message = new Message();
        message.setCommand(Message.REMOVE_MEANING);
        message.setWord(word);
        message.setMeaning(meaning);
        return message;
    }

    public static Message makeUpdate(String word, String meaning) {
        Message message = new Message();
        message.setCommand(Message.UPDATE);
        message.setWord(word);
        message.setMeaning(meaning);
        return message;
    }

    public static Message makeEditMeaning(String word, String oldMeaning, String newMeaning) {
        Message message = new Message();
        message.setCommand(Message.EDIT_MEANING);
        message.setWord(word);
        message.setOldMeaning(oldMeaning);
        message.setMeaning(newMeaning);
        return message;
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
    public void setOldMeaning(String oldMeaning) {
        this.oldMeaning = oldMeaning;
    }
    public String getOldMeaning() {
        return oldMeaning;
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
    public void changeMeaning(String oldMeaning, String newMeaning) {
        // remove the old meaning and add the new meaning at the same position
        int index = this.meanings.indexOf(oldMeaning);
        if (index == -1) {
            return;
        }
        this.meanings.remove(index);
        this.meanings.add(index, newMeaning);
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
