package com.elsafty.notesapp.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.elsafty.notesapp.R;
import com.elsafty.notesapp.database.NoteDatabase;
import com.elsafty.notesapp.entities.Note;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreateNoteActivity extends AppCompatActivity {

    @BindView(R.id.inputNoteTitle)
    EditText inputNoteTitle;
    @BindView(R.id.textDateTime)
    TextView textDateTime;
    @BindView(R.id.inputNoteSubtitle)
    EditText inputNoteSubtitle;
    @BindView(R.id.imageSave)
    ImageView imageSave;
    @BindView(R.id.imageBack)
    ImageView imageBack;
    @BindView(R.id.subtitle_indecator)
    View subtitleIndecator;
    @BindView(R.id.inputNote)
    EditText inputNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ButterKnife.bind(this);
        textDateTime.setText(new SimpleDateFormat("EEEE,dd MMMM yyyy HH:mm a",
                Locale.getDefault()).format(new Date()));
        initLayoutMiscellaneous();
    }

    private void saveNote() {
        if (inputNoteTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note title can't be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (inputNoteSubtitle.getText().toString().trim().isEmpty() &&
                inputNote.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Note can't be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        final Note note = new Note();
        note.setTitle(inputNoteTitle.getText().toString().trim());
        note.setSubtitle(inputNoteSubtitle.getText().toString().trim());
        note.setDateTime(textDateTime.getText().toString());
        note.setNoteText(inputNote.getText().toString().trim());
        class InsertNoteasyncTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                NoteDatabase.getNoteDatabase(getApplication()).noteDao().insertNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
        new InsertNoteasyncTask().execute();
    }

    @OnClick({R.id.imageSave, R.id.imageBack})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.imageSave:
                saveNote();
                break;
            case R.id.imageBack:
                onBackPressed();
                break;
        }
    }
    private void initLayoutMiscellaneous(){
        LinearLayout layoutMiscellaneous = findViewById(R.id.layout_miscellaneous);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.text_miscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState()!=BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }
}