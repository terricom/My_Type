package com.terricom.mytype.foodie

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.terricom.mytype.*
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.UserManager
import com.terricom.mytype.databinding.FragmentFoodieRecordBinding
import com.terricom.mytype.tools.*
import kotlinx.android.synthetic.main.fragment_foodie_record.*
import java.io.*
import java.sql.Timestamp
import java.util.*


class FoodieFragment: Fragment() {

    private val viewModel: FoodieViewModel by lazy {
        ViewModelProviders.of(this).get(FoodieViewModel::class.java)
    }
    private lateinit var binding: FragmentFoodieRecordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        binding = FragmentFoodieRecordBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //處理從其他 Fragment 帶 Argument 過來的情況

        arguments?.let {
            foodie = FoodieFragmentArgs.fromBundle(it).selectedProperty

            foodie?.let { foodie ->
                binding.foodie = foodie
                foodie.timestamp?.let{ it ->

                    viewModel.setCurrentDate(it)
                    binding.editDate.text = it.toDateFormat(FORMAT_YYYY_MM_DD)
                    binding.editTime.text = it.toDateFormat(FORMAT_HH_MM)

                    viewModel.getHistoryFoodie(foodie)
                    binding.foodieTitle.text = App.applicationContext().getString(R.string.foodie_edit_foodie)

                    viewModel.water.value = foodie.water
                    viewModel.fruit.value = foodie.fruit
                    viewModel.protein.value = foodie.protein
                    viewModel.vegetable.value = foodie.vegetable
                    viewModel.oil.value = foodie.oil
                    viewModel.carbon.value = foodie.carbon

                    foodie.foods?.let {foods ->

                        if (foods.isNotEmpty()){

                            for (food in foods){

                                viewModel.addToFoodList(food)
                            }

                            binding.recyclerAddedFoods.adapter = FoodAdapter(
                                viewModel, FoodAdapter.OnClickListener{
                                    viewModel.dropOutFoodList(it)
                                })

                            when (binding.recyclerAddedFoods.childCount){

                                0 -> binding.dragFoodHint.visibility = View.VISIBLE
                                else -> {
                                    binding.dragFoodHint.visibility = View.GONE
                                    binding.recyclerAddedFoods.smoothScrollToPosition(
                                        binding.recyclerAddedFoods.childCount-1)
                                }
                            }

                            (binding.recyclerAddedFoods.adapter as FoodAdapter).submitFoodsWithEdit(foodie.foods)

                            binding.recyclerAddedFoods.addItemDecoration(
                                SpaceItemDecoration(
                                    resources.getDimension(R.dimen.recyclerview_between).toInt(),
                                    true
                                )
                            )
                        }

                    }

                    foodie.nutritions?.let {nutritions ->

                        if (nutritions.isNotEmpty()){

                            for (nutrition in nutritions){

                                viewModel.addToNutritionList(nutrition)
                            }

                            binding.recyclerAddedNutritions.adapter = NutritionAdapter(
                                viewModel, NutritionAdapter.OnClickListener{
                                viewModel.dropOutNutritionList(it)
                            })

                            when (binding.recyclerAddedNutritions.childCount){

                                0 -> binding.dragNutritionHint.visibility = View.VISIBLE
                                else -> {
                                    binding.dragNutritionHint.visibility = View.GONE
                                    binding.recyclerAddedNutritions.smoothScrollToPosition(
                                        binding.recyclerAddedNutritions.childCount-1)
                                }
                            }
                            (binding.recyclerAddedNutritions.adapter as NutritionAdapter)
                                .submitNutritionsWithEdit(foodie.nutritions)
                            binding.recyclerAddedNutritions.addItemDecoration(SpaceItemDecoration(
                                resources.getDimension(R.dimen.recyclerview_between).toInt(),true)
                            )
                        }
                    }

                    foodie.memo?.let {

                        if (it.isNotEmpty()){
                            viewModel.memo.value = foodie.memo
                        }
                    }

                    binding.textFoodieSave.text = App.applicationContext().getString(R.string.add_new_confirm)

                }
            }
        }


        //控制 DatePicker 顯示與否
        controlDateAndTimePicker()

        //控制新增或移除食物清單與營養素清單的項目
        addOrDropFoodAndNutritionList()

