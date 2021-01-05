package com.elsafty.notesapp.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.elsafty.notesapp.R;
import com.elsafty.notesapp.adapters.NoteAdapter;
import com.elsafty.notesapp.database.NoteDatabase;
import com.elsafty.notesapp.entities.Note;
import com.elsafty.notesapp.listeners.OnNoteClickedListener;
import com.elsafty.notesapp.listeners.OnNoteLongClickedListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements OnNoteClickedListener, OnNoteLongClickedListener {

    private static final int REQUEST_CODE_ADD_NOTE = 438;
    private static final int REQUEST_CODE_UPDATE_NOTE = 721;
    private static final int REQUEST_CODE_SHOW_NOTES = 930;
    private static final int REQUEST_CODE_SELECT_IMAGE = 799;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 201;
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
    @BindView(R.id.emptyView)
    LinearLayout emptyView;
    private NoteAdapter mAdapter;
    private List<Note> mNotes;
    private int noteClickedPosition = -1;
    private AlertDialog alertDialogAddUrl;
    private ActionMode mActionMode;
    private ActionMode.Callback mCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        mAdapter = new NoteAdapter(this, this, this);
        mNotes = new ArrayList<>();
        mCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                if (mActionMode != null) {
                    return false;
                }
                mode.getMenuInflater().inflate(R.menu.main_actoin_mode, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.shareActionMenu:
                        Toast.makeText(MainActivity.this, "Share Action", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    case R.id.deleteActionMenu:
                        Toast.makeText(MainActivity.this, "Delete Action", Toast.LENGTH_SHORT).show();
                        mode.finish();
                        return true;
                    default:
                        return false;

                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
            }
        };
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
        getAllNotes(REQUEST_CODE_SHOW_NOTES, false);

        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.searchNotes(inputSearch.getText().toString());
            }
        });

        imageAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
            }
        });
        imageAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
                } else {
                    selectImage();
                }
            }
        });

        imageAddLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showLinkDialog();
            }
        });

    }

    private void showLinkDialog() {
        if (alertDialogAddUrl == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.link_dialog_layout,
                    (ViewGroup) findViewById(R.id.layout_linkDialog));
            builder.setView(view);
            alertDialogAddUrl = builder.create();
            if (alertDialogAddUrl.getWindow() != null) {
                alertDialogAddUrl.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            EditText inputUrl = view.findViewById(R.id.input_linkAddress);
            inputUrl.requestFocus();
            view.findViewById(R.id.text_addAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputUrl.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputUrl.getText().toString().trim()).matches()) {
                        Toast.makeText(MainActivity.this, "Sorry!, Enter Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        alertDialogAddUrl.dismiss();
                        String url = inputUrl.getText().toString().trim();
                        Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions", true);
                        intent.putExtra("quickActionType", "url");
                        intent.putExtra("stringUrl", url);
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                        inputUrl.setText("");
                    }
                }
            });
            view.findViewById(R.id.text_cancelAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialogAddUrl.dismiss();
                }
            });
        }
        alertDialogAddUrl.show();
    }


    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver().query(contentUri,
                null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int dataColumn = cursor.getColumnIndex("_data");
            filePath = cursor.getString(dataColumn);
            cursor.close();
        }
        return filePath;
    }

    private void getAllNotes(int requestCode, boolean isNoteDeleted) {

        class GetNotesAsyncTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                return NoteDatabase.getNoteDatabase(getApplicationContext()).noteDao()
                        .getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if (notes.size() == 0) {
                    emptyView.setVisibility(View.VISIBLE);
                } else {
                    emptyView.setVisibility(View.GONE);
                }
                if (requestCode == REQUEST_CODE_SHOW_NOTES) {
                    mNotes.addAll(notes);
                    mAdapter.notifyDataSetChanged();
                } else if (requestCode == REQUEST_CODE_ADD_NOTE) {
                    mNotes.add(0, notes.get(0));
                    mAdapter.notifyItemInserted(0);
                    notesRecyclerView.smoothScrollToPosition(0);
                } else if (requestCode == REQUEST_CODE_UPDATE_NOTE) {
                    mNotes.remove(noteClickedPosition);
                    if (isNoteDeleted) {
                        mAdapter.notifyItemRemoved(noteClickedPosition);
                    } else {
                        mNotes.add(noteClickedPosition, notes.get(noteClickedPosition));
                        mAdapter.notifyItemChanged(noteClickedPosition);
                    }
                }

            }
        }
        new GetNotesAsyncTask().execute();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_NOTE && resultCode == RESULT_OK) {
            getAllNotes(REQUEST_CODE_ADD_NOTE, false);
        } else if (requestCode == REQUEST_CODE_UPDATE_NOTE && resultCode == RESULT_OK) {
            if (data != null) {
                getAllNotes(REQUEST_CODE_UPDATE_NOTE, data.getBooleanExtra("isNoteDeleted", false));
            }
        } else if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        String selectedImagePath = getPathFromUri(selectedImageUri);
                        Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions", true);
                        intent.putExtra("quickActionType", "image");
                        intent.putExtra("imagePath", selectedImagePath);
                        startActivityForResult(intent, REQUEST_CODE_ADD_NOTE);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

            }
        }
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
    public void onClick(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(this, CreateNoteActivity.class);
        intent.putExtra("note", note);
        intent.putExtra("isViewOrUpdate", true);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_NOTE);
    }

    @Override
    public void onLongClicked(Note note, int position) {
        mActionMode = startSupportActionMode(mCallback);
    }
}