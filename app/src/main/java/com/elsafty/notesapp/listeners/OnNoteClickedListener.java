package com.elsafty.notesapp.listeners;

import com.elsafty.notesapp.entities.Note;

public interface OnNoteClickedListener {
    void onClick(Note note,int position);
}
