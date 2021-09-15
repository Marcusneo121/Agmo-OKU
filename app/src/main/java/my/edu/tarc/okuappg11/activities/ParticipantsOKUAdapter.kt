package my.edu.tarc.okuappg11.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.firestore.FirebaseFirestore
import my.edu.tarc.okuappg11.R

class ParticipantsOKUAdapter(private val participantList: ArrayList<ParticipantsOKUArrayList>) :
    RecyclerView.Adapter<ParticipantsOKUAdapter.ViewHolder>() {
    private lateinit var reference2: DatabaseReference
    private lateinit var fStore: FirebaseFirestore

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_participants, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val participantOKUArrayListItem: ParticipantsOKUArrayList = participantList[position]
        holder.participantName.text = participantOKUArrayListItem.name.toString()


    }





    override fun getItemCount(): Int {
        return participantList.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val participantName: TextView = itemView.findViewById(R.id.tvPName)

    }
}