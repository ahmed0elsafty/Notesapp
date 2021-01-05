package com.elsafty.notesapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.elsafty.notesapp.R;
import com.elsafty.notesapp.entities.Note;
import com.elsafty.notesapp.listeners.OnNoteClickedListener;
import com.elsafty.notesapp.listeners.OnNoteLongClickedListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> items = new ArrayList<>();
    private final Context mContext;
    private OnNoteClickedListener onNoteClickedListener;
    private OnNoteLongClickedListener onNoteLongClickedListener;
    private List<Note> searchNotes;
    private Timer timer;
    private List<Note> selectedNotes;

    public NoteAdapter(Context mContext, OnNoteClickedListener onNoteClickedListener, OnNoteLongClickedListener onNoteLongClickedListener) {
        this.mContext = mContext;
        this.onNoteClickedListener = onNoteClickedListener;
        this.onNoteLongClickedListener = onNoteLongClickedListener;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.setNote(items.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNoteClickedListener.onClick(items.get(position), position);
            }
        });
        holder.layoutNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onNoteLongClickedListener.onLongClicked(items.get(position), position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setList(List<Note> items) {
        this.items = items;
        searchNotes = items;
        notifyDataSetChanged();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        private TextView textTitle;
        private TextView textSubtitle;
        private TextView textDateTime;
        private LinearLayout layoutNote;
        private RoundedImageView noteImage;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.text_title);
            textSubtitle = itemView.findViewById(R.id.text_subtitle);
            textDateTime = itemView.findViewById(R.id.text_date_time);
            noteImage = itemView.findViewById(R.id.image_note);
            layoutNote = itemView.findViewById(R.id.layout_note);
        }

        void setNote(Note note) {
            textTitle.setText(note.getTitle());
            if (note.getSubtitle().trim().isEmpty()) {
                textSubtitle.setVisibility(View.GONE);
            } else {
                textSubtitle.setText(note.getSubtitle());
            }
            textDateTime.setText(parseDate(note.getDateTime()));
            GradientDrawable gradientDrawable = (GradientDrawable) layoutNote.getBackground();
            if (note.getColor() != null) {
                gradientDrawable.setColor(Color.parseColor(note.getColor()));
            } else {
                gradientDrawable.setColor(Color.parseColor("#333333"));
            }

            if (note.getImagePath() != null) {
                Bitmap bitmap = BitmapFactory.decodeFile(note.getImagePath());
                noteImage.setVisibility(View.VISIBLE);
                noteImage.setImageBitmap(bitmap);
            } else {
                noteImage.setVisibility(View.GONE);
            }

        }
    }

    public void searchNotes(String searchKeyword) {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    items = searchNotes;
                } else {
                    ArrayList<Note> temp = new ArrayList<>();
                    for (Note note : items) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) ||
                                note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            temp.add(note);
                        }
                    }
                    items = temp;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        }, 500);
    }

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private String parseDate(String dateStr) {

        try {

            DateFormat srcDf = new SimpleDateFormat("EEEE,dd MMMM yyyy HH:mm a");

            // parse the date string into Date object
            Date date = srcDf.parse(dateStr);

            DateFormat destDf = new SimpleDateFormat("EE,dd MMM yyyy HH:mm a");

            // format the date into another format
            dateStr = destDf.format(date);


        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateStr;
    }
}