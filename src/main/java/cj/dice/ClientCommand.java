package cj.dice;

import org.json.JSONObject;

public interface ClientCommand {

    public String execute(String userInput, JSONObject jsonObject);

    public boolean isTrigger(String userInput);

    public String retrieveInstructions();
}
