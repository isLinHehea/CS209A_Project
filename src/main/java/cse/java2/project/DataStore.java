package cse.java2.project;

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
                String pageUrl = "https://api.stackexchange.com/2.3/questions?page="+page+"&pagesize=100&order=desc&sort=activity&tagged=java&site=stackoverflow&filter=!)5gbzFCpDpqI.hwSxz)_ewjJDfr1";
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
            }

            List<Map.Entry<String, Integer>> sortedAPIs = new ArrayList<>(apiFrequency.entrySet());
            sortedAPIs.sort((a, b) -> b.getValue().compareTo(a.getValue()));

            for (Map.Entry<String, Integer> entry : sortedAPIs) {
                System.out.println(entry.getKey() + " : " + entry.getValue());
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
}
