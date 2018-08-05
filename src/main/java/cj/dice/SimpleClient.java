package cj.dice;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Scanner;

public class SimpleClient {

    public static void main(String[] args) {
        JSONObject jsonRequest = new JSONObject();
        jsonRequest.put("name", "CJ");
        Content response = requestResource("/command/newgame", "CJ");
        JSONObject jsonResponse = new JSONObject(response.asString());

        Scanner scanner = new Scanner(System.in);
        String userInput;

        while (!isComplete(jsonResponse)) {
            printState(jsonResponse);
            userInput = scanner.next();
            response = requestResource("/command", new JSONObject().put("state", jsonResponse).put("userInput", userInput));
            System.out.println(response.asString());
        }
        System.out.println("the end");
    }

    private static void printState(JSONObject jsonResponse) {
        System.out.println("enter command");
    }

    private static boolean isComplete(JSONObject jsonResponse) {
        return (Boolean) jsonResponse.getJSONObject("scoreboard").get("complete");
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
