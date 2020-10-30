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
import com.elsafty.notesapp.listeners.OnNoteClickedListener;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnNoteClickedListener {

    private static final int REQUEST_CODE_ADD_NOTE = 438;
    private static final int REQUEST_CODE_UPDATE_NOTE = 721;
    private static final int REQUEST_CODE_SHOW_NOTES = 930;
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
    private int noteClicledPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mAdapter = new NoteAdapter(this, this);
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
        getAllNotes(REQUEST_CODE_SHOW_NOTES);
    }

    private void getAllNotes(int requestCode) {
        class GetNotesAsyncTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao()
                        .getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    mNotes.addAll(notes);
                    mAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    mNotes.add(0, notes.get(0));
                    mAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    mNotes.remove(noteClicledPosition);
                    mNotes.add(noteClicledPosition, notes.get(noteClicledPosition));
                    mAdapter.notifyItemChanged(noteClicledPosition);
                }

            }
        }
        new GetNotesAsyncTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getAllNotes(REQUEST_CODE_ADD_NOTE);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getAllNotes(REQUEST_CODE_UPDATE_NOTE);
            }
        }
    }

    @Override
    public void onClick(Note note, int position) {
        noteClicledPosition = position;
        Intent intent = new Intent(this, CreateNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("isViewOrUpdate", true);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }
}