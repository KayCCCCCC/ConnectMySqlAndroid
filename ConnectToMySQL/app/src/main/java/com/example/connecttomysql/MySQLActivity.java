package com.example.connecttomysql;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MySQLActivity extends AppCompatActivity {
    Connection conn;
    ListView lv_Users;
    ArrayList<User> users;
    UserAdapter userAdapter;
    Button btn_add, btn_update, btn_delete;
    EditText edt_fullName, edt_email, edt_phone, edt_address;
    TextView tv_message, txt_userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_my_sqlactivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // References
        lv_Users = (ListView) findViewById(R.id.lv_Users);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_update = (Button) findViewById(R.id.btn_update);
        btn_delete = (Button) findViewById(R.id.btn_delete);
        edt_fullName = (EditText) findViewById(R.id.edt_fullname);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        edt_address = (EditText) findViewById(R.id.edt_address);
        tv_message = (TextView) findViewById(R.id.tv_message);
        txt_userId = (TextView) findViewById(R.id.txt_userId);


        // Action handlers
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addUser();
            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUser();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });

        // Set default list view elements
        setDefault();

        // Get list user
        getAllUser();
    }

    // Check valid data inputs
    private boolean checkValidData(){
        // Validations
        if(edt_fullName.getText() == null
                || edt_fullName.getText().toString().isEmpty()){
            edt_fullName.setError("FullName is required");
            return false;
        }
        if(edt_email.getText() == null
                || edt_email.getText().toString().isEmpty()){
            edt_email.setError("Email is required");
            return false;
        }
        if(edt_phone.getText() == null
                || edt_phone.getText().toString().isEmpty()){
            edt_phone.setError("Phone is required");
            return false;
        }

        return true;
    }

    private void setDefault(){
        btn_update.setVisibility(View.INVISIBLE);
        btn_delete.setVisibility(View.INVISIBLE);

        edt_fullName.setText("");
        edt_email.setText("");
        edt_phone.setText("");
        edt_address.setText("");
        tv_message.setText("Click item to Update or Delete!");
    }

    private void getAllUser(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                // Initiate user list
                users = new ArrayList<>();

                // Initiate MySQL Connection
                conn = ConnectionClass.getConnection();
                String query = "SELECT * FROM prm392.user";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                ResultSet resultSet = preparedStatement.executeQuery();
                // Initiate user instance
                User user;
                while (resultSet.next()){
                    user = new User();

                    user.setId(resultSet.getInt("iduser"));
                    user.setName(resultSet.getString("name"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPhone(resultSet.getString("phone"));
                    user.setAddress(resultSet.getString("address"));

                    // Add user to list
                    users.add(user);
                }

                runOnUiThread(() -> {
                    // Initiate user adapter and set it to ListView
                    userAdapter = new UserAdapter(MySQLActivity.this, users, R.layout.list_view_row);
                    lv_Users.setAdapter(userAdapter);
                });
            }catch (SQLException sqlException){
                sqlException.printStackTrace();
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void addUser(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try{
                // Check valid data inputs
                checkValidData();

                // Initiate MySQL Connection
                conn = ConnectionClass.getConnection();
                String query = "INSERT INTO prm392.user (name, email, phone, address)" +
                        " VALUES (?,?,?,?)";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, edt_fullName.getText().toString());
                preparedStatement.setString(2, edt_email.getText().toString());
                preparedStatement.setString(3, edt_phone.getText().toString());
                preparedStatement.setString(4, edt_address.getText().toString());

                // Insert
                int rowEffected = preparedStatement.executeUpdate();
                if(rowEffected > 0){
                    runOnUiThread(() -> {
                        Toast.makeText(this, "Add new user success", Toast.LENGTH_LONG).show();
                        // Set default input elements
                        setDefault();
                    });
                }

                // Get all user
                getAllUser();
            }catch (SQLException sqlException){
                sqlException.printStackTrace();
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void updateUser(){
        // Progress update
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                // Check valid data inputs
                checkValidData();

                // Initiate MySQL Connection
                conn = ConnectionClass.getConnection();
                String query = "UPDATE prm392.user" +
                        " SET name=?, email=?, phone=?, address=?" +
                        " WHERE iduser=?";
                PreparedStatement preparedStatement = conn.prepareStatement(query);
                preparedStatement.setString(1, edt_fullName.getText().toString());
                preparedStatement.setString(2, edt_email.getText().toString());
                preparedStatement.setString(3, edt_phone.getText().toString());
                preparedStatement.setString(4, edt_address.getText().toString());
                preparedStatement.setString(5, String.valueOf(txt_userId.getText()));

                // Update
                int rowEffected = preparedStatement.executeUpdate();
                if (rowEffected > 0) {
                    runOnUiThread(() -> {
                        Toast.makeText(MySQLActivity.this, "Update user success", Toast.LENGTH_LONG).show();
                        // Set default input elements
                        setDefault();
                    });
                }

                // Get all user
                getAllUser();
            } catch (SQLException sqlException) {
                sqlException.printStackTrace();
            }

            runOnUiThread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    private void deleteUser() {
        // Create a confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm Delete")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    // Progress update
                    ExecutorService executorService = Executors.newSingleThreadExecutor();
                    executorService.execute(() -> {
                        try {
                            // Check valid data inputs
                            checkValidData();

                            // Initiate MySQL Connection
                            conn = ConnectionClass.getConnection();
                            String query = "DELETE FROM prm392.user WHERE iduser=?";
                            PreparedStatement preparedStatement = conn.prepareStatement(query);
                            preparedStatement.setString(1, String.valueOf(txt_userId.getText()));

                            // Delete
                            int rowEffected = preparedStatement.executeUpdate();
                            if (rowEffected > 0) {
                                runOnUiThread(() -> {
                                    Toast.makeText(this, "Delete user success", Toast.LENGTH_LONG).show();
                                    // Set default input elements
                                    setDefault();
                                });
                            }

                            // Get all user
                            getAllUser();
                        } catch (SQLException sqlException) {
                            sqlException.printStackTrace();
                        }

                        runOnUiThread(() -> {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create()
                .show();
    }
}