        //點擊新增相片要判斷是否取得權限和跳出選項的對話框
        binding.foodiePhoto.setOnClickListener{

            getPermissions()
            if (isUploadPermissionsGranted){

                selectImage()
            } else if (!isUploadPermissionsGranted){

                Toast.makeText(App.applicationContext(), App.applicationContext()
                    .getString(R.string.foodie_upload_permission_hint), Toast.LENGTH_SHORT).show()
            }
        }

        binding.buttonFoodieShowInfo.setOnClickListener {

            findNavController().navigate(NavigationDirections.navigateToReferenceDialog())
        }

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

        //取得手機解析度決定是否要壓縮
        displayMetrics = DisplayMetrics()
        getWindowManager(App.applicationContext()).defaultDisplay.getMetrics(displayMetrics)

        binding.buttonFoodieSave.setOnClickListener { it ->

            if (isConnected()) {

                //沒有輸入營養素則無法送出食記
                if ((viewModel.water.value ?: 0.0f).plus(viewModel.fruit.value ?: 0.0f)
                        .plus(viewModel.vegetable.value ?: 0.0f).plus(viewModel.oil.value ?: 0.0f)
                        .plus(viewModel.protein.value ?: 0.0f).plus(viewModel.carbon.value ?: 0.0f) != 0.0f){

                    it.background = App.applicationContext().getDrawable(R.color.colorSecondary)

                    viewModel.setCurrentDate(Date(Timestamp.valueOf(App.applicationContext()
                        .getString(R.string.simpledateformat_date_time_to_timestamp,
                            "${binding.editDate.text}", "${binding.editTime.text}")).time))

                    foodie?.let {foodie ->
                        if (foodie.timestamp != null){
                            viewModel.adjustOldFoodie()
                        } else {
                            viewModel.addNewFoodie()
                        }
                    }

                    viewModel.updateFoodAndNuList()
                    viewModel.clearData()

                    findNavController().navigate(NavigationDirections.navigateToMessageDialog(MessageDialog.MessageType.ADDED_SUCCESS))

                } else {
                    Toast.makeText(App.applicationContext(),resources.getText(R.string.foodie_input_hint), Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT).show()
                //告訴使用者網路無法使用
            }

        }

        return binding.root
    }

    private fun controlDateAndTimePicker(){

        viewModel.isEditDateClicked.observe(this, Observer {

            if (!it){

                binding.editDate.setOnClickListener {

                    val year = binding.datePicker.year.toString()
                    val month =
                        when (binding.datePicker.month+1){

                            10,11,12 -> "${binding.datePicker.month+1}"
                            else -> "0${binding.datePicker.month+1}"
                        }
                    val day =
                        when (binding.datePicker.dayOfMonth){

                            1,2,3,4,5,6,7,8,9 -> "0${binding.datePicker.dayOfMonth}"
                            else -> "${binding.datePicker.dayOfMonth}"
                        }

                    binding.datePicker.visibility = View.INVISIBLE
                    binding.editDate.text = App.applicationContext().getString(R.string.year_month_date,year,month,day)

                    viewModel.editDateClicked()
                }
            }else if (it){

                binding.editDate.setOnClickListener {

                    binding.datePicker.visibility = View.VISIBLE
                    viewModel.editDateClickedAgain()
                }
            }
        })

        viewModel.isEditTimeClicked.observe(this, Observer {

            if (!it){
                binding.editTime.setOnClickListener {

                    val hour =
                        when (binding.timePicker2.hour){

                            0,1,2,3,4,5,6,7,8,9 -> "0${binding.timePicker2.hour}"
                            else ->"${binding.timePicker2.hour}"
                        }

                    val minute =
                        when (binding.timePicker2.minute){

                            0,1,2,3,4,5,6,7,8,9 -> "0${binding.timePicker2.minute}"
                            else -> "${binding.timePicker2.minute}"
                        }


                    binding.timePicker2.visibility = View.INVISIBLE
                    binding.editTime.text = App.applicationContext().getString(R.string.hour_minute, hour, minute)
                    viewModel.editTimeClicked()
                }
            }else if (it){
                binding.editTime.setOnClickListener {

                    binding.timePicker2.visibility = View.VISIBLE
                    viewModel.editTimeClickedAgain()
                }
            }
        })
    }

