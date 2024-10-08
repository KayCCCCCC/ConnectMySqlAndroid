package com.example.connecttomysql;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UserAdapter extends BaseAdapter {
    private Connection conn;
    private Context context;
    private ArrayList<User> users;
    private int rowLayout;
    private int positionSelected = -1;

    public UserAdapter(Context context, ArrayList<User> users, int rowLayout) {
        this.context = context;
        this.users = users;
        this.rowLayout = rowLayout;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int i) {
        return users.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        view = inflater.inflate(rowLayout, null);

        // Row view references
        TextView txt_fullName = (TextView) view.findViewById(R.id.txt_fullName);
        TextView txt_email = (TextView) view.findViewById(R.id.txt_email);
        TextView txt_phone = (TextView) view.findViewById(R.id.txt_phone);
        TextView txt_address = (TextView) view.findViewById(R.id.txt_address);

        // Assign user data
        User user = users.get(i);
        txt_fullName.setText(user.getName());
        txt_email.setText(user.getEmail());
        txt_phone.setText(user.getPhone());
        txt_address.setText(user.getAddress());

        // Get ListView elements
        TextView txt_userId = ((Activity) context).findViewById(R.id.txt_userId);
        TextView tv_message = ((Activity) context).findViewById(R.id.tv_message);
        EditText edt_fullName = ((Activity) context).findViewById(R.id.edt_fullname);
        EditText edt_email = ((Activity) context).findViewById(R.id.edt_email);
        EditText edt_phone = ((Activity) context).findViewById(R.id.edt_phone);
        EditText edt_address = ((Activity) context).findViewById(R.id.edt_address);
        Button btn_update = ((Activity) context).findViewById(R.id.btn_update);
        Button btn_delete = ((Activity) context).findViewById(R.id.btn_delete);

        // Handle view onClick
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Assign user data (for update purpose)
                txt_userId.setText(String.valueOf(user.getId()));
                edt_fullName.setText(user.getName());
                edt_email.setText(user.getEmail());
                edt_phone.setText(user.getPhone());
                edt_address.setText(user.getAddress());

                // Visible action when OnClickComponent
                btn_update.setVisibility(View.VISIBLE);
                btn_delete.setVisibility(View.VISIBLE);
                tv_message.setText("");

                Toast.makeText(context, "Selected user: " + user.getName(), Toast.LENGTH_LONG).show();
                positionSelected = i;
                notifyDataSetChanged();
            }
        });


        // Assign row list view constraint layout
        ConstraintLayout constraintLayout = (ConstraintLayout) view.findViewById(R.id.list_view_row);
        if (positionSelected == i) {
            constraintLayout.setBackgroundColor(Color.GRAY);
        } else {
            constraintLayout.setBackgroundColor(Color.WHITE);
        }
        return view;
    }
}
