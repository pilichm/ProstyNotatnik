package pl.pilichm.prostynotatnik

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.pilichm.prostynotatnik.Util.Companion.getCurrentDateAsString
import pl.pilichm.prostynotatnik.Util.Companion.saveNote
import pl.pilichm.prostynotatnik.databinding.ActivityAddNoteBinding
import pl.pilichm.prostynotatnik.recyclerview.Constants
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.EXTRA_KEY_NOTE_ID
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.EXTRA_KEY_NOTE_TEXT
import pl.pilichm.prostynotatnik.recyclerview.Note

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private lateinit var passedNoteId: String

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

        passedNoteId = if(intent.hasExtra(EXTRA_KEY_NOTE_ID)) intent.getStringExtra(EXTRA_KEY_NOTE_ID)
            .toString() else ""

        /**
         * Add listener to floating action button, for saving edited or new note.
         * Return to main activity after click.
         * */
        binding.fabSaveNote.setOnClickListener {
            if (passedNoteId!=""){
                updateNote()
            } else {
                val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
                val noteText = binding.etAddNote.text.toString()
                val currDate = getCurrentDateAsString()

                saveNote(sharedPreferences, noteText, currDate)
            }
            finish()
        }
    }

    /**
     * Function for updating content of existing note.
     */
    private fun updateNote(){
        if (passedNoteId!=""){
            val sharedPreferences =
                getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val noteText = binding.etAddNote.text.toString()
            val currDate = getCurrentDateAsString()
            val serializedNote = Json.encodeToString(Note(noteText, currDate))
            editor.putString(passedNoteId, serializedNote)
            editor.apply()
        }
    }

    /**
     * Ask user if note should be saved as new or updated on back button press.
     * */
    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Save or update note?")
        builder.setMessage("Should note be updated, saved or discarded?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Save"
        ) { dialog, _ ->
            val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
            val noteText = binding.etAddNote.text.toString()
            val currDate = getCurrentDateAsString()

            saveNote(sharedPreferences, noteText, currDate)
            dialog.cancel()
            finish()
        }

        builder.setNeutralButton("Update"
        ) { dialog, _ ->
            updateNote()
            dialog.cancel()
            finish()
        }

        builder.setNegativeButton("Discard"
        ) { dialog, _ ->
            dialog.cancel()
            finish()
        }

        builder.create().show()
    }
}