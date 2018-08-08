package cj.dice;

import org.json.JSONObject;

public class ShowScoreboardCommand implements ClientCommand {

    @Override
    public String execute(String userInput, JSONObject jsonObject) {
        return jsonObject.getString("scoreboard");
    }

    @Override
    public boolean isTrigger(String userInput) {
        return userInput.equals("p");
    }

    @Override
    public String retrieveInstructions() {
        return "[p] print scoreboard";
    }
}
