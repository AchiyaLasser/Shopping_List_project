package com.example.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private List<RecyclerViewItem> shoppingList;
    private LayoutInflater inflater;
    private Context context;


    public ListAdapter(Context context, List<RecyclerViewItem> shoppingList) {
        this.inflater = LayoutInflater.from(context);
        this.shoppingList = shoppingList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecyclerViewItem currentNote = shoppingList.get(position);
        holder.textView.setText(currentNote.getText());
        holder.checkBox.setChecked(currentNote.isSelected());
        holder.tvAmount.setText(currentNote.getAmountOfProduct() + "");
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    private void toggleSelection(int position, CheckBox checkBox) {
        RecyclerViewItem item = shoppingList.get(position);
        item.setSelected(!item.isSelected());
        checkBox.setChecked(item.isSelected());
        DatabaseReference myRef = null;
        if (context instanceof ShareActivity)
            myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                    .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share" + "/" + item.getKey());
        if (context instanceof MainActivity)
            myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                    .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());
        myRef.child("selected").setValue(item.isSelected());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        CheckBox checkBox;
        Button btnPlus, btnMinus;
        TextView tvAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title);
            checkBox = itemView.findViewById(R.id.check_box);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus.setOnClickListener(this);
            btnMinus.setOnClickListener(this);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            checkBox.setOnClickListener(this);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Do yo want to delete product or change the product's name?")
                            .setCancelable(true)
                            .setPositiveButton("Change name", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    AlertDialog.Builder changeNameDialog = new AlertDialog.Builder(context);
                                    EditText etDialog = new EditText(context);
                                    changeNameDialog.setView(etDialog);
                                    etDialog.setHint("new name of product");
                                    etDialog.setBackground(null);
                                    changeNameDialog.setCancelable(true);
                                    changeNameDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String nameOfProduct = etDialog.getText().toString();
                                            if (!nameOfProduct.isEmpty()) {
                                                RecyclerViewItem item = shoppingList.get(getAdapterPosition());
                                                DatabaseReference myRef = null;
                                                if (context instanceof ShareActivity)
                                                    myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                                                            .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share" + "/" + item.getKey());
                                                else if (context instanceof MainActivity)
                                                    myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                                                            .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());
                                                myRef.child("text").setValue(nameOfProduct);
                                            } else
                                                Toast.makeText(context, "write the name of the product", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    changeNameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                                    changeNameDialog.show();

                                }
                            })
                            .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    RecyclerViewItem item = shoppingList.get(getAdapterPosition());
                                    DatabaseReference myRef = null;
                                    if (context instanceof ShareActivity)
                                        myRef = FirebaseDatabase
                                                .getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                                                .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share" + "/" + item.getKey());
                                    else if (context instanceof MainActivity)
                                        myRef = FirebaseDatabase
                                                .getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                                                .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());
                                    myRef.removeValue();
                                }
                            })
                            .show();
                    return true;
                }
            });

        }

        @Override
        public void onClick(View v) {
            if (v == checkBox) {
                toggleSelection(getAdapterPosition(), checkBox);
            }
            else if (v == btnPlus) {
                btnMinus.setEnabled(true);
                RecyclerViewItem item = shoppingList.get(getAdapterPosition());
                int amount = Integer.parseInt(tvAmount.getText().toString()) + 1;
                tvAmount.setText(amount + "");

                DatabaseReference myRef = null;
                if (context instanceof ShareActivity)
                    myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                            .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share" + "/" + item.getKey());

                else if (context instanceof MainActivity)
                    myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                            .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());

                myRef.child("amountOfProduct").setValue(amount);
                if (amount == 999)
                    btnPlus.setEnabled(false);
            } else if (v == btnMinus) {
                btnPlus.setEnabled(true);
                RecyclerViewItem item = shoppingList.get(getAdapterPosition());
                int amount = Integer.parseInt(tvAmount.getText().toString()) - 1;
                tvAmount.setText(amount + "");

                DatabaseReference myRef = null;
                if (context instanceof ShareActivity)
                    myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                            .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "-share" + "/" + item.getKey());

                else if (context instanceof MainActivity)
                    myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/")
                            .getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());

                myRef.child("amountOfProduct").setValue(amount);
                if (amount == 1)
                    btnMinus.setEnabled(false);
            }
        }
    }
}
