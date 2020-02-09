package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_view_blockout_times_list.view.*

class BlockoutTimeViewHolder(itemView: View, adapter: BlockoutTimeAdapter): RecyclerView.ViewHolder(itemView) {

    init {
        itemView.blockout_times_delete_icon.setOnClickListener {
            adapter.showRemoveDialog(adapterPosition)
            true
        }
    }

    fun bind(blockoutTime: BlockoutTime){
        var startMinutesString = blockoutTime.startMinutes.toString()
        var endMinutesString = blockoutTime.endMinutes.toString()
        if(blockoutTime.startMinutes < 10) startMinutesString = "0" + blockoutTime.startMinutes.toString()
        if(blockoutTime.endMinutes < 10) endMinutesString = "0" + blockoutTime.endMinutes.toString()

        itemView.blockout_times_time_text.text = "${blockoutTime.startHour}:${startMinutesString} to ${blockoutTime.endHour}:${endMinutesString}"
    }
}