    private fun addOrDropFoodAndNutritionList(){

        binding.foodsRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.OnClickListener{
            viewModel.addToFoodList(it) //點選後加入清單
        })

        // 加入從 Firebase 拿到的歷史清單，若歷史清單尚未建立則加上新增的選項
        viewModel.userFoodList.observe(this, Observer {

            if (it.isNullOrEmpty()){

                (binding.foodsRecycler.adapter as FoodAdapter).submitFoods(listOf(App.applicationContext()
                    .getString(R.string.foodie_add_food)))
            }else {

                (binding.foodsRecycler.adapter as FoodAdapter).submitFoods(it)
            }

        })

        binding.foodsRecycler.addItemDecoration(SpaceItemDecoration(
            resources.getDimension(R.dimen.recyclerview_between).toInt(), true)
        )

        binding.nutritionRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.OnClickListener{

            viewModel.addToNutritionList(it)
        })

        viewModel.userNutritionList.observe(this, Observer {

            if (it.isNullOrEmpty()){

                (binding.nutritionRecycler.adapter as NutritionAdapter)
                    .submitNutritions(listOf(App.applicationContext().getString(R.string.diary_add_nutrition)))
            }else {

                (binding.nutritionRecycler.adapter as NutritionAdapter).submitNutritions(it)
            }
        })
        binding.nutritionRecycler.addItemDecoration( SpaceItemDecoration(
            resources.getDimension(R.dimen.recyclerview_between).toInt(), true)
        )

        binding.recyclerAddedFoods.adapter = FoodAdapter(viewModel, FoodAdapter.OnClickListener{
            viewModel.dropOutFoodList(it)
        })

        // 改變 FoodAdapter 的變數，決定點擊事件的結果是加入清單或移除清單
        (binding.recyclerAddedFoods.adapter as FoodAdapter).addOrRemove = false

        binding.recyclerAddedFoods.addItemDecoration(SpaceItemDecoration(
            resources.getDimension(R.dimen.recyclerview_between).toInt(), true)
        )
        viewModel.selectedFoodList.observe(this, Observer {

            binding.dragFoodHint.visibility = View.GONE
            (binding.recyclerAddedFoods.adapter as FoodAdapter).submitFoodsWithEdit(it.distinct())

            if (it.isNotEmpty()){

                binding.recyclerAddedFoods.smoothScrollToPosition(it.lastIndex)
            }
        })
        if (binding.recyclerAddedFoods.childCount != 0){

            binding.recyclerAddedFoods.smoothScrollToPosition(binding.recyclerAddedFoods.childCount-1)
        }

        binding.recyclerAddedNutritions.adapter = NutritionAdapter(viewModel, NutritionAdapter.OnClickListener{
            viewModel.dropOutNutritionList(it)
        })
        (binding.recyclerAddedNutritions.adapter as NutritionAdapter).addOrRemove = false

        viewModel.selectedNutritionList.observe(this, Observer {

            binding.dragNutritionHint.visibility = View.GONE
            (binding.recyclerAddedNutritions.adapter as NutritionAdapter).submitNutritionsWithEdit(it)

            if (it.isNotEmpty()){
                binding.recyclerAddedNutritions.smoothScrollToPosition(it.lastIndex)
            }
        })
        if (binding.recyclerAddedNutritions.childCount != 0){
            binding.recyclerAddedNutritions.smoothScrollToPosition(binding.recyclerAddedNutritions.childCount-1)
        }

        binding.recyclerAddedNutritions.addItemDecoration(SpaceItemDecoration(
            resources.getDimension(R.dimen.recyclerview_between).toInt(), true)
        )
    }


    private fun getWindowManager(context: Context): WindowManager {

        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        return windowManager as WindowManager
    }

    private fun showGallery() {

        val intent = Intent()
        intent.type = App.applicationContext().getString(R.string.foodie_show_gallery_intent_type)
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, App.applicationContext()
            .getString(R.string.foodie_show_gallery_select_picture)),
            IMAGE_FROM_GALLERY
        )
    }

    private fun startCamera() {

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(App.applicationContext().packageManager)!= null){

            try {
                fileFromCamera = createImageFile()

            }catch (ex: IOException){
                return
            }
            if (fileFromCamera != null){
                val photoURI = getUriForFile(this.context!!
                    , App.applicationContext().packageName+
                            App.applicationContext().getString(R.string.foodie_start_camera_provider),
                    fileFromCamera!!)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, IMAGE_FROM_CAMERA)
            }
        }
    }

    // Create an image file name
    private fun createImageFile(): File {

        //This is the directory in which the file will be created. This is the default location of Camera photos
        val storageDir = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM), App.applicationContext().getString(R.string.foodie_start_camera_camera))

        return File.createTempFile(
            viewModel.date.value.toDateFormat(FORMAT_YYYY_MM_DDHHMMSS),  /* prefix */
                App.applicationContext().getString(R.string.foodie_start_camera_jpg), /* suffix */
                storageDir      /* directory */
        )
    }


    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){

            when (requestCode){
                IMAGE_FROM_GALLERY ->{

                    data?.let {

                        it.data?.let {data ->

                            filePath = data
                            uploadFile()

                            try {
                                bitmap = MediaStore.Images.Media.getBitmap(
                                    (activity as MainActivity).contentResolver, filePath)

                                val matrix = Matrix()
                                matrix.postRotate(getImageRotation(App.applicationContext(), data).toFloat())

                                val outBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                    bitmap!!.width, bitmap!!.height, matrix, false)

                                outBitmap.compress(Bitmap.CompressFormat.JPEG, 15, ByteArrayOutputStream())
                                scalePic(outBitmap, displayMetrics!!.widthPixels)

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                IMAGE_FROM_CAMERA -> {

                    fileFromCamera?.let {

                        filePath = getUriForFile(this.context!!,
                            App.applicationContext().packageName+ App.applicationContext()
                                .getString(R.string.foodie_start_camera_provider), it)

                        uploadFile()

                        filePath?.let { it ->

                            bitmap =
                                MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, it)

                            val matrix = Matrix()
                            matrix.postRotate(getImageRotation(App.applicationContext(), it).toFloat())

                            val outBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap!!.width, bitmap!!.height, matrix, false)

                            outBitmap.compress(Bitmap.CompressFormat.JPEG, 15, ByteArrayOutputStream())
                            scalePic(outBitmap, displayMetrics!!.widthPixels)
                        }
                    }
                }
            }
        }
    }

    private fun scalePic(bitmap:Bitmap, phone: Int)
    {
        //縮放比例預設為1
        var scaleRate = 1f

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if(bitmap.width > phone) {

            scaleRate = phone.toFloat()/ bitmap.width.toFloat() //判斷縮放比例

            val matrix = Matrix()
            matrix.setScale(scaleRate, scaleRate)

            foodiePhoto.setImageBitmap(
                Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.width, bitmap.height, matrix, false))
        }
        else foodiePhoto.setImageBitmap(bitmap)
    }

    private fun getImageRotation(context: Context, uri: Uri): Int {
            var stream: InputStream? = null
            return try {
                stream = context.contentResolver.openInputStream(uri)
                val exifInterface = ExifInterface(stream)
                val exifOrientation =
                    exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                when (exifOrientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270
                    else -> 0
                }
            } catch (e: Exception) {
                0
            } finally {
                stream?.close()
            }
        }


    private fun selectImage() {

        val items = arrayOf<CharSequence>(
            App.applicationContext().resources.getText(R.string.foodie_add_photo)
            , App.applicationContext().resources.getText(R.string.foodie_choose_from_gallery)
            , App.applicationContext().resources.getText(R.string.foodie_cancel)
        )

        val context = this.context

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(App.applicationContext().resources.getText(R.string.foodie_add_photo_title))
        builder.setItems(items) { dialog, item ->
            if (items[item] == App.applicationContext().resources.getText(R.string.foodie_cancel)) {

                dialog.dismiss()
            } else {

                chooseCameraOrGallery = items[item].toString()
                callCameraOrGallery()
            }
        }

        builder.show()
    }

    private fun callCameraOrGallery() {

        chooseCameraOrGallery?.let {

            when(it){

                App.applicationContext().resources.getString(R.string.foodie_add_photo) -> {

                    startCamera()
                }
                App.applicationContext().resources.getString(R.string.foodie_choose_from_gallery) -> {

                    showGallery()
                }
            }
        }

    }


    private fun uploadFile(){

        filePath?.let { filePath ->

            viewModel.uploadPhoto()

            UserManager.uid?.let {uid ->

                // Firebase storage
                auth = FirebaseAuth.getInstance()

                val imageReference = FirebaseStorage.getInstance().reference
                    .child(App.applicationContext()
                    .getString(R.string.firebase_storage_reference, uid,
                        viewModel.date.value.toDateFormat(FORMAT_YYYY_MM_DDHHMMSS)))

                compress(filePath)?.let { compressResult ->

                    imageReference.putBytes(compressResult)
                        .addOnCompleteListener{

                            imageReference.downloadUrl.addOnCompleteListener { task ->

                                task.result?.let { taskResult ->

                                    viewModel.setPhoto(taskResult)}
                            }
                        }
                }
            }
        }
    }

    private fun compress(image: Uri): ByteArray? {

        var imageStream: InputStream? = null

        try {
            imageStream = App.applicationContext().contentResolver.openInputStream(image)
        } catch (e: FileNotFoundException) {

            e.printStackTrace()
        }

        val bitmapOrigin = BitmapFactory.decodeStream(imageStream)

        val stream = ByteArrayOutputStream()
        // 縮小至 15 %
        bitmapOrigin.compress(Bitmap.CompressFormat.JPEG, 15, stream)
        val byteArray = stream.toByteArray()

        try {

            stream.close()
            return byteArray
        } catch (e: IOException) {

            e.printStackTrace()
        }

        return null
    }


    private fun getPermissions() {

        val permissions = arrayOf(
            PERMISSION_CAMERA,
            PERMISSION_READ_EXTERNAL_STORAGE,
            PERMISSION_WRITE_EXTERNAL_STORAGE
        )

        when (ContextCompat.checkSelfPermission(App.applicationContext(),
            PERMISSION_CAMERA)){

            PackageManager.PERMISSION_GRANTED -> {

                when (ContextCompat.checkSelfPermission(App.applicationContext(),
                        PERMISSION_WRITE_EXTERNAL_STORAGE)) {

                    PackageManager.PERMISSION_GRANTED -> {

                        when (ContextCompat.checkSelfPermission(App.applicationContext(),
                            PERMISSION_READ_EXTERNAL_STORAGE)) {

                            PackageManager.PERMISSION_GRANTED -> {

                                isUploadPermissionsGranted = true
                            }
                        }
                    }

                    else -> {
                        ActivityCompat.requestPermissions(activity as MainActivity,
                            permissions,
                            SELECT_PHOTO_PERMISSION_REQUEST_CODE
                        )
                    }
                }
            }

            else -> {
                ActivityCompat.requestPermissions(activity as MainActivity,
                    permissions,
                    SELECT_PHOTO_PERMISSION_REQUEST_CODE
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        isUploadPermissionsGranted = false

        when (requestCode) {

            SELECT_PHOTO_PERMISSION_REQUEST_CODE ->

                if (grantResults.isNotEmpty()) {

                    for (i in 0 until grantResults.size) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {

                            isUploadPermissionsGranted = false
                            return
                        }
                    }
                    isUploadPermissionsGranted = true
                    try {
                    } catch ( e: IOException) {
                        e.printStackTrace()
                    }
                }
        }
    }


    override fun onStop() {

        super.onStop()
        (activity as MainActivity).backFromEditPage()
    }

    companion object {
        private const val PERMISSION_CAMERA = Manifest.permission.CAMERA
        private const val PERMISSION_WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE
        private const val PERMISSION_READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE
        private const val SELECT_PHOTO_PERMISSION_REQUEST_CODE = 1234

        //Image request code
        private const val IMAGE_FROM_CAMERA = 0
        private const val IMAGE_FROM_GALLERY = 1
        private var chooseCameraOrGallery: String? = null

        //Uri to store the image uri
        private var filePath: Uri? = null
        //Bitmap to get image from gallery
        private var bitmap: Bitmap? = null
        private var auth: FirebaseAuth ?= null
        private var displayMetrics: DisplayMetrics?= null
        private var windowManager: WindowManager? = null
        private var fileFromCamera: File ?= null
        private var isUploadPermissionsGranted = false
        private var foodie: Foodie? = null
    }

}