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

class VolunteerRequestAdapter(private val volunteerRequestArrayListList : ArrayList<VolunteerRequestArrayList>) : RecyclerView.Adapter<VolunteerRequestAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerRequestAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_volunteer_request, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VolunteerRequestAdapter.ViewHolder, position: Int ){
        val volunteerRequestArrayListItem: VolunteerRequestArrayList = volunteerRequestArrayListList[position]
        holder.tvVName.text = volunteerRequestArrayListItem.vname
        holder.tvVId.text = volunteerRequestArrayListItem.vid
        holder.tvVEventId.text= volunteerRequestArrayListItem.eventId

        val vid = holder.tvVId.text.toString()
        val eventId = holder.tvVEventId.text.toString()
        holder.tvVId.visibility= View.GONE
        holder.tvVEventId.visibility= View.GONE


        holder.itemView.setOnClickListener(){
                 //Log.d("checktvVId", vid)

           val intent = Intent(holder.itemView.context, VolunteerRequestDetailActivity::class.java)
            intent.putExtra("VolunteerID","${vid.toString()}")
            intent.putExtra("EventUID","${eventId.toString()}")
            holder.itemView.context.startActivity(intent)
         }

    }

    override fun getItemCount(): Int {
        return volunteerRequestArrayListList.size
    }


    public class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView) {
        val tvVName : TextView = itemView.findViewById(R.id.tvVName)
        val tvVId :TextView = itemView.findViewById(R.id.tvVId)
        val tvVEventId :TextView = itemView.findViewById(R.id.tvVEventId)
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