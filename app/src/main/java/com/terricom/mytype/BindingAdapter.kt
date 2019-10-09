package com.terricom.mytype

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.terricom.mytype.data.PlaceHolder
import com.terricom.mytype.data.Puzzle
import com.terricom.mytype.profile.PuzzleAdapter
import com.terricom.mytype.tools.Logger


@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String) {
    imgUrl?.let {

        val imgUri = if (imgUrl == "null" || imgUrl == "") PlaceHolder.values().toList().shuffled().first().value.toUri().buildUpon().scheme("https").build() else imgUrl.toUri().buildUpon().scheme("https").build()
        Logger.i("bindImage imgUri =$imgUri")
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(
                        R.drawable.icon_placeholder)
                    .error(R.drawable.icon_placeholder_error))
            .into(imgView)
    }
}

@BindingAdapter("puzzles")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<Puzzle>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PuzzleAdapter -> {
                    submitPuzzles(it)
                }
            }
        }
    }
}

