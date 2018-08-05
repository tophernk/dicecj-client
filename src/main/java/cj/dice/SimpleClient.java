package cj.dice;

import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import java.io.IOException;

public class SimpleClient
{
    public static void main( String[] args )
    {
        Content content = null;
        try {
            content = Request.Get("http://localhost:8080").execute().returnContent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println( content.asString() );
    }
}
