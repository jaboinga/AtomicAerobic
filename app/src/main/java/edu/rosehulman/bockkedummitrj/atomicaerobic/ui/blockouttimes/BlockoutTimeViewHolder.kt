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
        itemView.blockout_times_time_text.text = "${blockoutTime.startTime} ${blockoutTime.startTimePeriod} to ${blockoutTime.endTime} ${blockoutTime.endTimePeriod}"
    }
}