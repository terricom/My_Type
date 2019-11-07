package com.terricom.mytype.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.terricom.mytype.data.Puzzle
import com.terricom.mytype.databinding.ItemProfileDreamBoardBinding

class PuzzleAdapter(
    val viewModel: ProfileViewModel
) : ListAdapter<Puzzle, PuzzleAdapter.PuzzleViewHolder>(DiffCallback) {

    class PuzzleViewHolder(private var binding: ItemProfileDreamBoardBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(puzzle: Puzzle) {
            binding.lifecycleOwner =this
            binding.puzzle = puzzle

            puzzle.let {
                binding.puzzle1.visibility =
                    if (puzzle.position!!.contains(0)) View.INVISIBLE else View.VISIBLE
                binding.puzzle2.visibility =
                    if (puzzle.position.contains(1)) View.INVISIBLE else View.VISIBLE
                binding.puzzle3.visibility =
                    if (puzzle.position.contains(2)) View.INVISIBLE else View.VISIBLE
                binding.puzzle4.visibility =
                    if (puzzle.position.contains(3)) View.INVISIBLE else View.VISIBLE
                binding.puzzle5.visibility =
                    if (puzzle.position.contains(4)) View.INVISIBLE else View.VISIBLE
                binding.puzzle6.visibility =
                    if (puzzle.position.contains(5)) View.INVISIBLE else View.VISIBLE
                binding.puzzle7.visibility =
                    if (puzzle.position.contains(6)) View.INVISIBLE else View.VISIBLE
                binding.puzzle8.visibility =
                    if (puzzle.position.contains(7)) View.INVISIBLE else View.VISIBLE
                binding.puzzle9.visibility =
                    if (puzzle.position.contains(8)) View.INVISIBLE else View.VISIBLE
                binding.puzzle10.visibility =
                    if (puzzle.position.contains(9)) View.INVISIBLE else View.VISIBLE
                binding.puzzle11.visibility =
                    if (puzzle.position.contains(10)) View.INVISIBLE else View.VISIBLE
                binding.puzzle12.visibility =
                    if (puzzle.position.contains(11)) View.INVISIBLE else View.VISIBLE
                binding.puzzle13.visibility =
                    if (puzzle.position.contains(12)) View.INVISIBLE else View.VISIBLE
                binding.puzzle14.visibility =
                    if (puzzle.position.contains(13)) View.INVISIBLE else View.VISIBLE
                binding.puzzle15.visibility =
                    if (puzzle.position.contains(14)) View.INVISIBLE else View.VISIBLE
            }

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

    companion object DiffCallback : DiffUtil.ItemCallback<Puzzle>() {
        override fun areItemsTheSame(oldItem: Puzzle, newItem: Puzzle): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: Puzzle, newItem: Puzzle): Boolean {
            return oldItem.docId == newItem.docId
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PuzzleViewHolder {

        return PuzzleViewHolder(ItemProfileDreamBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }


    override fun onBindViewHolder(holder: PuzzleViewHolder, position: Int) {

        getItem(position).let {

            holder.bind(it)
        }
    }

    override fun onViewAttachedToWindow(holder: PuzzleViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder.markAttach()
    }

    override fun onViewDetachedFromWindow(holder: PuzzleViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.markDetach()
    }
}