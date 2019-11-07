package com.terricom.mytype.shaperecord

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentShapeRecordBinding
import com.terricom.mytype.tools.getVmFactory
import com.terricom.mytype.tools.isConnected
import com.terricom.mytype.tools.toDemicalPoint

class ShapeRecordFragment: Fragment(), ShapeCalendarFragment.EventBetweenCalendarAndFragment {

    private val viewModel by viewModels<ShapeRecordViewModel> { getVmFactory() }

    private lateinit var binding: FragmentShapeRecordBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentShapeRecordBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val shape = ShapeRecordFragmentArgs.fromBundle(arguments!!).selectedProperty

        binding.smartCustomCalendar.setEventHandler(this)
        binding.smartCustomCalendar.selectDateOut.observe(this, androidx.lifecycle.Observer {
            viewModel.setDate(it)
        })

        binding.shape = ShapeRecordFragmentArgs.fromBundle(arguments!!).selectedProperty

        when (shape.timestamp){
            null -> {

                binding.smartCustomCalendar.getAndSetDataShape()
                binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
                    binding.smartCustomCalendar.updateCalendar()
                })

                binding.buttonShaperecordSave.setOnClickListener {

                    it.background = App.applicationContext().getDrawable(R.color.colorSecondary)
                    if (((viewModel.weight.value ?: "0.0").toFloat() )
                            .plus((viewModel.bodyWater.value ?: "0.0").toFloat() )
                            .plus((viewModel.bodyFat.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyAge.value ?: "0.0").toFloat())
                            .plus((viewModel.tdee.value ?: "0.0").toFloat())
                            .plus((viewModel.muscle.value ?: "0.0").toFloat()) != 0.0f){

                        if (isConnected()) {

                            viewModel.addOrUpdateShape2Firebase("")
                            viewModel.clearData()
                        }else{

                            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                            it.background = App.applicationContext().getDrawable(R.color.colorMyType)
                        }
                    }else if (((viewModel.weight.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyWater.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyFat.value ?: "0.0").toFloat())
                            .plus((viewModel.tdee.value ?: "0.0").toFloat())
                            .plus((viewModel.muscle.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyAge.value ?: "0.0").toFloat()) == 0.0f){

                        Toast.makeText(App.applicationContext(),resources.getText(R.string.shaperecord_input_hint), Toast.LENGTH_SHORT).show()
                        it.background = App.applicationContext().getDrawable(R.color.colorMyType)
                    }
                }

            }
            else -> {

                binding.shapeRecordTitle.text = App.applicationContext().getString(R.string.shaperecord_edit_accumulation)

                viewModel.setDate(shape.timestamp)
                viewModel.weight.value = shape.weight.toDemicalPoint(1)
                viewModel.bodyFat.value = shape.bodyFat.toDemicalPoint(1)
                viewModel.muscle.value = shape.muscle.toDemicalPoint(1)
                viewModel.bodyAge.value = shape.bodyAge.toDemicalPoint(1)
                viewModel.bodyWater.value = shape.bodyWater.toDemicalPoint(1)
                viewModel.tdee.value = shape.tdee.toDemicalPoint(1)
                binding.textShapeSave.setText(App.applicationContext().getString(R.string.add_new_confirm))

                binding.smartCustomCalendar.getAndSetDataShape()
                binding.smartCustomCalendar.setSelectDate(shape.timestamp)

                binding.smartCustomCalendar.isSelected = true
                binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
                    binding.smartCustomCalendar.updateCalendar()
                })

                binding.buttonShaperecordSave.setOnClickListener {

                    it.background = App.applicationContext().getDrawable(R.color.colorSecondary)

                    if (((viewModel.weight.value ?: "0.0").toFloat() )
                            .plus((viewModel.bodyWater.value ?: "0.0").toFloat() )
                            .plus((viewModel.bodyFat.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyAge.value ?: "0.0").toFloat())
                            .plus((viewModel.tdee.value ?: "0.0").toFloat())
                            .plus((viewModel.muscle.value ?: "0.0").toFloat()) != 0.0f){

                        if (isConnected()) {

                            viewModel.addOrUpdateShape2Firebase(shape.docId)
                            viewModel.clearData()
                        }else{

                            Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                            it.background = App.applicationContext().getDrawable(R.color.colorMyType)
                            //告訴使用者網路無法使用
                        }
                    }else if (((viewModel.weight.value ?: "0.0").toFloat() )
                            .plus((viewModel.bodyWater.value ?: "0.0").toFloat() )
                            .plus((viewModel.bodyFat.value ?: "0.0").toFloat())
                            .plus((viewModel.bodyAge.value ?: "0.0").toFloat())
                            .plus((viewModel.tdee.value ?: "0.0").toFloat())
                            .plus((viewModel.muscle.value ?: "0.0").toFloat()) != 0.0f){

                        Toast.makeText(App.applicationContext(),resources.getText(R.string.shaperecord_input_hint), Toast.LENGTH_SHORT).show()
                        it.background = App.applicationContext().getDrawable(R.color.colorMyType)
                    }

                }

            }
        }


        viewModel.date.observe(this, androidx.lifecycle.Observer {
            viewModel.getRecordedDates()
        })

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).back2DiaryFragment()

            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).back2DiaryFragment()

        }


        viewModel.isAddDataShape.observe(this, androidx.lifecycle.Observer {
            if (it == true){
                this.findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.ADDED_SUCCESS))
            } else if (it == false){

                findNavController().navigate(NavigationDirections.navigateToMessageDialog(

                    MessageDialog.MessageType.MESSAGE.apply { value.message = getString(R.string.dialog_message_shape_record_failure)}
                ))
                binding.buttonShaperecordSave.background  = App.applicationContext().getDrawable(R.color.colorMyType)
            }
        })

        binding.buttonShapeShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToShapeRecordDialog())
        }

        return binding.root
    }

    override fun onCalendarNextPressed() {
        binding.smartCustomCalendar.getAndSetDataShape()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }

    override fun onCalendarPreviousPressed() {
        binding.smartCustomCalendar.getAndSetDataShape()
        binding.smartCustomCalendar.recordedDate.observe(this, androidx.lifecycle.Observer {
            binding.smartCustomCalendar.updateCalendar()
        })

    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).backFromEditPage()
    }

}