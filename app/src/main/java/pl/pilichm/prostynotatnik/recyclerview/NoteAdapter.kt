package pl.pilichm.prostynotatnik.recyclerview

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pl.pilichm.prostynotatnik.R

class NoteAdapter(private val notes: ArrayList<Note>):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>() {
    private var onClickListener: OnClickListener? = null
    private var onLongClickListener: OnLongClickListener? = null

    interface OnClickListener {
        fun onClick(position: Int, item: Note)
    }

    interface OnLongClickListener {
        fun onLongClick(position: Int, item: Note, action: () -> Unit)
    }

    fun addOnClickListener(onClickListener: OnClickListener){
        this.onClickListener = onClickListener
    }

    fun addOnLongClickListener(onLongClickListener: OnLongClickListener){
        this.onLongClickListener = onLongClickListener
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

        /**
         * On long click pressed items background color changes to gray.
         */
        holder.itemView.setOnLongClickListener {
            if (onLongClickListener!=null){
                val llNote = holder.itemView.findViewById(R.id.llNote) as LinearLayout
                llNote.background = ColorDrawable(Color.parseColor("#D3D3D3"))
                onLongClickListener!!.onLongClick(position, note) {
                    llNote.background = ColorDrawable(Color.parseColor("#FFFFFF"))
                }
            }
            true
        }
    }

    override fun getItemCount(): Int {
        return notes.size
    }
}