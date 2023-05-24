package cse.java2.project;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class DataStore {

    public static void main(String[] args) {
        String pattern = "<code>(.*?)</code>";
        int totalQuestions = 500;
        int questionsPerPage = 100;
        int totalPages = (int) Math.ceil((double) totalQuestions / questionsPerPage);
        Map<String, Integer> apiFrequency = new HashMap<>();
        try {
            for (int page = 1; page <= totalPages; page++) {
                String pageUrl = "https://api.stackexchange.com/2.3/questions?page=" + page +
                    "&pagesize=100&order=desc&sort=activity&tagged=java&site=stackoverflow" +
                    "&filter=!*Mg4PjfgUpvo9FU5";
                URL url = new URL(pageUrl);
                HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
                connection1.setRequestMethod("GET");
                BufferedReader in;
                if ("gzip".equals(connection1.getContentEncoding())) {
                    in = new BufferedReader(
                        new InputStreamReader(new GZIPInputStream(connection1.getInputStream())));
                } else {
                    in = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
                }
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                in.close();
                Pattern codePattern = Pattern.compile(pattern);
                Matcher matcher = codePattern.matcher(response.toString());
                while (matcher.find()) {
                    String codeSnippet = matcher.group(1);
                    extractJavaAPIs(codeSnippet, apiFrequency);
                }

                JSONObject json = new JSONObject(response.toString());
                JSONArray items = json.getJSONArray("items");
                for (int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    if (item.has("comments")) {
                        JSONArray comments = item.getJSONArray("comments");
                        for (int j = 0; j < comments.length(); j++) {
                            JSONObject commentObject = comments.getJSONObject(j);
                            String commentText = commentObject.getString("body");
                            Matcher commentMatcher = codePattern.matcher(commentText);
                            while (commentMatcher.find()) {
                                String commentSnippet = commentMatcher.group(1);
                                extractJavaAPIs(commentSnippet, apiFrequency);
                            }
                        }
                    }
                    if (item.has("answers")) {
                        JSONArray answers = item.getJSONArray("answers");
                        for (int j = 0; j < answers.length(); j++) {
                            JSONObject answerObject = answers.getJSONObject(j);
                            String answerText = answerObject.getString("body");
                            Matcher answerMatcher = codePattern.matcher(answerText);
                            while (answerMatcher.find()) {
                                String answerSnippet = answerMatcher.group(1);
                                extractJavaAPIs(answerSnippet, apiFrequency);
                            }
                        }
                    }
                }
            }
            String filePath = "src/main/resources/static/js/FrequentlyDiscussedAPIs.js";
            Map<String, Integer> apiFrequencyFinal = apiFrequency.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(50)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("let wordcloudRaw =")) {
                        Map<String, Integer> values = extractValues(line);
                        values.clear();
                        values.putAll(apiFrequencyFinal);
                        String modifiedLine = generateModifiedLine(values);
                        line = line.replaceFirst("let wordcloudRaw =.*", modifiedLine);
                    }
                    content.append(line).append("\n");
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                    writer.write(content.toString());
                }
                System.out.println("JavaScript file modified successfully.");
            } catch (IOException e) {
                System.out.println(
                    "An error occurred while modifying the JavaScript file: " + e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractJavaAPIs(String codeSnippet, Map<String, Integer> apiFrequency) {
        Pattern javaAPITagPattern = Pattern.compile("(?<![.\\w])\\w+(?=\\()");
        Matcher matcher = javaAPITagPattern.matcher(codeSnippet);
        while (matcher.find()) {
            String javaAPI = matcher.group();
            if (!Character.isLowerCase(javaAPI.charAt(0))) {
                apiFrequency.put(javaAPI, apiFrequency.getOrDefault(javaAPI, 0) + 1);
            }
        }
    }

    private static Map<String, Integer> extractValues(String line) {
        Map<String, Integer> values = new HashMap<>();

        // Match key-value pairs within curly braces
        Pattern pattern = Pattern.compile("\"(.*?)\":\\s*(\\d+)");
        Matcher matcher = pattern.matcher(line);

        // Extract the key-value pairs and store them in the map
        while (matcher.find()) {
            String key = matcher.group(1);
            int value = Integer.parseInt(matcher.group(2));
            values.put(key, value);
        }

        return values;
    }

    private static String generateModifiedLine(Map<String, Integer> values) {
        StringBuilder line = new StringBuilder();
        line.append("let ").append("wordcloudRaw").append(" = {");

        // Generate the modified key-value pairs
        for (Map.Entry<String, Integer> entry : values.entrySet()) {
            line.append("\"").append(entry.getKey()).append("\": ").append(entry.getValue())
                .append(", ");
        }

        // Remove the trailing comma and space
        line.delete(line.length() - 2, line.length());

        line.append("}");

        return line.toString();
    }
}
