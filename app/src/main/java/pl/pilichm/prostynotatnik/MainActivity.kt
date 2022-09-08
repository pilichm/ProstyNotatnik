package pl.pilichm.prostynotatnik

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.pilichm.prostynotatnik.databinding.ActivityMainBinding
import pl.pilichm.prostynotatnik.recyclerview.Constants
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.EXTRA_KEY_LIST_OF_NOTES_ID
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.EXTRA_KEY_NOTE_ID
import pl.pilichm.prostynotatnik.recyclerview.Note
import pl.pilichm.prostynotatnik.recyclerview.NoteAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var mNotes: ArrayList<Note>? = ArrayList()
    private var mAdapter: NoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        readSavedNotes()
    }

    /**
     * Function loads all notes from shared preferences.
     * It is called when activity starts or restarts.
     */
    private fun readSavedNotes(){
        val sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
        mNotes = ArrayList()

        if (sharedPreferences.contains(EXTRA_KEY_LIST_OF_NOTES_ID)) {
            val listOfNotesIds = sharedPreferences.getString(EXTRA_KEY_LIST_OF_NOTES_ID, "") ?: ""
            val notesIds = listOfNotesIds.split(" ")

            if (!notesIds.isNullOrEmpty()) {
                for (noteId in notesIds) {
                    Log.i("MainActivity", "Reading note $noteId")
                    if (sharedPreferences.contains(noteId)) {
                        val serializedNote = sharedPreferences.getString(noteId,
                            Json.encodeToString(Note("-", "-")))
                        val note = Json.decodeFromString<Note>(serializedNote!!)
                        if (note.noteText.isNotEmpty()) {
                            Log.i("MainActivity", "Read note: ${note.noteText}")
                            mNotes!!.add(note)
                        } else {
                            Log.i("MainActivity", "Read note from sp but empty content!")
                        }
                    } else {
                        Log.i("MainActivity", "No note text for id $noteId!")
                    }
                }
            }
        } else {
            Log.i("MainActivity", "No notes to display!")
        }

        displaySavedNotes()
    }

    /**
     * Loads notes stored in shared preferences and displays it on main screen.
     * If no note is found, default one will be displayed.
     * */
    private fun displaySavedNotes(){
        mAdapter = NoteAdapter(mNotes!!)

        /**
         * Open note for edition on click.
         */
        mAdapter!!.addOnClickListener(object: NoteAdapter.OnClickListener {
            override fun onClick(position: Int, item: Note) {
                startEditNoteActivity(position)
            }
        })

        /**
         * Ask if note should be deleted on long click.
         */
        mAdapter!!.addOnLongClickListener(object: NoteAdapter.OnLongClickListener{
            override fun onLongClick(position: Int, item: Note) {
                displayDeleteDialog(position)
            }
        })

        binding.rvNotes.adapter = mAdapter
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
    }

    /**
     * Function for displaying dialog asking user if pressed note should be deleted.
     */
    private fun displayDeleteDialog(position: Int){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete note?")
        builder.setMessage("Should pressed note be deleted?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton("Delete"
        ) { dialog, _ ->
            deleteNoteByText(position)
            dialog.cancel()
        }

        builder.setNegativeButton("Cancel"
        ) { dialog, _ ->
            dialog.cancel()
        }

        builder.create().show()
    }

    /**
     * Function opening edit note activity. Passes note text and id in intent.
     */
    private fun startEditNoteActivity(position: Int){
        val intent = Intent(applicationContext, AddNoteActivity::class.java)
        intent.putExtra(Constants.EXTRA_KEY_NOTE_TEXT, mNotes!![position].noteText)
        intent.putExtra(EXTRA_KEY_NOTE_ID, getNoteIdByText(mNotes!![position].noteText))
        startActivity(intent)
    }

    /**
     * Function for deleting note of passed text.
     */
    private fun deleteNoteByText(position: Int): Boolean{
        val noteText = mNotes!![position].noteText
        var matchedNoteId = ""
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

        if (sharedPreferences.contains(EXTRA_KEY_LIST_OF_NOTES_ID)){
            val listOfNotesIds = sharedPreferences.getString(EXTRA_KEY_LIST_OF_NOTES_ID, "") ?: ""
            val notesIds = listOfNotesIds.split(" ")

            if (!notesIds.isNullOrEmpty()){
                for (noteId in notesIds){
                    if (sharedPreferences.contains(noteId)){
                        val serializedNote = sharedPreferences.getString(noteId,
                            Json.encodeToString(Note("-", "-")))
                        val note = Json.decodeFromString<Note>(serializedNote!!)

                        if (note.noteText.isNotEmpty()&& note.noteText == noteText) {
                            matchedNoteId = noteId
                            val editor = sharedPreferences.edit()
                            editor.remove(noteId)
                            editor.apply()
                        }
                    }
                }
            }

            if (matchedNoteId!=""){
                val updatedIds = notesIds.filter { it != matchedNoteId }
                var notesIdsAsString = ""
                for (noteId in updatedIds){
                    notesIdsAsString = "$notesIdsAsString $noteId"
                }

                val editor = sharedPreferences.edit()
                editor.putString(EXTRA_KEY_LIST_OF_NOTES_ID, notesIdsAsString)
                editor.apply()

                readSavedNotes()
                displaySavedNotes()
            }

        }

        return false
    }

    /**
     * Function for retrieving note id by its text.
     */
    private fun getNoteIdByText(noteText: String): String {
        val sharedPreferences = getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

        if (sharedPreferences.contains(EXTRA_KEY_LIST_OF_NOTES_ID)) {
            val listOfNotesIds = sharedPreferences.getString(EXTRA_KEY_LIST_OF_NOTES_ID, "") ?: ""
            val notesIds = listOfNotesIds.split(" ")

            if (!notesIds.isNullOrEmpty()) {
                for (noteId in notesIds){
                    if (sharedPreferences.contains(noteId)){
                        val serializedNote = sharedPreferences.getString(noteId,
                        Json.encodeToString(Note("-", "-")))
                        val note = Json.decodeFromString<Note>(serializedNote!!)

                        if (note.noteText.isNotEmpty()&& note.noteText == noteText) {
                            return noteId
                        }
                    }
                }
            }
        }

        return ""
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.main_menu_add_note -> {
                val intent = Intent(applicationContext, AddNoteActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    /**
     * Update number of saved and displayed notes after return from edit note activity.
     * */
    override fun onRestart() {
        super.onRestart()
        readSavedNotes()
    }
}