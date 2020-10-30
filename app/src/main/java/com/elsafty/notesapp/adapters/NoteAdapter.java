package com.elsafty.notesapp.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.elsafty.notesapp.R;
import com.elsafty.notesapp.entities.Note;
import com.elsafty.notesapp.listeners.OnNoteClickedListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {
    private List<Note> items = new ArrayList<>();
    private final Context mContext;
    private OnNoteClickedListener onNoteClickedListener;

    public NoteAdapter(Context mContext, OnNoteClickedListener onNoteClickedListener) {
        this.mContext = mContext;
        this.onNoteClickedListener = onNoteClickedListener;
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
                onNoteClickedListener.onClick(items.get(position),position);
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
            textDateTime.setText(note.getDateTime());
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
}