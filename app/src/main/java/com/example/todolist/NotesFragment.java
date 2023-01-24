package com.example.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class NotesFragment extends Fragment implements View.OnClickListener {

    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragmet_notes, container, false);
        fab = v.findViewById(R.id.btn_add_note);
        fab.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v == fab) {
            if (getActivity() != null) {
                android.app.AlertDialog.Builder noteDialog = new AlertDialog.Builder(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                EditText etDialog = new EditText(getActivity());
                noteDialog.setView(etDialog);
                etDialog.setLayoutParams(params);
                etDialog.setBackground(null);
                etDialog.setHint("Write your note");
                noteDialog.setCancelable(true);
                noteDialog.setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://todo-list-d62c4-default-rtdb.firebaseio.com/");
                        DatabaseReference myRef = database.getReference("notes").push();

                        myRef.setValue(etDialog.getText().toString());
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
    }
}
