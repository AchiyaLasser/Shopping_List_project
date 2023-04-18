package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNotificationActivity extends AppCompatActivity {

    Button btnChannel1;//rrtermj
    EditText etTitle, etMessage;
    NotificationHelper mNotificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notification);

        btnChannel1 = findViewById(R.id.btn_Channel);
        etTitle = findViewById(R.id.et_notification_title);
        etMessage = findViewById(R.id.et_notification_message);

        mNotificationHelper = new NotificationHelper(this);

        btnChannel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendOnChannel1(etTitle.getText().toString(), etMessage.getText().toString());
            }
        });
    }

    public void sendOnChannel1(String title, String message) {
        NotificationCompat.Builder nb = mNotificationHelper.getNotification(title, message);
        mNotificationHelper.getManager().notify(1, nb.build());
    }
}