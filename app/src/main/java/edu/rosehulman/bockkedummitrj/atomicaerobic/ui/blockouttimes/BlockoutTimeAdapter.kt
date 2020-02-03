package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.bockkedummitrj.atomicaerobic.Constants
import edu.rosehulman.bockkedummitrj.atomicaerobic.R

class BlockoutTimeAdapter(var context: Context?): RecyclerView.Adapter<BlockoutTimeViewHolder>() {


    private val blockoutTimes = ArrayList<BlockoutTime>()

    private val blockoutTimesRef = FirebaseFirestore.getInstance().collection(Constants.BLOCKOUT_TIMES_COLLECTION)

    init{
        blockoutTimesRef
            .addSnapshotListener { snapshot: QuerySnapshot?, exception: FirebaseFirestoreException? ->
                if (exception != null) {
                    Log.e(Constants.TAG, "Listen error: $exception")
                    return@addSnapshotListener
                }

                for (docChange in snapshot!!.documentChanges) {
                    val bt = BlockoutTime.fromSnapshot(docChange.document)
                    when (docChange.type) {
                        DocumentChange.Type.ADDED -> {
                            Log.d(Constants.TAG, "blockout time added")
                            blockoutTimes.add(0, bt)
                            notifyItemInserted(0)
                        }

                        DocumentChange.Type.REMOVED -> {
                            val pos = blockoutTimes.indexOfFirst { bt.id == it.id }
                            blockoutTimes.removeAt(pos)
                            notifyItemRemoved(pos)
                        }

                        DocumentChange.Type.MODIFIED -> {
                            val pos = blockoutTimes.indexOfFirst { bt.id == it.id }
                            blockoutTimes[pos] = bt
                            notifyItemChanged(pos)
                        }
                    }
                }
            }
    }

    override fun getItemCount(): Int {
        return blockoutTimes.size
    }

    override fun onBindViewHolder(holder: BlockoutTimeViewHolder, position: Int) {
       holder.bind(blockoutTimes[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BlockoutTimeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.blockout_times_recycler_view, parent, false)
        return BlockoutTimeViewHolder(view, this)
    }

    fun showRemoveDialog(position: Int) {
            val builder = AlertDialog.Builder(context!!)

            val view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_blockout_time, null, false)
            builder.setView(view)

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                remove(position)
            }
            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()

    }

    private fun remove(position: Int){
        blockoutTimesRef.document(blockoutTimes[position].id).delete()
    }
}