package cse.java2.project;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.*;

public class DatabaseManipulation implements DataManipulation {
    private Connection con = null;
    private ResultSet resultSet;

    private String host = "localhost";
    private String dbname = "Shipment_records";
    private String user = "checker";
    private String pwd = "123456";
    private String port = "5432";


    private void getConnection() {
        try {
            Class.forName("org.postgresql.Driver");

        } catch (Exception e) {
            System.err.println("Cannot find the PostgreSQL driver. Check CLASSPATH.");
            System.exit(1);
        }

        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + dbname;
            con = DriverManager.getConnection(url, user, pwd);

        } catch (SQLException e) {
            System.err.println("Database connection failed");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }


    private void closeConnection() {
        if (con != null) {
            try {
                con.close();
                con = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void getTxt() {
        try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("item_transportation_information1.csv"))) {
            getConnection();
            StringBuilder sb = new StringBuilder();
            sb.append("item_name,retrieval_city_code,delivery_city_code,retrieval_courier_phone_number,delivery_courier_phone_number,ship_name,container_code\n");
            String sql = "select * from item_transportation_information1 ";
            try {
                Statement statement = con.createStatement();
                resultSet = statement.executeQuery(sql);
                while (resultSet.next()) {
                    sb.append(resultSet.getString("item_name") + ",");
                    sb.append(resultSet.getString("retrieval_city_code") + ",");
                    sb.append(resultSet.getString("delivery_city_code") + ",");
                    sb.append(resultSet.getString("retrieval_courier_phone_number") + ",");
                    sb.append(resultSet.getString("delivery_courier_phone_number") + ",");
                    sb.append(resultSet.getString("ship_name") + ",");
                    sb.append(resultSet.getString("container_code") + "\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                closeConnection();
            }
            osw.write(sb.toString());
            osw.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int updateReCode(String str2, String x) {
        getConnection();
        String sql = " update item_transportation_information1 set retrieval_city_code=? where item_name = ?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, str2);
            preparedStatement.setString(2, x);
            int re = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return 1;
    }

    public int updateDeCode(String str2, String x) {
        getConnection();
        String sql = " update item_transportation_information1 set delivery_city_code=? where item_name =?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, str2);
            preparedStatement.setString(2, x);
            int re = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return 1;
    }

    public String deleteOneItem(String x) {
        getConnection();

        String sql = "delete from item_transportation_information1 where delivery_city_code=?;";
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, x);

            int re = preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return x;

    }

    @Override
    public int addOneItem(String str) {
        getConnection();
        int result = 0;
        String sql = "insert into item_transportation_information1( item_name,retrieval_city_code,delivery_city_code,retrieval_courier_phone_number,delivery_courier_phone_number,ship_name,container_code) " +
                "values (?,?,?,?,?,?,?)";
        String Info[] = str.split(",");
        try {
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, Info[0]);
            preparedStatement.setString(2, Info[1]);
            preparedStatement.setString(3, Info[2]);
            preparedStatement.setString(4, Info[3]);
            preparedStatement.setString(5, Info[4]);
            preparedStatement.setString(6, Info[5]);
            preparedStatement.setString(7, Info[6]);
//            System.out.println(preparedStatement.toString());
            result = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
        return result;
    }



}
