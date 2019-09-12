package com.terricom.mytype.profile

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.R
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.Pazzle
import com.terricom.mytype.databinding.FragmentProfileBinding




class ProfileFragment: Fragment() {

    private val viewModel: ProfileViewModel by lazy {
        ViewModelProviders.of(this).get(ProfileViewModel::class.java)
    }

    private lateinit var binding: FragmentProfileBinding
    private var mLastPos = -1
    private var mCardScaleHelper: CardScaleHelper? = null
    private val mList = ArrayList<Int>()
    private var mBlurRunnable: Runnable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        binding.viewModel = viewModel
        binding.lifecycleOwner= this
        val pazzleMock = Pazzle(
            listOf(0,4,6,8),
            ""
        )
//        val pazzleMock2 = Pazzle(
//            listOf(10,14,16,18),
//            ""
//        )
        binding.recyclerPuzzle.adapter = PazzleAdapter(viewModel
//            ,PazzleAdapter.OnClickListener{
//            viewModel.setPazzle(it)
//            findNavController().navigate(NavigationDirections.navigateToDreamBoardFragment(it))
//        }
        )
        binding.recyclerPuzzle.addItemDecoration(
            SpaceItemDecoration(
                resources.getDimension(R.dimen.elevation_all).toInt(),
                true
            )
        )
//        (binding.recyclerPuzzle.adapter as PazzleAdapter).submitList(listOf(pazzleMock))
        init()

        return binding.root
    }

    private fun init() {
        for (i in 0..9) {
            mList.add(R.drawable.icon_broccoli)
            mList.add(R.drawable.icon_vegetable)
            mList.add(R.drawable.icon_body_fat)
        }

//        val linearLayoutManager = LinearLayoutManager(App.applicationContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.recyclerPuzzle.setLayoutManager(linearLayoutManager)
        binding.recyclerPuzzle.setAdapter(PazzleAdapter(viewModel, mList))
        // mRecyclerView绑定scale效果
        mCardScaleHelper = CardScaleHelper()
        mCardScaleHelper!!.currentItemPos=2
        mCardScaleHelper!!.attachToRecyclerView(binding.recyclerPuzzle)

        initBlurBackground()
    }

    private fun initBlurBackground() {
        binding.blurView
        binding.recyclerPuzzle.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    notifyBackgroundChange()
                }
            }
        })

        notifyBackgroundChange()
    }

    private fun notifyBackgroundChange() {
        if (mLastPos === mCardScaleHelper!!.currentItemPos) return
        mLastPos = mCardScaleHelper!!.currentItemPos
        val resId = mList.get(mCardScaleHelper!!.currentItemPos)
        binding.blurView.removeCallbacks(mBlurRunnable)
        mBlurRunnable = Runnable {
            val bitmap = BitmapFactory.decodeResource(resources, resId)
            ViewSwitchUtils.startSwitchBackgroundAnim(
                binding.blurView,
                BlurBitmapUtils.getBlurBitmap(binding.blurView.getContext(), bitmap, 15)
            )
        }
        binding.blurView.postDelayed(mBlurRunnable, 500)
    }
}