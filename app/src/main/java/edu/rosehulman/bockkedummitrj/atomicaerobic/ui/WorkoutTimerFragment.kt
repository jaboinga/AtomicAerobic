package edu.rosehulman.bockkedummitrj.atomicaerobic.ui

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.Interval
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import kotlinx.android.synthetic.main.fragment_workout.view.*

class WorkoutTimerFragment(var interval: Interval) : Fragment() {

    private lateinit var timer: CountDownTimer
    private var secondsTotal = interval.duration
    private var secondsRemaining = 0L
    lateinit var progressBar: ProgressBar
    lateinit var timerTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_workout, container, false)
        timerTextView = root.timer_text
        progressBar = root.timer_progress_bar
        progressBar.progress = 100
        root.workout_type_text.text = interval.workout
        secondsRemaining = interval.duration
        startTimer()
        return root
    }

    private fun startTimer() {
        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }
        timer.start()
    }

    private fun updateCountdownUI() {
        progressBar.progress = ((secondsRemaining * 100) / secondsTotal).toInt()
        val minutes = secondsRemaining / 60
        val seconds = secondsRemaining % 60
        if (seconds < 10) {
            timerTextView.text = "$minutes:0$seconds"
        } else {
            timerTextView.text = "$minutes:$seconds"
        }
    }

    private fun onTimerFinished() {
        progressBar.progress = 0
        timerTextView.text = "Done!"
        //TODO mark that the interval is completed
    }

}