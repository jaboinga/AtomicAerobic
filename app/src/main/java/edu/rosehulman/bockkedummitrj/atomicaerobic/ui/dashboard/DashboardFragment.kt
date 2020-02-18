package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import edu.rosehulman.bockkedummitrj.atomicaerobic.WorkoutManager
import kotlinx.android.synthetic.main.fragment_dashboard.view.*
import java.util.*


class DashboardFragment(var workoutManager: WorkoutManager) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container,false)
        root.progress_bar.setProgress(workoutManager.getPercentCompleted())
        root.progress_text_view.setText(workoutManager.getPercentCompleted().toString() + "% done")
        root.intervals_left_text.setText(workoutManager.getIntervalsLeft().toString())
        root.time_left_text.setText(workoutManager.getTimeLeft().toString())
        val currentTime = Calendar.getInstance()
        if(workoutManager.inBlockoutTime(currentTime)){
            var currentBlockoutTime = workoutManager.getCurrentBlockoutTime()
            var endMinutesString = currentBlockoutTime!!.endMinutes.toString()
            if(currentBlockoutTime.endMinutes < 10) endMinutesString = "0" + currentBlockoutTime!!.endMinutes.toString()
            root.next_blockout_text.setText(currentBlockoutTime!!.endHour.toString() + ":" + endMinutesString)
            root.next_blockout_description.setText("this blockout ends")
        }else{
            var nextBlockoutTime = workoutManager.getNextBlockoutTime()
            var startMinutesString = nextBlockoutTime.startMinutes.toString()
            if(nextBlockoutTime.startMinutes < 10) startMinutesString = "0" + nextBlockoutTime.startMinutes.toString()
            root.next_blockout_text.setText(nextBlockoutTime.startHour.toString() + ":" + startMinutesString)
            root.next_blockout_description.setText("next blockout time")
        }
        return root
    }


}