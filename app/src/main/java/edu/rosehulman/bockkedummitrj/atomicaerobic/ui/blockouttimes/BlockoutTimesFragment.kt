package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.bockkedummitrj.atomicaerobic.R
import kotlinx.android.synthetic.main.blockout_times_recycler_view.view.*

class BlockoutTimesFragment(private var adapter: BlockoutTimeAdapter) : Fragment() {

    private lateinit var blockoutTimesViewModel: BlockoutTimesViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        blockoutTimesViewModel =
//            ViewModelProviders.of(this).get(BlockoutTimesViewModel::class.java)
        //        val textView: TextView = root.findViewById(R.id.text_notifications)
//        blockoutTimesViewModel.text.observe(this, Observer {
////            textView.text = it
//        })
        val view = inflater.inflate(R.layout.fragment_blockout_times_list, container, false)
        view.blockout_times_recycler_view.adapter = adapter
        view.blockout_times_recycler_view.layoutManager = LinearLayoutManager(context)
        return  view
    }
}