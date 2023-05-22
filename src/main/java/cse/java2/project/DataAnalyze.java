package cse.java2.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAnalyze {

    public static Connection con = null;
    public static String user = "postgres";
    public static String pwd = "123456";

    private static void getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            System.exit(1);
        }
        try {
            String url = "jdbc:postgresql://localhost:5432/postgres";
            con = DriverManager.getConnection(url, user, pwd);

        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void closeConnection() {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
//        getConnection();
        QuestionAnswerNumber();
        QuestionAnswerEvaluation();
        AnswerNumberDistribution();
        QuestionAcceptedAnswerNumber();
        QuestionResolutionTimeDistribution();
        MoreUpvotesNon_acceptedAnswer();
        AppearTogetherWithJavaTag();
        TheMostUpvotesTag();
        TheMostViewsTag();
        ParticipationDistribution();
        TheMostActiveUser();
//        closeConnection();
    }

    public static void QuestionAnswerNumber() {
        String filePath = "src/main/java/cse/java2/project/NoAnswerQuestion.js";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Find the line containing the piechartRaw object
                if (line.contains("let piechartRaw =")) {
                    // Extract the key-value pairs from the piechartRaw object
                    Map<String, Integer> values = extractValues(line);

                    // Modify the values as per your requirements
                    if (values.containsKey("No answer")) {
                        int noAnswerValue = values.get("No answer");
                        values.put("No answer", noAnswerValue + 1);
                    }

                    if (values.containsKey("Has answer")) {
                        int hasAnswerValue = values.get("Has answer");
                        values.put("Has answer", hasAnswerValue - 1);
                    }

                    // Generate the modified piechartRaw line
                    String modifiedLine = generateModifiedLine(values);

                    // Replace the original piechartRaw line with the modified line
                    line = line.replaceFirst("let piechartRaw =.*", modifiedLine);
                }

                // Append the modified line to the content
                content.append(line).append("\n");
            }

            // Write the modified content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                writer.write(content.toString());
            }

            System.out.println("JavaScript file modified successfully.");
        } catch (IOException e) {
            System.out.println(
                "An error occurred while modifying the JavaScript file: " + e.getMessage());
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
        line.append("let piechartRaw = {");

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

    public static void QuestionAnswerEvaluation() {

    }

    public static void AnswerNumberDistribution() {

    }

    public static void QuestionAcceptedAnswerNumber() {

    }

    public static void QuestionResolutionTimeDistribution() {

    }

    public static void MoreUpvotesNon_acceptedAnswer() {

    }

    public static void AppearTogetherWithJavaTag() {

    }

    public static void TheMostUpvotesTag() {

    }

    public static void TheMostViewsTag() {

    }

    public static void ParticipationDistribution() {

    }

    public static void TheMostActiveUser() {

    }
}
