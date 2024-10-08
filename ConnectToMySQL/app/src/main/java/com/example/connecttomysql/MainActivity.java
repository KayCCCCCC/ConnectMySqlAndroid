package com.example.connecttomysql;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

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
    Button btn_crudNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // References
        btn_crudNavigation = (Button) findViewById(R.id.btn_crudNavigation);

        // Btn click handler
        btn_crudNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { // On click button
                // Navigate to MySQLActivity
                Intent it = new Intent(MainActivity.this, MySQLActivity.class);
                startActivity(it);
            }
        });

        connectionClass = new ConnectionClass();
        connect();
    }

    public void btnClick(View view) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                connection = connectionClass.getConnection();
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
                connection = connectionClass.getConnection();
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