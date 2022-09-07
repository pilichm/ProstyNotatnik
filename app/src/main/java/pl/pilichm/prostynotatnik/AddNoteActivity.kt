package pl.pilichm.prostynotatnik

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.pilichm.prostynotatnik.databinding.ActivityAddNoteBinding
import pl.pilichm.prostynotatnik.recyclerview.Constants
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.EXTRA_KEY_NOTE_TEXT
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.SHARED_PREF_NEW_NOTE_SUFFIX
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.SHARED_PREF_NOTE_TEXT
import pl.pilichm.prostynotatnik.recyclerview.Note
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * If user has opened existing note for editing, display its content.
         * */
        if (intent.hasExtra(EXTRA_KEY_NOTE_TEXT)){
            binding.etAddNote.setText(intent.getStringExtra(EXTRA_KEY_NOTE_TEXT))
        }

        /**
         * Add listener to floating action button, for saving edited or new note.
         * Return to main activity after click.
         * */
        binding.fabSaveNote.setOnClickListener {
            saveNote()
            finish()
        }
    }

    /**
     * Function for saving created or updated note.
     * */
    private fun saveNote(){
        val sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        val noteText = binding.etAddNote.text.toString()
        val currDate = getCurrentDateAsString()
        val serializedNote = Json.encodeToString(Note(noteText, currDate))
        editor.putString("${SHARED_PREF_NOTE_TEXT}${SHARED_PREF_NEW_NOTE_SUFFIX}", serializedNote)
        editor.apply()
    }

    /**
     * Get current date as string.
     * */
    private fun getCurrentDateAsString(): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        return  current.format(formatter)
    }

    /**
     * Save note on back button press.
     * */
    override fun onBackPressed() {
        saveNote()
        finish()
    }
}