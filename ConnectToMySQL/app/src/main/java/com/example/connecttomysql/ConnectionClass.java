package com.example.connecttomysql;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Objects;

public class ConnectionClass {
    protected static String db = "prm392";
    protected static String ip = "192.168.1.61";
    protected static String port = "3306";
    protected static String username = "root";
    protected static String password = "12345678";

    public static Connection getConnection(){
        Connection connection = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            String connectionString = "jdbc:mysql://" + ip + ":" + port + "/" + db;
            connection = DriverManager.getConnection(connectionString, username, password);

        }catch (Exception e){
            Log.e("Error", Objects.requireNonNull(e.getMessage()));
        }
        return connection;
    }
}
