package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.bockkedummitrj.atomicaerobic.Constants
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment(var userId: String) : Fragment() {
    private lateinit var root: View
    private val settingsRef =
        FirebaseFirestore.getInstance().collection(Constants.SETTINGS_COLLECTION)
    private var setting: Setting = Setting()
    private var documentID: String = "test"

    init {
        settingsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
//                    Log.wtf(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                if (snapshot!!.documentChanges.size == 0) {
                    setting = Setting()
                    setting.userId = userId
                    settingsRef.add(setting)
                }

                for (docChange in snapshot!!.documentChanges) {
                    val set = Setting.fromSnapshot(docChange.document)
                    setting = set
                    documentID = set.id
                    updateSettings(root)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_settings, container, false)
        initializeListeners(root)
        updateSettings(root)
        return root
    }

    private fun initializeListeners(root: View) {
        root.total_time_edit_text.setOnFocusChangeListener { view: View, hasFocus: Boolean ->

            if (!hasFocus) {
                if (root.total_time_edit_text.text.toString() == "") {
                    root.total_time_edit_text.setText(Constants.DEFAULT_TOTAL_TIME.toString())
                }
                setting.totalTime = root.total_time_edit_text.text.toString().toInt()
                settingsRef.document(documentID).set(setting)

            }

        }

        root.time_per_session_edit_text.setOnFocusChangeListener { view: View, hasFocus: Boolean ->
            if (!hasFocus) {
                if (root.time_per_session_edit_text.text.toString() == "") {
                    root.time_per_session_edit_text.setText(Constants.DEFAULT_TIME_PER_SESSION.toString())
                }
                setting.timePerSession = root.time_per_session_edit_text.text.toString().toInt()
                settingsRef.document(documentID).set(setting)
            }
        }

        root.time_per_session_spinner.onItemSelectedListener = SpinnerListener()

        root.workout_type_arms_checkbox.setOnClickListener {
            setting.workoutArms = root.workout_type_arms_checkbox.isChecked
            settingsRef.document(documentID).set(setting)

        }

        root.workout_type_cardio_checkbox.setOnClickListener {
            setting.workoutCardio = root.workout_type_cardio_checkbox.isChecked
            settingsRef.document(documentID).set(setting)

        }

        root.workout_type_core_checkbox.setOnClickListener {
            setting.workoutCore = root.workout_type_core_checkbox.isChecked
            settingsRef.document(documentID).set(setting)

        }

        root.workout_type_legs_checkbox.setOnClickListener {
            setting.workoutLegs = root.workout_type_legs_checkbox.isChecked
            settingsRef.document(documentID).set(setting)

        }

        root.randomization_checkbox.setOnClickListener {
            setting.randomization = root.randomization_checkbox.isChecked
            settingsRef.document(documentID).set(setting)

        }

    }

    inner class SpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(parent: AdapterView<*>?) {
            //Do nothing
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            setting.timePerSessionUnit =
                parent?.getItemAtPosition(position).toString()
            settingsRef.document(documentID).set(setting)
        }

    }

    private fun updateSettings(root: View) {
        root.total_time_edit_text.setText(setting.totalTime.toString())
        root.time_per_session_edit_text.setText(setting.timePerSession.toString())
        if (setting.timePerSessionUnit == "seconds") {
            root.time_per_session_spinner.setSelection(0)
        } else {
            //minutes
            root.time_per_session_spinner.setSelection(1)
        }
        root.workout_type_arms_checkbox.isChecked = setting.workoutArms
        root.workout_type_cardio_checkbox.isChecked = setting.workoutCardio
        root.workout_type_core_checkbox.isChecked = setting.workoutCore
        root.workout_type_legs_checkbox.isChecked = setting.workoutLegs
        root.randomization_checkbox.isChecked = setting.randomization
    }
}


