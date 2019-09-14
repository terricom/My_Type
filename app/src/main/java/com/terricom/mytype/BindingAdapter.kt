package com.terricom.mytype

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.terricom.mytype.data.Pazzle
import com.terricom.mytype.data.PlaceHolder
import com.terricom.mytype.profile.PazzleAdapter


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
                        R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background))
            .into(imgView)
    }
}

@BindingAdapter("pazzles")
fun bindRecyclerViewWithImages(recyclerView: RecyclerView, images: List<Pazzle>?) {
    images?.let {
        recyclerView.adapter?.apply {
            when (this) {
                is PazzleAdapter -> {
                    submitPazzles(it)
                }
            }
        }
    }
}