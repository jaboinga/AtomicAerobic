package edu.rosehulman.bockkedummitrj.atomicaerobic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutResetReciever : BroadcastReceiver() {

    // Triggered by the Alarm periodically (starts the service to run task)
    override fun onReceive(context: Context, intent: Intent) {
        val uid = intent.getStringExtra(Constants.UID_TAG)!!
        val ref = FirebaseFirestore.getInstance()
            .collection(Constants.COMPLETED_SESSIONS_COLLECTION)

        ref
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot.documents) {
                    Log.e("resetting", "resetting it all!")
                    ref.document(document.id).set(CompletedSessions(0, uid))
                }
            }

        setupNotifications(uid, context)
    }

    private fun setupNotifications(uid: String, context: Context) {
        val manager = WorkoutManager(uid, context)
        manager.createIntervals()
    }
}