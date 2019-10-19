package com.terricom.mytype

import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.terricom.mytype.data.PlaceHolder
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

@BindingAdapter("loadingStatus")
fun bindApiStatus(view: ProgressBar, status: Boolean?) {
    when (status) {
        true -> view.visibility = View.INVISIBLE
        null, false -> view.visibility = View.VISIBLE
    }
}


