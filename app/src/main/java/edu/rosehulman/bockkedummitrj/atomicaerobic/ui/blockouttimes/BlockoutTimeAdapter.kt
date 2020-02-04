package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import edu.rosehulman.bockkedummitrj.atomicaerobic.Constants
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import kotlinx.android.synthetic.main.dialog_add_blockout_time.view.*

class BlockoutTimeAdapter(var context: Context?, var userId: String) :
    RecyclerView.Adapter<BlockoutTimeViewHolder>() {
    private val blockoutTimes = ArrayList<BlockoutTime>()

    private val blockoutTimesRef =
        FirebaseFirestore.getInstance().collection(Constants.BLOCKOUT_TIMES_COLLECTION)

    init {
        blockoutTimesRef
            .whereEqualTo("userId", userId)
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
        val view = LayoutInflater.from(context)
            .inflate(R.layout.row_view_blockout_times_list, parent, false)
        return BlockoutTimeViewHolder(view, this)
    }

    fun showRemoveDialog(position: Int) {
        val builder = AlertDialog.Builder(context!!)

        val view =
            LayoutInflater.from(context).inflate(R.layout.dialog_delete_blockout_time, null, false)
        builder.setView(view)

        builder.setPositiveButton(android.R.string.ok) { _, _ ->
            remove(position)
        }
        builder.setNegativeButton(android.R.string.cancel, null)
        builder.create().show()

    }

    fun showAddDialog() : View.OnClickListener{
        return View.OnClickListener {
            val builder = AlertDialog.Builder(context!!, R.style.DialogStyle)

            val view =
                LayoutInflater.from(context).inflate(R.layout.dialog_add_blockout_time, null, false)
            builder.setView(view)

            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                val time = BlockoutTime(
                    view.add_blockout_start_time_spinner.currentHour,
                    view.add_blockout_start_time_spinner.currentMinute,
                    view.add_blockout_end_time_spinner.currentHour,
                    view.add_blockout_end_time_spinner.currentMinute,
                    userId
                )
                add(time)

                //TODO: figure out why this isn't updating in the view immediately
                // Dr. Boutell please help: we can't figure out why our recycler view isn't updating on an add here
                // The data does persist in the database after you change the fragment, but it just doesn't update immediately
            }

            builder.setNegativeButton(android.R.string.cancel, null)
            builder.create().show()
        }
    }

    private fun add(time: BlockoutTime) {
        blockoutTimesRef.add(time)
    }

    private fun remove(position: Int) {
        blockoutTimesRef.document(blockoutTimes[position].id).delete()
    }
}