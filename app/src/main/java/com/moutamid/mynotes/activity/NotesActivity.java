package com.moutamid.mynotes.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fxn.stash.Stash;
import com.google.android.material.button.MaterialButton;
import com.moutamid.mynotes.Constants;
import com.moutamid.mynotes.R;
import com.moutamid.mynotes.utils.AppCompatLineEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NotesActivity extends AppCompatActivity {

    private ImageView saveNotesBtn;
    private AppCompatLineEditText editText;
    MaterialButton savedNotesBtn;

    ArrayList<String> tasksArrayList = Stash.getArrayList(Constants.NOTES_LIST, String.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        saveNotesBtn = findViewById(R.id.saveBtnNotes);
        savedNotesBtn = findViewById(R.id.savedNotesBtn);
        saveNotesBtn.setOnClickListener(saveNotesBtnClickListener());

        editText = findViewById(R.id.notesEdittext);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.hasFocus() && count > 0)
                    saveNotesBtn.setVisibility(View.VISIBLE);
                else
                    saveNotesBtn.setVisibility(View.GONE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        initSavedNotesData();
    }

    private void initSavedNotesData() {
//        if (!tasksArrayList.get(0).equals("Error")) {
        if (tasksArrayList != null) {
            initRecyclerView();
            savedNotesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    initRecyclerView();

                    if (editText.getVisibility() == View.VISIBLE) {
                        editText.setVisibility(View.GONE);
                        savedNotesBtn.setText("Hide Saved Notes");
                        conversationRecyclerView.setVisibility(View.VISIBLE);
                    } else {
                        editText.setVisibility(View.VISIBLE);
                        savedNotesBtn.setText("Show Saved Notes");
                        conversationRecyclerView.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            savedNotesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toast("List is Empty!");

                }
            });
        }
    }

    private View.OnClickListener saveNotesBtnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText().toString().isEmpty()) {
                    toast("Please enter some text in the notes!");
                    return;
                }

                // THIS IS THE ARRAY LIST STORED IN THE SHARED PREFERENCES
                if (tasksArrayList == null) {
                    ArrayList<String> notesArrayList = new ArrayList<>();
                    notesArrayList.add(getDateTime() + "\n" + editText.getText().toString());
                    Stash.put(Constants.NOTES_LIST, notesArrayList);
                } else {
                    tasksArrayList.add(getDateTime() + "\n" + editText.getText().toString());
                    Stash.put(Constants.NOTES_LIST, tasksArrayList);
                }
                editText.setText("");
                toast("Saved!");
                saveNotesBtn.setVisibility(View.GONE);
            }
        };
    }

//    private ArrayList<String> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {

        conversationRecyclerView = findViewById(R.id.notesListRecyclerView);
        //conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        conversationRecyclerView.setAdapter(adapter);

        //    if (adapter.getItemCount() != 0) {

        //        noChatsLayout.setVisibility(View.GONE);
        //        chatsRecyclerView.setVisibility(View.VISIBLE);

        //    }

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_notes, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position1) {

            int position = holder.getAdapterPosition();

            holder.title.setText(tasksArrayList.get(position));

            holder.title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    new AlertDialog.Builder(NotesActivity.this)
                            .setTitle("Are you sure?")
                            .setMessage("Do you really want to delete this note?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    tasksArrayList.remove(position);
                                    adapter.notifyItemRemoved(position);
                                    Stash.put(Constants.NOTES_LIST, tasksArrayList);

                                    dialogInterface.dismiss();
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            })
                            .show();

                    return false;
                }
            });

        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView title;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                title = v.findViewById(R.id.titleTextview);

            }
        }

    }

    private void toast(String msg) {
        Toast.makeText(NotesActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private String getDateTime() {
        try {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
//            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            return sdf.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Error";
    }
}