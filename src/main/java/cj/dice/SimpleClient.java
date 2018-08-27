package cj.dice;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class SimpleClient {

    public static final String NUMBER_OF_ROLLS = "numberOfRolls";
    public static final String GAME_ID = "gameId";
    public static final String RESULT = "result";
    public static final String DICECJ_RESOURCE = "http://localhost:8080/dicecj/resources";
    public static final String COMPLETE = "complete";
    public static List<ClientCommand> clientCommands;

    static {
        clientCommands = new ArrayList<>();
        clientCommands.add(new ShowScoreboardCommand());
    }

    public static void main(String[] args) {
        Content response = requestGetResource("/command/overview");
        JSONObject jsonResponse = new JSONObject(response.asString());
        jsonResponse.put(NUMBER_OF_ROLLS, -1);
        jsonResponse.put(GAME_ID, -1);
        jsonResponse.put(RESULT, response.asString());
        jsonResponse.put(COMPLETE, false);
        System.out.println(jsonResponse);

        Scanner scanner = new Scanner(System.in);
        String userInput;
        String result = null;

        while (!isComplete(jsonResponse)) {
            printResult(result, jsonResponse.getInt(NUMBER_OF_ROLLS));
            userInput = scanner.next();
            Optional<ClientCommand> executableClientCommand = findExecutableClientCommand(userInput);
            if (executableClientCommand.isPresent()) {
                result = executableClientCommand.get().execute(userInput, jsonResponse);
            } else {
                int gameId = jsonResponse.getInt(GAME_ID);
                response = requestPostResource("/command", new JSONObject().put(GAME_ID, gameId).put("userInput", userInput).toString());
                jsonResponse = new JSONObject(response.asString());
                result = jsonResponse.getString(RESULT);
            }
        }
        System.out.println("scoreboard is complete");
    }

    private static Optional<ClientCommand> findExecutableClientCommand(String userInput) {
        return clientCommands.stream().filter(c -> c.isTrigger(userInput)).findFirst();
    }

    private static void printResult(String result, int numberOfRolls) {
        System.out.println(result);
        System.out.println("enter command [" + numberOfRolls + "]");
    }

    private static boolean isComplete(JSONObject jsonResponse) {
        return jsonResponse.getBoolean(COMPLETE);
    }

    private static Content requestGetResource(String resource) {
        Content response = null;
        try {
            response = Request.Get(DICECJ_RESOURCE + resource).execute().returnContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static Content requestPostResource(String resource, String data) {
        Content response = null;
        try {
            response = Request.Post(DICECJ_RESOURCE + resource).body(new StringEntity(data)).execute().returnContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

}
