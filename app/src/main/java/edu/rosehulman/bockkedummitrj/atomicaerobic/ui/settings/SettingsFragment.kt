package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.bockkedummitrj.atomicaerobic.Constants
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import kotlinx.android.synthetic.main.fragment_settings.view.*

class SettingsFragment(var userId: String) : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel

    private lateinit var root: View
    private val settingsRef =
        FirebaseFirestore.getInstance().collection(Constants.SETTINGS_COLLECTION)
    private var setting: Setting = Setting()
    private var documentID : String = "1234"

    init {
        settingsRef
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.wtf(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                for (docChange in snapshot!!.documentChanges) {
                    val set = Setting.fromSnapshot(docChange.document)
                    Log.e("YEET", set.toString())
                    setting = set
                    documentID = set.id
                    updateSettings(root)

//                        when (docChange.type) {
//                        DocumentChange.Type.ADDED -> {
//                            set
//                        }
//
//                        DocumentChange.Type.REMOVED -> {
//                            Setting()
//                        }
//
//                        DocumentChange.Type.MODIFIED -> {
//                            set
//                        }
//                    }
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        settingsViewModel =
//            ViewModelProviders.of(this).get(SettingsViewModel::class.java)
        root = inflater.inflate(R.layout.fragment_settings, container, false)
//        val textView: TextView = root.findViewById(R.id.text_home)
//        settingsViewModel.text.observe(this, Observer {
////            textView.text = it
//        })
        initializeListeners(root)
        updateSettings(root)
        return root
    }

    private fun initializeListeners(root: View) {
        root.total_time_edit_text.setOnEditorActionListener { v, actionId, event ->
            setting.totalTime = root.total_time_edit_text.text.toString().toInt()
            settingsRef.document(documentID).set(setting)
            true
        }

        root.time_per_session_edit_text.setOnEditorActionListener { v, actionId, event ->
            setting.timePerSession = root.time_per_session_edit_text.text.toString().toInt()
            settingsRef.document(documentID).set(setting)
            true

        }

        root.time_per_session_spinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    setting.timePerSessionUnit =
                        root.time_per_session_spinner.selectedItem.toString()
                    settingsRef.document(documentID).set(setting)

                }

            }

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

    private fun updateSettings(root: View) {
        root.total_time_edit_text.setText(setting.totalTime.toString())
        root.time_per_session_edit_text.setText(setting.timePerSession.toString())
        root.time_per_session_spinner.setSelection(0)
        //TODO fix spinner
        root.workout_type_arms_checkbox.isChecked = setting.workoutArms
        root.workout_type_cardio_checkbox.isChecked = setting.workoutCardio
        root.workout_type_core_checkbox.isChecked = setting.workoutCore
        root.workout_type_legs_checkbox.isChecked = setting.workoutLegs
        root.randomization_checkbox.isChecked = setting.randomization
    }
}