package cse.java2.project;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class DataStore {
    public static void main(String[] args) {
        String apiUrl = "https://api.stackexchange.com/2.3/questions?order=desc&sort=activity&tagged=java&site=stackoverflow&filter=!)5gbzFCpDpqI.hwSxz)_ewjJDfr1";
        String pattern = "<code>(.*?)</code>";
        Set<String> javaAPIs = new HashSet<>();

        try {
            URL url = new URL(apiUrl);


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
                extractJavaAPIs(codeSnippet, javaAPIs);
            }

            for (String api : javaAPIs) {
                System.out.println(api);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractJavaAPIs(String codeSnippet, Set<String> javaAPIs) {
        Pattern javaAPITagPattern = Pattern.compile("(?<![.\\w])\\w+(?=\\()");
        Matcher matcher = javaAPITagPattern.matcher(codeSnippet);
        while (matcher.find()) {
            String javaAPI = matcher.group();
            if (!Character.isLowerCase(javaAPI.charAt(0))) {
                javaAPIs.add(javaAPI);
            }
        }
    }
}
