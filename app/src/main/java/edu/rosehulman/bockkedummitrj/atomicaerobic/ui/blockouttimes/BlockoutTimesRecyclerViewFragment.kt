package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import edu.rosehulman.bockkedummitrj.atomicaerobic.R

class BlockoutTimesRecyclerViewFragment(private var adapter: BlockoutTimeAdapter): Fragment() {

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
            val recyclerView =  inflater.inflate(R.layout.blockout_times_recycler_view, container, false) as RecyclerView
            recyclerView.adapter = adapter
            recyclerView.layoutManager = LinearLayoutManager(context)
            return recyclerView

        }
    }
