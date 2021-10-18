package pl.pilichm.prostynotatnik

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import pl.pilichm.prostynotatnik.recyclerview.Constants
import pl.pilichm.prostynotatnik.recyclerview.Constants.Companion.SHARED_PREF_NUM_OF_NOTES
import pl.pilichm.prostynotatnik.recyclerview.Note
import pl.pilichm.prostynotatnik.recyclerview.NoteAdapter

class MainActivity : AppCompatActivity() {
    private var mNotes: ArrayList<Note>? = ArrayList()
    private var mAdapter: NoteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        readSavedNotes()
    }

    private fun readSavedNotes(){
        val sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)
        mNotes = ArrayList()

        if (sharedPreferences.contains(SHARED_PREF_NUM_OF_NOTES)){
            val notesCount = sharedPreferences.getInt(SHARED_PREF_NUM_OF_NOTES, 0)
            for (index in 0 until notesCount){
                val key = "${Constants.SHARED_PREF_NOTE_TEXT}${index}"
                val serializedNote = sharedPreferences.getString(key,
                    Json.encodeToString(Note("-", "-")))
                val note = Json.decodeFromString<Note>(serializedNote!!)
                if (note.noteText != "-"){
                    mNotes!!.add(note)
                }
            }
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

        rvNotes.adapter = mAdapter
        rvNotes.layoutManager = LinearLayoutManager(this)
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
        val sharedPreferences =
            getSharedPreferences(Constants.SHARED_PREF_NAME, MODE_PRIVATE)

        val key = "${Constants.SHARED_PREF_NOTE_TEXT}${Constants.SHARED_PREF_NEW_NOTE_SUFFIX}"
        if (sharedPreferences.contains(key)){
            val newNote = sharedPreferences.getString(key, Json.encodeToString(Note("-", "-")))
            val editor = sharedPreferences.edit()
            val notesCount = mNotes!!.size + 1
            editor.putInt(SHARED_PREF_NUM_OF_NOTES, notesCount)
            val newKey = "${Constants.SHARED_PREF_NOTE_TEXT}${notesCount}"
            editor.putString(newKey, newNote)
            editor.remove(key)
            editor.apply()
        }

        readSavedNotes()
    }
}