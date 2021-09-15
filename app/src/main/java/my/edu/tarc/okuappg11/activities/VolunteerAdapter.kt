package my.edu.tarc.okuappg11.activities

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import my.edu.tarc.okuappg11.R

class VolunteerAdapter(private val volunteerArrayListList : ArrayList<VolunteerArrayList>) : RecyclerView.Adapter<VolunteerAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_volunteer, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VolunteerAdapter.ViewHolder, position: Int ){
        val volunteerArrayListItem: VolunteerArrayList = volunteerArrayListList[position]
        holder.tvVAName.text = volunteerArrayListItem.vname
        holder.tvVAId.text = volunteerArrayListItem.vid
        holder.tvVAEventId.text= volunteerArrayListItem.eventId

        val vid = holder.tvVAId.text.toString()
        val eventId = holder.tvVAEventId.text.toString()
        //holder.tvVId.visibility= View.GONE
        //holder.tvVEventId.visibility= View.GONE


        holder.itemView.setOnClickListener(){
            //Log.d("checktvVId", vid)

            val intent = Intent(holder.itemView.context, VolunteerDetailActivity::class.java)
            intent.putExtra("VolunteerID","${vid.toString()}")
            intent.putExtra("EventUID","${eventId.toString()}")
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return volunteerArrayListList.size
    }


    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val tvVAName : TextView = itemView.findViewById(R.id.tvVAName)
        val tvVAId :TextView = itemView.findViewById(R.id.tvVAId)
        val tvVAEventId :TextView = itemView.findViewById(R.id.tvVAEventId)
        //val vid = holder.tvVId.text.toString()

        /* init{
             itemView.setOnClickListener{
                 //println("Test")

                 val intent =Intent(itemView.context, VolunteerRequestDetailActivity::class.java)
                 intent.putExtra("VolunteerID","${vid.toString()}")
                 itemView.context.startActivity(intent)
             }
         }*/
    }


}