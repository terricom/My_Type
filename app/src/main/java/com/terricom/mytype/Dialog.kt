package com.terricom.mytype

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import com.terricom.mytype.databinding.DialogMessageBinding

class MessageDialog : AppCompatDialogFragment() {

    var iconRes: Drawable? = null
    var message: String? = null
    private val messageType by lazy {
        MessageDialogArgs.fromBundle(arguments!!).messageTypeKey
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.MessageDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        init()
        val binding = DialogMessageBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.dialog = this


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Handler().postDelayed({ this.dismiss() }, 2000)
    }

    private fun init() {
        when (messageType) {
//            MessageType.LOGIN_SUCCESS -> {
//                iconRes = App.instance!!.getDrawable(R.drawable.icon_my_type)
//                message = getString(R.string.login_success)
//            }
            MessageType.ADDED_SUCCESS -> {
                iconRes = App.instance!!.getDrawable(R.drawable.icon_my_type)
                message = getString(R.string.dialog_message_add_new_success)
            }
            MessageType.MESSAGE -> {
                iconRes = App.instance!!.getDrawable(R.drawable.icon_my_type)
                message = messageType.value.message
            }

            else -> {

            }
        }
    }

    enum class MessageType(val value: Message) {
        LOGIN_SUCCESS(Message()),
        LOGIN_FAIL(Message()),
        ADDED_SUCCESS(Message()),
        MESSAGE(Message())
    }

    interface IMessage {
        var message: String
    }

    class Message : IMessage {
        private var _message = ""
        override var message: String
            get() = _message
            set(value) { _message = value }
    }
}