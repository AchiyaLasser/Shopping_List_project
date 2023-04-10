package com.example.todolist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

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
        holder.etAmount.setText(currentNote.getAmountOfProduct() + "");
    }

    @Override
    public int getItemCount() {
        return shoppingList.size();
    }

    private void toggleSelection(int position, CheckBox checkBox) {
        RecyclerViewItem item = shoppingList.get(position);
        item.setSelected(!item.isSelected());
        checkBox.setChecked(item.isSelected());
        DatabaseReference myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/").getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());
        myRef.child("selected").setValue(item.isSelected());
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView textView;
        CheckBox checkBox;
        Button btnPlus, btnMinus;
        EditText etAmount;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tv_title);
            checkBox = itemView.findViewById(R.id.check_box);
            btnPlus = itemView.findViewById(R.id.btn_plus);
            btnMinus = itemView.findViewById(R.id.btn_minus);
            btnPlus.setOnClickListener(this);
            btnMinus.setOnClickListener(this);
            etAmount = itemView.findViewById(R.id.et_amount);
            checkBox.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v == checkBox)
                toggleSelection(getAdapterPosition(), checkBox);
            else if(v == btnPlus){
                btnMinus.setEnabled(true);
                RecyclerViewItem item = shoppingList.get(getAdapterPosition());
                int amount = Integer.parseInt(etAmount.getText().toString()) + 1;
                etAmount.setText(amount + "");
                DatabaseReference myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/").getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());
                myRef.child("amountOfProduct").setValue(amount);
                if(amount == 999)
                    btnPlus.setEnabled(false);
            }
            else if(v == btnMinus) {
                btnPlus.setEnabled(true);
                RecyclerViewItem item = shoppingList.get(getAdapterPosition());
                int amount = Integer.parseInt(etAmount.getText().toString()) - 1;
                etAmount.setText(amount + "");
                DatabaseReference myRef = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/").getReference("lists/" + FirebaseAuth.getInstance().getUid() + "/" + item.getKey());
                myRef.child("amountOfProduct").setValue(amount);
                if(amount == 1)
                    btnMinus.setEnabled(false);
            }
        }
    }
}
