package cse.java2.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
        getConnection();
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
        closeConnection();
    }

    public static void QuestionAnswerNumber() {

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
