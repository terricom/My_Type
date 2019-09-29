package com.terricom.mytype

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.databinding.DialogMessageBinding
import kotlinx.android.synthetic.main.activity_main.*

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
        this.isCancelable = true
        com.terricom.mytype.tools.Logger.i("messageType = $messageType")


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun init() {
        when (messageType) {
            MessageType.LOGIN_SUCCESS -> {
                iconRes = App.instance!!.getDrawable(R.drawable.icon_my_type)
                message = getString(R.string.login_success)
            }
            MessageType.ADDED_SUCCESS -> {
                iconRes = App.instance!!.getDrawable(R.drawable.icon_my_type_border)
                message = getString(R.string.dialog_message_add_new_success)
            }
            MessageType.MESSAGE -> {
                iconRes = App.instance!!.getDrawable(R.drawable.icon_my_type_border)
                message = messageType.value.message
            }
            MessageType.GET_PUZZLE -> {
                iconRes = App.instance!!.getDrawable(R.drawable.icon_puzzle)
                message = messageType.value.message
            }


            else -> {

            }
        }
    }

    enum class MessageType(val value: Message) {
        LOGIN_SUCCESS(Message()),
        GET_PUZZLE(Message()),
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

    override fun dismiss() {
        super.dismiss()
        if (messageType == MessageType.ADDED_SUCCESS || messageType == MessageType.LOGIN_SUCCESS){
            this.findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                    (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                    (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                    (activity as MainActivity).fab.visibility = View.VISIBLE
                    (activity as MainActivity).closeFABMenu()
        }
    }
}