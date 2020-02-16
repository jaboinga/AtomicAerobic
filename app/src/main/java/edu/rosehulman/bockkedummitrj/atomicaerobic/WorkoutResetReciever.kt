package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.lang.Thread.sleep

class WorkoutResetReciever : BroadcastReceiver() {

    // Triggered by the Alarm periodically (starts the service to run task)
    override fun onReceive(context: Context, intent: Intent) {
        val uid = intent.getStringExtra(Constants.UID_TAG)!!
        Log.e("uid", uid)

        var ref :DocumentReference? = null

        FirebaseFirestore.getInstance().collection(Constants.COMPLETED_SESSIONS_COLLECTION)
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    ref = FirebaseFirestore.getInstance()
                        .collection(Constants.COMPLETED_SESSIONS_COLLECTION).document(document.id)
                }

            }
            if(ref != null) ref?.set(CompletedSessions(0, uid))

    }
}