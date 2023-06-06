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
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShareActivity extends AppCompatActivity {

    ListAdapter adapter;
    TextView emptyState;
    RecyclerView recyclerView;
    ExtendedFloatingActionButton shareFab;
    ArrayList<RecyclerViewItem> shoppingList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        recyclerView = findViewById(R.id.rv_share_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ListAdapter(this, shoppingList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

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
        shareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.AlertDialog.Builder whoToShareDialog = new AlertDialog.Builder(ShareActivity.this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                EditText etDialog = new EditText(ShareActivity.this);
                whoToShareDialog.setView(etDialog);
                whoToShareDialog.setTitle("Who do you want to share with?");
                etDialog.setLayoutParams(params);
                etDialog.setHint("Email");
                whoToShareDialog.setCancelable(true);
                whoToShareDialog.setPositiveButton("Share", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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

}