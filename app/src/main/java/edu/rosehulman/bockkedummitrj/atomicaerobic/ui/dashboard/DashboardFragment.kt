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
        //TODO change description from minutes to seconds depending
        //TODO update the blockout time box
        //TODO randomize the motivational statement
        return root
    }
}