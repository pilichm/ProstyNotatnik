package pl.pilichm.prostynotatnik

import android.content.SharedPreferences
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.pilichm.prostynotatnik.recyclerview.Constants
import pl.pilichm.prostynotatnik.recyclerview.Note
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Util {
    companion object {
        /**
         * Function for saving created note.
         * */
        fun saveNote(sharedPreferences: SharedPreferences, noteText: String, currDate: String){
            val editor = sharedPreferences.edit()
            val serializedNote = Json.encodeToString(Note(noteText, currDate))

            /**
             * Get value of newest note id. Use zero if no note exists.
             */
            val newNoteId = if (sharedPreferences.contains(Constants.EXTRA_KEY_MAX_NOTE_ID)){
                sharedPreferences.getInt(Constants.EXTRA_KEY_MAX_NOTE_ID, 0)
            } else {
                0
            }

            /**
             * Save list of ids for all saved notes.
             */
            if (sharedPreferences.contains(Constants.EXTRA_KEY_LIST_OF_NOTES_ID)){
                val listOfNotesIds = sharedPreferences.getString(Constants.EXTRA_KEY_LIST_OF_NOTES_ID, "")
                editor.putString(Constants.EXTRA_KEY_LIST_OF_NOTES_ID, "$listOfNotesIds $newNoteId")
            } else {
                editor.putString(Constants.EXTRA_KEY_LIST_OF_NOTES_ID, "$newNoteId")
            }

            editor.putString(newNoteId.toString(), serializedNote)
            editor.putInt(Constants.EXTRA_KEY_MAX_NOTE_ID, newNoteId+1)

            editor.apply()
        }

        /**
         * Get current date as string.
         * */
        public fun getCurrentDateAsString(): String {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
            return  current.format(formatter)
        }
    }
}