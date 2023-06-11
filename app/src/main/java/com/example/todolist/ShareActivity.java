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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity implements View.OnTouchListener {

    ListAdapter adapter;
    TextView emptyState;
    RecyclerView recyclerView;
    FloatingActionButton shareFab;
    ArrayList<RecyclerViewItem> shoppingList = new ArrayList<>();
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.rv_share_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(this, shoppingList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        mAuth = FirebaseAuth.getInstance();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingList.clear();
                for (DataSnapshot noteSnapshot : dataSnapshot.getChildren()) {
                    RecyclerViewItem currentNote = noteSnapshot.getValue(RecyclerViewItem.class);
                    shoppingList.add(currentNote);
                }
                adapter.notifyDataSetChanged(); // Update adapter with new data

                if (shoppingList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        emptyState = findViewById(R.id.empty_state);

        shareFab = findViewById(R.id.share_fab);
        shareFab.setOnTouchListener(this);
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder whoToShareDialog = new AlertDialog.Builder(ShareActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                EditText etDialog = new EditText(ShareActivity.this);
                whoToShareDialog.setView(etDialog);
                whoToShareDialog.setTitle("Who do you want to share with?");
                etDialog.setLayoutParams(params);
                etDialog.setPadding(30, 30, 30, 30);
                etDialog.setHint("Email");
                whoToShareDialog.setCancelable(true);
                whoToShareDialog.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!etDialog.getText().toString().isEmpty())
                            checkingDialog(etDialog.getText().toString());
                        else
                            Toast.makeText(ShareActivity.this, "Please provide an Email", Toast.LENGTH_SHORT).show();
                    }
                });
                whoToShareDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                whoToShareDialog.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.share_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        if (id == R.id.share_menu_add_product) {
            android.app.AlertDialog.Builder addProductDialog = new AlertDialog.Builder(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            EditText etDialog = new EditText(this);
            addProductDialog.setView(etDialog);
            etDialog.setLayoutParams(params);
            etDialog.setBackground(null);
            etDialog.setHint("name of product");
            addProductDialog.setCancelable(true);
            addProductDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String nameOfProduct = etDialog.getText().toString();
                    if (!nameOfProduct.isEmpty()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share");
                        String key = myRef.push().getKey(); // Get new key for the product
                        myRef.child(key).setValue(new RecyclerViewItem(nameOfProduct, false, key)); // Add product to Firebase with new key
                    } else
                        Toast.makeText(ShareActivity.this, "write the name of the product", Toast.LENGTH_SHORT).show();
                }
            });
            addProductDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            addProductDialog.show();

        } else if (id == R.id.share_menu_delete) {
            // dialog to check if the user really want to delete
            android.app.AlertDialog.Builder checkingDialog = new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to delete all the checked products?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteCheckedItems();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            checkingDialog.show();
        } else if (id == R.id.share_menu_delete_all) {
            // dialog to check if the user really want to delete
            android.app.AlertDialog.Builder checkingDialog = new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to delete all products?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                            DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share");
                            myRef.removeValue();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            checkingDialog.show();
        } else if (id == android.R.id.home) {
            onBackPressed();  // This will navigate back to the previous activity
            return true;
        }
        return true;
    }

    private void deleteCheckedItems() {
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share");

        // Query to filter only the checked items
        Query checkedItemsQuery = myRef.orderByChild("selected").equalTo(true);

        // Remove the checked items from the database
        checkedItemsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    snapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void checkingDialog(String email) {
        android.app.AlertDialog.Builder checkingDialog = new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to share this list?")
                .setCancelable(true)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                        DatabaseReference usersRef = database.getReference("users");

                        // Retrieve the user ID based on the provided email;
                        Query query = usersRef.orderByChild("email").equalTo(email).limitToFirst(1);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        String userId = snapshot.child("userId").getValue(String.class);
                                        shareList(userId);
                                    }
                                } else
                                    Toast.makeText(ShareActivity.this, "No user found for email: " + email, Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(ShareActivity.this, "Data retrieval canceled: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
        checkingDialog.show();

    }

    public void shareList(String userId) {
        // Get reference to the user's list node
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
        DatabaseReference userListNode = database.getReference("lists/" + userId);

        // Write the shopping list items to the user's list node
        for (RecyclerViewItem item : shoppingList) {
            String key = userListNode.push().getKey(); // Generate a new key for each item
            userListNode.child(key).setValue(item); // Write the item to the Firebase database
        }
        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share");
        myRef.removeValue();
        Toast.makeText(ShareActivity.this, "List shared successfully!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Button is being touched
                shareFab.setAlpha(0.5f); // Set alpha to 0.5 (half-transparent)
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Touch is released or canceled
                shareFab.setAlpha(1.0f); // Restore original alpha (fully opaque)
                break;
        }
        return false;
    }
}