package com.kore.sample.discover

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kore.jigsaw.core.Jigsaw
import com.kore.jigsaw.core.router.JRouter
import com.kore.sample.commons.CusToast
import com.kore.sample.interfaceshelf.me.IMe
import kotlinx.android.synthetic.main.discover_frg_discover.*

private const val ARG_SOURCE = "ARG_SOURCE"

class DiscoverFragment : Fragment() {
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
        return inflater.inflate(R.layout.discover_frg_discover, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iv_fruit.setOnClickListener {
            CusToast.show("我是蓝莓！")
        }

        initView()
    }

    private fun initView() {
        btn_go_next_home.setOnClickListener {
            val openUri = JRouter.get().openUri(activity, "jigsaw://me/me_home")
            CusToast.show("openUri = $openUri")
        }

        btn_get_str_from_next.setOnClickListener {
            val service = Jigsaw.getService(IMe::class.java) as IMe
            CusToast.show(service.getMeInfo())
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
            DiscoverFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SOURCE, source)
                }
            }
    }
}