package edu.rosehulman.bockkedummitrj.atomicaerobic.ui.blockouttimes

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import edu.rosehulman.bockkedummitrj.atomicaerobic.R

class BlockoutTimesFragment : Fragment() {

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
        return inflater.inflate(R.layout.fragment_blockout_times_list, container, false)
    }
}