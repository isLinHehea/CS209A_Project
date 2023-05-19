package cse.java2.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.zip.GZIPInputStream;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class DataFetch {
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
        // PostgreSQL数据库连接信息


        // API请求参数
        String apiUrl = "https://api.stackexchange.com/2.3/questions";
        String order = "desc";
        String sort = "activity";
        String tagged = "java";

        String site = "stackoverflow";
        int pageSize = 100;

//        try {
//            // 创建数据库连接
//            getConnection();
//
//            // 设置API请求参数
//            String apiParams = String.format("&order=%s&sort=%s&tagged=%s&site=%s",
//                    order, sort, tagged, site);
//
//            int page = 1;
//            int fetchedCount = 0;
//
//            while (fetchedCount < 2000) {
//                // 发起API请求
//                URL url = new URL(apiUrl + "?page=" + page + "&pagesize=" + pageSize + apiParams);
//                HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
//                connection1.setRequestMethod("GET");
//
//
//                BufferedReader in;
//
//                if ("gzip".equals(connection1.getContentEncoding())) {
//                    in = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection1.getInputStream())));
//                } else {
//                    in = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
//                }
//
//                // 读取API响应数据
//
//                StringBuilder response = new StringBuilder();
//                String line;
//                while ((line = in.readLine()) != null) {
//                    response.append(line);
//                }
//                in.close();
//
//                // 解析JSON响应
//                JSONObject json = new JSONObject(response.toString());
//                JSONArray items = json.getJSONArray("items");
//                // Parse the JSON data from the API response
//
//
//                // 将数据插入数据库
//                for (int i = 0; i < items.length(); i++) {
//                    JSONObject item = items.getJSONObject(i);
//                    String ownerId = "";
//                    if (item.has("owner")) {
//                        JSONObject owner = item.getJSONObject("owner");
//                        if (owner.has("account_id")) {
//                            ownerId = owner.getString("account_id");
//                        } else {
//                            ownerId = owner.getString("display_name");
//                        }
//                    }
//                    int questionId = item.getInt("question_id");
//                    boolean isAnswered = item.getBoolean("is_answered");
//                    int answerCount = item.has("answer_count") ? item.getInt("answer_count") : 0;
//                    int acceptedAnswerId = item.has("accept_answer_id") ? item.getInt("accept_answer_id") : 0;
//                    int questionPostingTime = item.has("creation_date") ? item.getInt("creation_date") : 0;
//                    int answerPostingTime = 0;
//                    int most_upvote_answer_id = 0;
//                    JSONArray tagArray = item.getJSONArray("tags");
//                    StringBuilder sb = new StringBuilder();
//                    for (int j = 0; j < tagArray.length(); j++) {
//                        sb.append(tagArray.getString(j));
//                        sb.append(",");
//
//                    }
//                    sb.deleteCharAt(sb.length()-1);
//                    String tags = sb.toString();
//                    int upvote = item.getInt("score");
//                    int views = item.has("view_count") ? item.getInt("view_count") : 0;
//                    int userCount = views + 1;
//                    // 执行数据库插入操作
//                    insertData(con, ownerId, questionId, isAnswered,
//                            answerCount, acceptedAnswerId, questionPostingTime, answerPostingTime
//                            , most_upvote_answer_id, tags, upvote, views, userCount);
//                }
//                // 更新计数器
//                fetchedCount += items.length();
//                page++;
//            }
//
//            // 关闭数据库连接
//            closeConnection();
//
//            System.out.println("数据导入完成！");
//        } catch (IOException | SQLException e) {
//            e.printStackTrace();
//        } catch (JSONException e) {
//            throw new RuntimeException(e);
//        } finally {
            try {
                // 创建数据库连接
                getConnection();
                for (int m = 1; m <= 2000; m++) {


                    int s = getId(m);

                    // 设置API请求参数
                    String apiParams = String.format("/answers?order=%s&sort=%s&site=%s",
                            order, sort, site);


                    // 发起API请求
                    URL url = new URL(apiUrl + "/"+s + apiParams);
                    HttpURLConnection connection1 = (HttpURLConnection) url.openConnection();
                    connection1.setRequestMethod("GET");


                    BufferedReader in;

                    if ("gzip".equals(connection1.getContentEncoding())) {
                        in = new BufferedReader(new InputStreamReader(new GZIPInputStream(connection1.getInputStream())));
                    } else {
                        in = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
                    }

                    // 读取API响应数据

                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // 解析JSON响应
                    JSONObject json = new JSONObject(response.toString());
                    JSONArray items = json.getJSONArray("items");
                    // Parse the JSON data from the API response

                    int mostUpvoteAnswer = 0;
                    int mostUpvote = 0;
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        int vote = item.getInt("score");
                        boolean isAccepted =item.getBoolean("is_accepted");
                        if (isAccepted){
                            int answerPosting = item.getInt("creation_date");
                            updateData(s,answerPosting);
                        }

                        if(Math.max(mostUpvote,vote)==vote){
                            mostUpvoteAnswer = item.getInt("answer_id");
                        }


                        // 执行数据库插入操作

                    }
                    updateUpvote(mostUpvoteAnswer,s);
                }


                // 关闭数据库连接
                closeConnection();

                System.out.println("数据导入完成！");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
//        }
    }

    private static void insertData(Connection connection, String ownerId, int questionId, boolean isAnswered, int answerCount,
                                   int acceptedAnswerId, int questionPostingTime, int answerPostingTime, int most_upvote_answer_id,
                                   String tags, int upvote, int views, int userCount) throws SQLException, SQLException {
        String insertQuery = "INSERT INTO question (owner_id,question_id,is_answered," +
                "answer_numbers,accepted_answer_id,question_posting_time,answer_posting_time," +
                "most_upvote_answer_id,tags,upvote,views,user_count) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";

        // 创建预编译语句
        PreparedStatement statement = connection.prepareStatement(insertQuery);
        statement.setString(1, ownerId);
        statement.setInt(2, questionId);
        statement.setBoolean(3, isAnswered);
        statement.setInt(4, answerCount);
        statement.setInt(5, acceptedAnswerId);
        statement.setInt(6, questionPostingTime);
        statement.setInt(7, answerPostingTime);
        statement.setInt(8, most_upvote_answer_id);
        statement.setString(9, tags);
        statement.setInt(10, upvote);
        statement.setInt(11, views);
        statement.setInt(12, userCount);


        // 执行插入操作
        statement.executeUpdate();

        // 关闭语句
        statement.close();
    }

    public static int getId(int n) {
        String sql = "SELECT question_id FROM question LIMIT 1 OFFSET ?";
        int id = 0;
        try {


            PreparedStatement statement = con.prepareStatement(sql);
            statement.setInt(1, n - 1); // 设置偏移量为 n-1，以选择第 n 个数
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                id = resultSet.getInt("question_id");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }


    public static void updateData(int s, int n) {
        String sql = " update question set answer_posting_time =? where question_id=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, n);
            preparedStatement.setInt(2, s);
            int re = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void updateUpvote(int s, int n) {
        String sql = " update question set most_upvote_answer_id =? where question_id=?";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, s);
            preparedStatement.setInt(2, n);
            int re = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
