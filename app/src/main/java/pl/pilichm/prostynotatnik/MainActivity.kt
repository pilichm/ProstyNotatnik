package pl.pilichm.prostynotatnik

import android.content.Intent
import android.os.Bundle
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
                    println("Reading note $noteId")
                    if (sharedPreferences.contains(noteId)) {
                        val serializedNote = sharedPreferences.getString(noteId,
                            Json.encodeToString(Note("-", "-")))
                        val note = Json.decodeFromString<Note>(serializedNote!!)
                        if (!note.noteText.isNullOrEmpty()) {
                            println("Read note: ${note.noteText}")
                            mNotes!!.add(note)
                        } else {
                            println("Read note from sp but empty content!")
                        }
                    } else {
                        println("No note text for id $noteId!")
                    }
                }
            }
        } else {
            println("No notes to display!")
        }

        displaySavedNotes()
    }

    /**
     * Loads notes stored in shared preferences and displays it on main screen.
     * If no note is found, default one will be displayed.
     * */
    private fun displaySavedNotes(){
        mAdapter = NoteAdapter(mNotes!!)
        mAdapter!!.addOnClickListener(object: NoteAdapter.OnClickListener {
            override fun onClick(position: Int, item: Note) {
                val intent = Intent(applicationContext, AddNoteActivity::class.java)
                intent.putExtra(Constants.EXTRA_KEY_NOTE_TEXT, mNotes!![position].noteText)
                startActivity(intent)
            }
        })

        binding.rvNotes.adapter = mAdapter
        binding.rvNotes.layoutManager = LinearLayoutManager(this)
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