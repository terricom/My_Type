package com.terricom.mytype.profile

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.data.Puzzle
import com.terricom.mytype.databinding.ItemProfileDreamBoardBinding

class PazzleAdapter(val viewModel: ProfileViewModel
) : RecyclerView.Adapter<PazzleAdapter.PazzleViewHolder>()
{

    private lateinit var context: Context
    // the data of adapter
    private var puzzles: List<Puzzle>? = null


    fun submitPazzles(puzzles: List<Puzzle>) {
        this.puzzles = puzzles
        notifyDataSetChanged()
    }



    class PazzleViewHolder(private var binding: ItemProfileDreamBoardBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(context: Context, puzzle: Puzzle) {
            binding.puzzle = puzzle
            puzzle?.let {
                binding.puzzle1.visibility =
                    if (puzzle.position!!.contains(0)) View.INVISIBLE else View.VISIBLE
                binding.puzzle2.visibility =
                    if (puzzle.position!!.contains(1)) View.INVISIBLE else View.VISIBLE
                binding.puzzle3.visibility =
                    if (puzzle.position!!.contains(2)) View.INVISIBLE else View.VISIBLE
                binding.puzzle4.visibility =
                    if (puzzle.position!!.contains(3)) View.INVISIBLE else View.VISIBLE
                binding.puzzle5.visibility =
                    if (puzzle.position!!.contains(4)) View.INVISIBLE else View.VISIBLE
                binding.puzzle6.visibility =
                    if (puzzle.position!!.contains(5)) View.INVISIBLE else View.VISIBLE
                binding.puzzle7.visibility =
                    if (puzzle.position!!.contains(6)) View.INVISIBLE else View.VISIBLE
                binding.puzzle8.visibility =
                    if (puzzle.position!!.contains(7)) View.INVISIBLE else View.VISIBLE
                binding.puzzle9.visibility =
                    if (puzzle.position!!.contains(8)) View.INVISIBLE else View.VISIBLE
                binding.puzzle10.visibility =
                    if (puzzle.position!!.contains(9)) View.INVISIBLE else View.VISIBLE
                binding.puzzle11.visibility =
                    if (puzzle.position!!.contains(10)) View.INVISIBLE else View.VISIBLE
                binding.puzzle12.visibility =
                    if (puzzle.position!!.contains(11)) View.INVISIBLE else View.VISIBLE
                binding.puzzle13.visibility =
                    if (puzzle.position!!.contains(12)) View.INVISIBLE else View.VISIBLE
                binding.puzzle14.visibility =
                    if (puzzle.position!!.contains(13)) View.INVISIBLE else View.VISIBLE
                binding.puzzle15.visibility =
                    if (puzzle.position!!.contains(14)) View.INVISIBLE else View.VISIBLE
            }
//            val displayMetrics = DisplayMetrics()
//            (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
//            binding.dreamboard.layoutParams = ConstraintLayout.LayoutParams(displayMetrics.widthPixels,
//                context.resources.getDimensionPixelSize(R.dimen.height_profile_pazzle))
            binding.lifecycleOwner =this
            // This is important, because it forces the data binding to execute immediately,
            // which allows the RecyclerView to make the correct view size measurements
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun markAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun markDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }
    }


//    companion object DiffCallback : DiffUtil.ItemCallback<ArrayList<Int>>() {
//        override fun areItemsTheSame(oldItem: ArrayList<Int>, newItem: ArrayList<Int>): Boolean {
//            return (oldItem == newItem)
//        }
//
//        override fun areContentsTheSame(oldItem: ArrayList<Int>, newItem: ArrayList<Int>): Boolean {
//            return oldItem == newItem
//        }
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PazzleViewHolder {
        context = parent.context
        return PazzleViewHolder(ItemProfileDreamBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: PazzleViewHolder, position: Int) {
        //// to pass onClicklistener into adapter in CartFragment
            puzzles?.let {
                holder.bind(context, it[getRealPosition(position)])
            }
        }


    override fun getItemCount(): Int {
        return puzzles?.let { Int.MAX_VALUE } ?: 0
    }

    private fun getRealPosition(position: Int): Int = puzzles?.let {
        position % it.size
    } ?: 0

    override fun onViewAttachedToWindow(holder: PazzleAdapter.PazzleViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: PazzleAdapter.PazzleViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}