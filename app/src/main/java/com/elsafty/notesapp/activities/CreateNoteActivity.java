package com.elsafty.notesapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
    @BindView(R.id.imageNote)
    ImageView imageNote;
    private String selectedNoteColor = "#333333";
    private String selectedImagePath = "";
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 720;
    private static final int REQUEST_CODE_SELECT_IMAGE = 246;
    private static final String TAG = CreateNoteActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        ButterKnife.bind(this);
        textDateTime.setText(new SimpleDateFormat("EEEE,dd MMMM yyyy HH:mm a",
                Locale.getDefault()).format(new Date()));
        initLayoutMiscellaneous();
        setSubtitleIndecatorColor();

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
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);
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

    private void initLayoutMiscellaneous() {
        LinearLayout layoutMiscellaneous = findViewById(R.id.layout_miscellaneous);
        BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutMiscellaneous);
        layoutMiscellaneous.findViewById(R.id.text_miscellaneous).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutMiscellaneous.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutMiscellaneous.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutMiscellaneous.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutMiscellaneous.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutMiscellaneous.findViewById(R.id.imageColor5);

        layoutMiscellaneous.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_done);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_done);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_done);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#3A52FC";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_done);
                imageColor5.setImageResource(0);
                setSubtitleIndecatorColor();
            }
        });
        layoutMiscellaneous.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_done);
                setSubtitleIndecatorColor();
            }
        });

        layoutMiscellaneous.findViewById(R.id.layout_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateNoteActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }
            }
        });


    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    private void setSubtitleIndecatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) subtitleIndecator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                try {
                    if (selectedImageUri!=null){
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap imageBitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(imageBitmap);
                        imageNote.setVisibility(View.VISIBLE);
                    }else {
                        imageNote.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                selectedImagePath = getPathFromUri(selectedImageUri);
            }
        }
    }

    private String getPathFromUri(Uri contentUri){
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri,
                null,null,null,null);
        if (cursor==null){
            filePath = contentUri.getPath();
        }else {
            cursor.moveToFirst();
            int dataColumn = cursor.getColumnIndex("_data");
            filePath = cursor.getString(dataColumn);
            cursor.close();
        }
        return filePath;
    }
}