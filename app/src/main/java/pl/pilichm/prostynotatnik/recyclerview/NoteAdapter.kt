package pl.pilichm.prostynotatnik.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.pilichm.prostynotatnik.R

class NoteAdapter(private val notes: ArrayList<Note>):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int, item: Note)
    }

    fun addOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNoteText = itemView.findViewById(R.id.tvNoteText) as TextView
        val tvNoteCreationDate = itemView.findViewById(R.id.tvNoteCreationDate) as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val layoutInflater = LayoutInflater.from(context)
        val noteView = layoutInflater.inflate(R.layout.note_item, parent, false)
        return ViewHolder(noteView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val note = notes[position]
        val tvNoteText = holder.tvNoteText
        val tvNoteCreationDate = holder.tvNoteCreationDate

        tvNoteText.text = note.noteText
        tvNoteCreationDate.text = note.noteCreationTime

        holder.itemView.setOnClickListener {
            if (onClickListener!=null){
                onClickListener!!.onClick(position, note)
            }
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}