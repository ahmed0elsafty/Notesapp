package com.elsafty.notesapp.listeners;

import com.elsafty.notesapp.entities.Note;

public interface OnNoteLongClickedListener {
    void onLongClicked(Note note,int position);
}
