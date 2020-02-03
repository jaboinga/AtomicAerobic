package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.settings

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class Setting(
    var randomization: Boolean = false,
    var timePerSession: Int = 0,
    var timePerSessionUnit: String = "minutes",
    var totalTime: Int = 0,
    var userId: String = "",
    var workoutArms: Boolean = true,
    var workoutCardio: Boolean = true,
    var workoutCore: Boolean = true,
    var workoutLegs: Boolean = true
) {
    @get:Exclude
    var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): Setting {
            val setting = snapshot.toObject(Setting::class.java)!!
            setting.id = snapshot.id
            return setting
        }
    }
}
