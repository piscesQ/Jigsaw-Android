package com.kore.sample.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kore.sample.commons.CusToast
import kotlinx.android.synthetic.main.me_frg_me.*

private const val ARG_SOURCE = "ARG_SOURCE"

class MeFragment : Fragment() {
    private var mSource: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mSource = it.getString(ARG_SOURCE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.me_frg_me, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_fruit.setOnClickListener {
            CusToast.show("我是橙子！")
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param source source
         * @return A new instance of fragment MeFragment.
         */
        @JvmStatic
        fun newInstance(source: String) =
            MeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SOURCE, source)
                }
            }
    }
}