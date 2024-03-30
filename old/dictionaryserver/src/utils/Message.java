package utils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Message {
    private String command;
    private String word;
    private String meanings;

    // Constructor
    public Message(String command, String word, String meanings) {
        this.command = command;
        this.word = word;
        this.meanings = meanings;
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
    public String getMeanings() {
        return meanings;
    }
    public void setMeanings(String meanings) {
        this.meanings = meanings;
    }


}
