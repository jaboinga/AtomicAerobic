package edu.rosehulman.bockkedummitrj.atomicaerobic

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude

data class CompletedSessions(
    var completedSessions: Int = 0,
    var userId: String = ""
){
    @get:Exclude
    var id = ""

    companion object {
        fun fromSnapshot(snapshot: DocumentSnapshot): CompletedSessions {
            val completedSessions = snapshot.toObject(CompletedSessions::class.java)!!
            completedSessions.id = snapshot.id
            return completedSessions
        }
    }
}