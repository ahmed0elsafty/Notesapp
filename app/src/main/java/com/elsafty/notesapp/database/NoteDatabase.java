package com.elsafty.notesapp.database;

import android.content.Context;

import com.elsafty.notesapp.dao.NoteDao;
import com.elsafty.notesapp.entities.Note;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = Note.class, version = 1, exportSchema = false)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase noteDatabase;

    public static synchronized NoteDatabase getNoteDatabase(Context context) {
        if (noteDatabase == null) {
            noteDatabase = Room.databaseBuilder(context,
                    NoteDatabase.class,
                    "Notes_db").build();
        }
        return noteDatabase;
    }

    public abstract NoteDao noteDao();
}
