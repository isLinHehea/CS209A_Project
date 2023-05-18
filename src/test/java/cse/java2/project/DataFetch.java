package cse.java2.project;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataFetch {
    public static void main(String[] args) throws IOException, JSONException {

        String url = "https://api.stackexchange.com/2.3/questions?page=1&pagesize=100&order=desc&sort=activity&tagged=java&site=stackoverflow";
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        BufferedReader in;

        if ("gzip".equals(con.getContentEncoding())) {
            in = new BufferedReader(new InputStreamReader(new GZIPInputStream(con.getInputStream())));
        } else {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        }
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        int max = 0;
        // Parse the JSON data from the API response
        JSONObject json = new JSONObject(response.toString());
        JSONArray items = json.getJSONArray("items");
        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            System.out.println("is_answered: " + item.getBoolean("is_answered"));
            if (item.getBoolean("is_answered")){
                System.out.println("answer_count: " + item.getInt("answer_count"));
                max = Math.max(item.getInt("answer_count"),max);
            }

            System.out.println("Tags: " + item.getJSONArray("tags"));
            System.out.println("Link: " + item.getString("link"));
            System.out.println();
        }
        System.out.println(max);
    }

}
