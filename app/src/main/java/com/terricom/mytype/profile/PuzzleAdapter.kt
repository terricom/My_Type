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
import com.terricom.mytype.databinding.ItemProfileLockPuzzleBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val PUZZLE = 0
private const val LOCK = 1

class PuzzleAdapter(
    val viewModel: ProfileViewModel
) : ListAdapter<DataItem, RecyclerView.ViewHolder>(DiffCallback) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)

    fun submitPuzzle(puzzleList: List<Puzzle>){

        adapterScope.launch {
            val newList = mutableListOf<DataItem>()

            for (puzzle in puzzleList){
                newList.add(DataItem.Puzzle(puzzle, viewModel))
            }
            newList.add(DataItem.Lock(viewModel))

            withContext(Dispatchers.Main) {
                submitList(newList)
            }
        }

    }

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

    class LockViewHolder(private var binding: ItemProfileLockPuzzleBinding): RecyclerView.ViewHolder(binding.root),
        LifecycleOwner {

        fun bind(viewModel: ProfileViewModel) {
            binding.lifecycleOwner =this
            binding.viewModel = viewModel
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

    companion object DiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem === newItem
        }
        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is DataItem.Puzzle -> PUZZLE
            is DataItem.Lock -> LOCK
            else -> throw IllegalArgumentException()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when(viewType){

            PUZZLE -> PuzzleViewHolder(ItemProfileDreamBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            LOCK -> LockViewHolder(ItemProfileLockPuzzleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            else -> throw IllegalArgumentException()
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder){
            is PuzzleViewHolder -> {
                val header = getItem(position) as DataItem.Puzzle
                holder.bind(header.puzzle)
            }

            is LockViewHolder -> {
                val header = getItem(position) as DataItem.Lock
                holder.bind(header.viewModel)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is PuzzleViewHolder -> holder.markAttach()
            is LockViewHolder -> holder.markAttach()
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        when (holder) {
            is PuzzleViewHolder -> holder.markDetach()
            is LockViewHolder -> holder.markDetach()
        }
    }
}

sealed class DataItem {

    data class Puzzle(
        val puzzle: com.terricom.mytype.data.Puzzle, val viewModel: ProfileViewModel
    ): DataItem(){

        override val id = puzzle.timestamp.toString()
    }
    data class Lock(
        val viewModel: ProfileViewModel
    ) : DataItem(){

        override val id = (Long.MIN_VALUE).toString()
    }

    abstract val id: String

}
