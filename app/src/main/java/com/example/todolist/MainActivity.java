package com.example.todolist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FloatingActionButton fab;
    NotesAdapter adapter;
    TextView emptyState;
    RecyclerView recyclerView;
    ArrayList<String> notes = new ArrayList<>(); // moved notes to class level

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = findViewById(R.id.btn_add_note);
        fab.setOnClickListener(this);

        recyclerView = findViewById(R.id.recycler_view_notes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with notes list
        adapter = new NotesAdapter(this, notes);
        recyclerView.setAdapter(adapter);

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("notes/" + FirebaseAuth.getInstance().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                notes.clear();
                for(DataSnapshot noteSnapshot : dataSnapshot.getChildren()){
                    String currentNote = noteSnapshot.getValue(String.class);
                    notes.add(currentNote);
                }
                adapter.notifyDataSetChanged(); // Update adapter with new data
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onClick(View v) {
        if (v == fab) {
            android.app.AlertDialog.Builder noteDialog = new AlertDialog.Builder(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            EditText etDialog = new EditText(this);
            noteDialog.setView(etDialog);
            etDialog.setLayoutParams(params);
            etDialog.setBackground(null);
            etDialog.setHint("Write your note");
            noteDialog.setCancelable(true);
            noteDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                    DatabaseReference myRef = database.getReference("notes/" + FirebaseAuth.getInstance().getUid());
                    String note = etDialog.getText().toString();
                    String key = myRef.push().getKey(); // Get new key for the note
                    myRef.child(key).setValue(note); // Add note to Firebase with new key
                }
            });
            noteDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            noteDialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.menu_log_out) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        return true;
    }
}
