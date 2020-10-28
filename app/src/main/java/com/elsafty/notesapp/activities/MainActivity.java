package com.elsafty.notesapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.elsafty.notesapp.R;
import com.elsafty.notesapp.adapters.NoteAdapter;
import com.elsafty.notesapp.database.NoteDatabase;
import com.elsafty.notesapp.entities.Note;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ADD_NOTE = 438;
    @BindView(R.id.input_search)
    EditText inputSearch;
    @BindView(R.id.imageAddNote)
    ImageView imageAddNote;
    @BindView(R.id.imageAddImage)
    ImageView imageAddImage;
    @BindView(R.id.imageAddLink)
    ImageView imageAddLink;
    @BindView(R.id.imageAddNoteMain)
    ImageView imageAddNoteMain;
    @BindView(R.id.notes_recyclerView)
    RecyclerView notesRecyclerView;
    private NoteAdapter mAdapter;
    private List<Note> mNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAdapter = new NoteAdapter(this);
        mNotes = new ArrayList<>();
        imageAddNoteMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });
        mAdapter.setList(mNotes);
        notesRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        notesRecyclerView.setAdapter(mAdapter);
        getAllNotes();
    }

    private void getAllNotes() {
        class GetNotesAsyncTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao()
                        .getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (mNotes.size() == 0) {
                    mNotes.addAll(notes);
                    mAdapter.notifyDataSetChanged();
                } else {
                    mNotes.add(0, notes.get(0));
                    mAdapter.notifyItemInserted(0);
                }
                notesRecyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesAsyncTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getAllNotes();
        }
    }
}