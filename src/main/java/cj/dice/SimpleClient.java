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

    public static List<ClientCommand> clientCommands;

    static {
        clientCommands = new ArrayList<>();
        clientCommands.add(new ShowScoreboardCommand());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter name");
        String playerName = scanner.next();

        Content response = requestResource("/command/newgame", playerName);
        JSONObject jsonResponse = new JSONObject(response.asString());
        String result = jsonResponse.getString("result");
        String userInput;

        while (!isComplete(jsonResponse)) {
            printResult(result);
            userInput = scanner.next();
            Optional<ClientCommand> executableClientCommand = findExecutableClientCommand(userInput);
            if (executableClientCommand.isPresent()) {
                result = executableClientCommand.get().execute(userInput, jsonResponse);
            } else {
                int turnId = jsonResponse.getInt("turnId");
                response = requestResource("/command", new JSONObject().put("turnId", turnId).put("userInput", userInput));
                jsonResponse = new JSONObject(response.asString());
                result = jsonResponse.getString("result");
            }
        }
        System.out.println("the end");
    }

    private static Optional<ClientCommand> findExecutableClientCommand(String userInput) {
        return clientCommands.stream().filter(c -> c.isTrigger(userInput)).findFirst();
    }

    private static void printResult(String result) {
        System.out.println(result);
        System.out.println("enter command");
    }

    private static boolean isComplete(JSONObject jsonResponse) {
        return jsonResponse.getBoolean("complete");
    }

    private static Content requestResource(String resource, String data) {
        Content response = null;
        try {
            response = Request.Post("http://localhost:8080/dicecj/resources" + resource).body(new StringEntity(data)).execute().returnContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    private static Content requestResource(String resource, JSONObject jsonData) {
        Content response = null;
        try {
            response = Request.Post("http://localhost:8080/dicecj/resources" + resource).body(new StringEntity(jsonData.toString())).execute().returnContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}
