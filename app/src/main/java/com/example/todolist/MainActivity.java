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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListAdapter adapter;
    TextView emptyState;
    RecyclerView recyclerView;
    ArrayList<RecyclerViewItem> shoppingList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.rv_shopping_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter with products list
        adapter = new ListAdapter(this, shoppingList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                shoppingList.clear();
                for(DataSnapshot noteSnapshot : dataSnapshot.getChildren()){
                    RecyclerViewItem currentNote = noteSnapshot.getValue(RecyclerViewItem.class);
                    shoppingList.add(currentNote);
                }
                adapter.notifyDataSetChanged(); // Update adapter with new data

                if(shoppingList.isEmpty()){
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
    }

    @Override
    public void onClick(View v) {

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
        else if(id == R.id.menu_add_product){
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
                    if(!nameOfProduct.isEmpty()) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid());
                        String key = myRef.push().getKey(); // Get new key for the product
                        myRef.child(key).setValue(new RecyclerViewItem(nameOfProduct, false, key)); // Add product to Firebase with new key
                    }
                    else
                        Toast.makeText(MainActivity.this, "write the name of the product", Toast.LENGTH_SHORT).show();
                }
            });
            addProductDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            addProductDialog.show();
        }
        else if(id == R.id.menu_delete){
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
        }
        else if(id == R.id.menu_delete_all){
            // dialog to check if the user really want to delete
            android.app.AlertDialog.Builder checkingDialog = new AlertDialog.Builder(this)
                    .setTitle("Are you sure you want to delete all products?")
                    .setCancelable(true)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                            DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid());
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
        DatabaseReference myRef = database.getReference("lists/" + FirebaseAuth.getInstance().getUid());

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
