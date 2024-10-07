package com.example.connecttomysql;

import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    ConnectionClass connectionClass;
    Connection connection;
    ResultSet resultSet;
    String name, str;
TextView txtName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        // Cấu hình StrictMode
/*        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);*/
        connectionClass = new ConnectionClass();
        connect();
    }

    public void btnClick(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                connection = connectionClass.CON();
                String query = "SELECT * FROM prm392.user";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                StringBuilder builder = new StringBuilder("List User\n");
                while (resultSet.next()){
                    builder.append(resultSet.getString("name")).append("\n");
                }
                name = builder.toString();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                txtName = findViewById(R.id.textView);
                txtName.setText(name);
            });

        });
    }

    public void connect(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                connection = connectionClass.CON();
                if (connection == null){
                    str = "Error in connection with MySQL server";
                }else{
                    str = "Connected with MySQL server";
                }
            }catch (Exception e){
                throw new RuntimeException(e);
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
            });
        });
    }
}