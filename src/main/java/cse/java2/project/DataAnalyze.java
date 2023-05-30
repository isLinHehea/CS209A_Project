package cse.java2.project;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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

    public static void main(String[] args) throws SQLException {
        getConnection();
        QuestionAnswerNumber("piechartRaw");
        QuestionAnswerEvaluation("barchartRaw");
        AnswerNumberDistribution("piechartRaw");
        QuestionAcceptedAnswerNumber("piechartRaw");
        QuestionResolutionTimeDistribution("piechartRaw");
        MoreUpvotesNon_acceptedAnswer("piechartRaw");
        AppearTogetherWithJavaTag("wordcloudRaw");
        TheMostUpvotesTag("wordcloudRaw");
        TheMostViewsTag("wordcloudRaw");
        ParticipationDistribution("piechartRaw");
        TheMostActiveUser("wordcloudRaw");
        closeConnection();
    }

    public static Map<String, Integer> extractValues(String line) {
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

    private static String generateModifiedLine(String str, Map<String, Integer> values) {
        StringBuilder line = new StringBuilder();
        line.append("let ").append(str).append(" = {");

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

    public static void QuestionAnswerNumber(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Number_of_Answers/NoAnswerQuestion.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where is_answered = true;");
        rs0.next();
        int hasAnswerValue = rs0.getInt(1);
        int noAnswerValue = 500 - hasAnswerValue;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;

            // Read the file line by line
            while ((line = reader.readLine()) != null) {
                // Find the line containing the piechartRaw object
                if (line.contains("let " + str + " =")) {
                    // Extract the key-value pairs from the piechartRaw object
                    Map<String, Integer> values = extractValues(line);

                    // Modify the values as per your requirements
                    if (values.containsKey("No answer")) {
//                        int noAnswerValue = values.get("No answer");
                        values.put("No answer", noAnswerValue);
                    }

                    if (values.containsKey("Has answer")) {
//                        int hasAnswerValue = values.get("Has answer");
                        values.put("Has answer", hasAnswerValue);
                    }

                    // Generate the modified piechartRaw line
                    String modifiedLine = generateModifiedLine(str, values);

                    // Replace the original piechartRaw line with the modified line
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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

    public static void QuestionAnswerEvaluation(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Number_of_Answers/AVEMAXNumber.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select answer_numbers\n"
            + "from question\n"
            + "order by answer_numbers desc\n"
            + "limit 1;");
        rs0.next();
        int maximum = rs0.getInt(1);

        ResultSet rs1 = stmt.executeQuery("select avg(answer_numbers)\n"
            + "from question;");
        rs1.next();
        int average = Math.round(rs1.getFloat(1));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    if (values.containsKey("Maximum")) {
                        values.put("Maximum", maximum);
                    }
                    if (values.containsKey("Average")) {
                        values.put("Average", average);
                    }
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void AnswerNumberDistribution(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Number_of_Answers/NumberDistribution.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where answer_numbers = 0;");
        rs0.next();
        int first = rs0.getInt(1);

        ResultSet rs1 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where answer_numbers >= 1\n"
            + "  and answer_numbers <= 3;");
        rs1.next();
        int second = Math.round(rs1.getFloat(1));

        ResultSet rs2 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where answer_numbers >= 4\n"
            + "  and answer_numbers <= 6;");
        rs2.next();
        int third = Math.round(rs2.getFloat(1));

        ResultSet rs3 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where answer_numbers >= 7\n"
            + "  and answer_numbers <= 9;");
        rs3.next();
        int fourth = Math.round(rs3.getFloat(1));

        ResultSet rs4 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where answer_numbers >= 10;");
        rs4.next();
        int fifth = Math.round(rs4.getFloat(1));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    if (values.containsKey("= 0")) {
                        values.put("= 0", first);
                    }
                    if (values.containsKey(">= 1 & <= 3")) {
                        values.put(">= 1 & <= 3", second);
                    }
                    if (values.containsKey(">= 4 & <= 6")) {
                        values.put(">= 4 & <= 6", third);
                    }
                    if (values.containsKey(">= 7 & <= 9")) {
                        values.put(">= 7 & <= 9", fourth);
                    }
                    if (values.containsKey(">= 10")) {
                        values.put(">= 10", fifth);
                    }
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void QuestionAcceptedAnswerNumber(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Accepted_Answers/AcceptAnswerQuestion.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where accepted_answer_id != 0;");
        rs0.next();
        int hasAcceptedAnswerValue = rs0.getInt(1);
        int noAcceptedAnswerValue = 500 - hasAcceptedAnswerValue;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    if (values.containsKey("No accepted answer")) {
                        values.put("No accepted answer", noAcceptedAnswerValue);
                    }
                    if (values.containsKey("Has accepted answer")) {
                        values.put("Has accepted answer", hasAcceptedAnswerValue);
                    }
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void QuestionResolutionTimeDistribution(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Accepted_Answers/QuestionResolutionTimeDistribution.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_posting_time - question_posting_time) as time\n"
            + "      from (select question_posting_time, answer_posting_time\n"
            + "            from question\n"
            + "            where accepted_answer_id != 0) t1) t2\n"
            + "where time < 1000;");
        rs0.next();
        int first = rs0.getInt(1);

        ResultSet rs1 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_posting_time - question_posting_time) as time\n"
            + "      from (select question_posting_time, answer_posting_time\n"
            + "            from question\n"
            + "            where accepted_answer_id != 0) t1) t2\n"
            + "where time > 1000\n"
            + "  and time <= 10000;");
        rs1.next();
        int second = Math.round(rs1.getFloat(1));

        ResultSet rs2 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_posting_time - question_posting_time) as time\n"
            + "      from (select question_posting_time, answer_posting_time\n"
            + "            from question\n"
            + "            where accepted_answer_id != 0) t1) t2\n"
            + "where time > 10000\n"
            + "  and time <= 100000;");
        rs2.next();
        int third = Math.round(rs2.getFloat(1));

        ResultSet rs3 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_posting_time - question_posting_time) as time\n"
            + "      from (select question_posting_time, answer_posting_time\n"
            + "            from question\n"
            + "            where accepted_answer_id != 0) t1) t2\n"
            + "where time > 100000;");
        rs3.next();
        int fourth = Math.round(rs3.getFloat(1));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    if (values.containsKey(">= 0, <= 1000")) {
                        values.put(">= 0, <= 1000", first);
                    }
                    if (values.containsKey("> 1000, <= 10000")) {
                        values.put("> 1000, <= 10000", second);
                    }
                    if (values.containsKey("> 10000, <= 100000")) {
                        values.put("> 10000, <= 100000", third);
                    }
                    if (values.containsKey("> 100000")) {
                        values.put("> 100000", fourth);
                    }
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void MoreUpvotesNon_acceptedAnswer(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Accepted_Answers/Non-acceptedAnswer.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where accepted_answer_id != question.most_upvote_answer_id\n"
            + "  and accepted_answer_id != 0;");
        rs0.next();
        int moreUpvotesNon_acceptedAnswerValue = rs0.getInt(1);

        ResultSet rs1 = stmt.executeQuery("select count(*)\n"
            + "from question\n"
            + "where accepted_answer_id = question.most_upvote_answer_id\n"
            + "  and accepted_answer_id != 0;");
        rs1.next();
        int lessUpvotesNon_acceptedAnswerValue = rs1.getInt(1);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    if (values.containsKey("More Upvotes Non-accepted Answer")) {
                        values.put("More Upvotes Non-accepted Answer",
                            moreUpvotesNon_acceptedAnswerValue);
                    }
                    if (values.containsKey("Less Upvotes Non-accepted Answer")) {
                        values.put("Less Upvotes Non-accepted Answer",
                            lessUpvotesNon_acceptedAnswerValue);
                    }
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void AppearTogetherWithJavaTag(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Tag/AppearTogetherWithJavaTag.js";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select tags\n"
            + "from question;");
        Map<String, Integer> tags = new HashMap<>();
        while (rs.next()) {
            String tagsStr = rs.getString(1);
            String[] tagsResult = tagsStr.split(",");
            for (int i = 0; i < tagsResult.length; i++) {
                String tag = tagsResult[i];
                if (tags.containsKey(tag)) {
                    tags.replace(tag, tags.get(tag) + 1);
                } else {
                    tags.put(tag, 1);
                }
            }
        }
        Map<String, Integer> tagsFinal = tags.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(50)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    values.clear();
                    values.putAll(tagsFinal);
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void TheMostUpvotesTag(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/Tag/TheMostUpvotesTag.js";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select tags, upvote\n"
            + "from question;");
        Map<String, Integer> tags = new HashMap<>();
        while (rs.next()) {
            String tagsStr = rs.getString(1);
            int upvoteNum = rs.getInt(2);
            String[] tagsResult = tagsStr.split(",");
            for (int i = 0; i < tagsResult.length; i++) {
                String tag = tagsResult[i];
                if (tags.containsKey(tag)) {
                    tags.replace(tag, tags.get(tag) + upvoteNum);
                } else {
                    tags.put(tag, 1);
                }
            }
        }
        Map<String, Integer> tagsFinal = tags.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(50)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    values.clear();
                    values.putAll(tagsFinal);
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void TheMostViewsTag(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/TheMostViewsTag.js";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery("select tags, views\n"
            + "from question;");
        Map<String, Integer> tags = new HashMap<>();
        while (rs.next()) {
            String tagsStr = rs.getString(1);
            int upvoteNum = rs.getInt(2);
            String[] tagsResult = tagsStr.split(",");
            for (int i = 0; i < tagsResult.length; i++) {
                String tag = tagsResult[i];
                if (tags.containsKey(tag)) {
                    tags.replace(tag, tags.get(tag) + upvoteNum);
                } else {
                    tags.put(tag, 1);
                }
            }
        }
        Map<String, Integer> tagsFinal = tags.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(50)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    values.clear();
                    values.putAll(tagsFinal);
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void ParticipationDistribution(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/User/ParticipationDistribution.js";

        Statement stmt = con.createStatement();
        ResultSet rs0 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_numbers + comment + 1) as sum\n"
            + "      from question) l\n"
            + "where sum >= 1\n"
            + "  and sum <= 10;");
        rs0.next();
        int first = rs0.getInt(1);

        ResultSet rs1 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_numbers + comment + 1) as sum\n"
            + "      from question) l\n"
            + "where sum > 10\n"
            + "  and sum <= 20;");
        rs1.next();
        int second = Math.round(rs1.getFloat(1));

        ResultSet rs2 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_numbers + comment + 1) as sum\n"
            + "      from question) l\n"
            + "where sum > 20\n"
            + "  and sum <= 30;");
        rs2.next();
        int third = Math.round(rs2.getFloat(1));

        ResultSet rs3 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_numbers + comment + 1) as sum\n"
            + "      from question) l\n"
            + "where sum > 30\n"
            + "  and sum <= 40;");
        rs3.next();
        int fourth = Math.round(rs3.getFloat(1));

        ResultSet rs4 = stmt.executeQuery("select count(*)\n"
            + "from (select (answer_numbers + comment + 1) as sum\n"
            + "      from question) l\n"
            + "where sum > 40;");
        rs4.next();
        int fifth = Math.round(rs4.getFloat(1));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    if (values.containsKey(">= 1 & <= 10")) {
                        values.put(">= 1 & <= 10", first);
                    }
                    if (values.containsKey("> 10 & <= 20")) {
                        values.put("> 10 & <= 20", second);
                    }
                    if (values.containsKey("> 20 & <= 30")) {
                        values.put("> 20 & <= 30", third);
                    }
                    if (values.containsKey("> 30 & <= 40")) {
                        values.put("> 30 & <= 40", fourth);
                    }
                    if (values.containsKey("> 40")) {
                        values.put("> 40", fifth);
                    }
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }

    public static void TheMostActiveUser(String str) throws SQLException {
        String filePath = "src/main/resources/static/js/User/TheMostActiveUser.js";

        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(
            "select owner_id, (question_numbers + answer_numbers + comment_numbers) as sum\n"
                + "from owner;");
        Map<String, Integer> users = new HashMap<>();
        while (rs.next()) {
            String usersStr = rs.getString(1);
            int num = rs.getInt(2);
            users.put(usersStr,num);
        }
        Map<String, Integer> usersFinal = users.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(50)
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                (oldValue, newValue) -> oldValue, LinkedHashMap::new));

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("let " + str + " =")) {
                    Map<String, Integer> values = extractValues(line);
                    values.clear();
                    values.putAll(usersFinal);
                    String modifiedLine = generateModifiedLine(str, values);
                    line = line.replaceFirst("let " + str + " =.*", modifiedLine);
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
    }
}
