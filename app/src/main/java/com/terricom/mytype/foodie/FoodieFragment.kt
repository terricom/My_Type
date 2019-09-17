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
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.FileProvider.getUriForFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.terricom.mytype.*
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.databinding.FragmentFoodieRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_foodie_record.*
import java.io.*
import java.text.SimpleDateFormat


class FoodieFragment: Fragment() {

    private val viewModel: FoodieViewModel by lazy {
        ViewModelProviders.of(this).get(FoodieViewModel::class.java)
    }

    //Image request code
    private val CAMERA_IMAGE = 0
    private val PICK_IMAGE_REQUEST = 1

    //Bitmap to get image from gallery
    private var bitmap: Bitmap? = null

    //Uri to store the image uri
    private var filePath: Uri? = null

    private var storageReference: StorageReference ?= null
    private var auth: FirebaseAuth ?= null

    private var mPhone: DisplayMetrics?= null

    private var windowManager: WindowManager? = null
    val sdf = SimpleDateFormat("yyyy-MM-dd-HHmmss")
    var pictureFile: File ?= null

    private lateinit var binding: FragmentFoodieRecordBinding
    private lateinit var editableFoods: MutableList<String>
    private lateinit var editableNutritions: MutableList<String>

    private var mLocationPermissionsGranted = false

    private val FINE_LOCATION = Manifest.permission.CAMERA
    private val COURSE_LOCATION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private val LOCATION_PERMISSION_REQUEST_CODE = 1234

    private var imageFilePathFromCamera: String? = null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val foodie = FoodieFragmentArgs.fromBundle(arguments!!).selectedProperty

        binding = FragmentFoodieRecordBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //處理從其他 Fragment 帶 Argument 過來的情況
        binding.foodie = foodie
        if (foodie.timestamp != null){
            viewModel.updateFoodie(foodie)
            binding.foodieTitle.setText("修改食記")

            viewModel.setDate(foodie.timestamp!!)
            viewModel.water.value = foodie.water
            viewModel.fruit.value = foodie.fruit
            viewModel.protein.value = foodie.protein
            viewModel.vegetable.value = foodie.vegetable
            viewModel.oil.value = foodie.oil
            viewModel.carbon.value = foodie.carbon


            if (foodie.foods!!.isNotEmpty()){
                editableFoods = foodie.foods!!.toMutableList()
                for (food in foodie.foods!!){
                    viewModel.dragToList(food)
                }
                binding.dagFoodHint.visibility = View.GONE
                binding.foodsTransportedRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.LongClickListener())
                (binding.foodsTransportedRecycler.adapter as FoodAdapter).submitFoods(foodie.foods)
                binding.foodsTransportedRecycler.addItemDecoration(
                    SpaceItemDecoration(
                        resources.getDimension(R.dimen.recyclerview_between).toInt(),
                        true
                    )
                )
                val owner = binding.foodsTransportedRecycler.parent as ViewGroup
                owner.removeView(binding.foodsTransportedRecycler)
                binding.chosedFood.addView(binding.foodsTransportedRecycler)
                binding.foodsTransportedRecycler.setOnLongClickListener(FoodAdapter.LongClickListener())
            }else {
                editableFoods = mutableListOf("")
            }

            if (foodie.nutritions!!.isNotEmpty()){
                editableNutritions = foodie.nutritions!!.toMutableList()
                for (nutrition in foodie.nutritions!!){
                    viewModel.dragOutListNu(nutrition)
                }
                binding.dragNutritionHint.visibility = View.GONE
                binding.nutritionsTransportedRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.LongClickListenerNu())
                (binding.nutritionsTransportedRecycler.adapter as NutritionAdapter).submitNutritions(foodie.nutritions)
                binding.nutritionsTransportedRecycler.addItemDecoration(
                    SpaceItemDecoration(
                        resources.getDimension(R.dimen.recyclerview_between).toInt(),
                        true
                    )
                )
                val owner = binding.nutritionsTransportedRecycler.parent as ViewGroup
                owner.removeView(binding.nutritionsTransportedRecycler)
                binding.chosedNutrition.addView(binding.nutritionsTransportedRecycler)
                binding.nutritionsTransportedRecycler.setOnLongClickListener(NutritionAdapter.LongClickListenerNu())
            }else{
                editableNutritions = mutableListOf("")
            }
            binding.textFoodieSave.setText("確認修改")

        }else {
            editableNutritions = mutableListOf("")
            editableFoods = mutableListOf("")
        }

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        binding.buttonFoodieShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToReferenceDialog())
        }

        binding.foodsRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.LongClickListener())
        viewModel.userFoodList.observe(this, androidx.lifecycle.Observer {
            (binding.foodsRecycler.adapter as FoodAdapter).addHeaderAndSubmitList(it)
            binding.foodsRecycler.addItemDecoration(
                SpaceItemDecoration(
                    resources.getDimension(R.dimen.recyclerview_between).toInt(),
                    true
                )
            )
        })

        binding.nutritionRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.LongClickListenerNu())
        viewModel.userNuList.observe(this, androidx.lifecycle.Observer {
            (binding.nutritionRecycler.adapter as NutritionAdapter).addHeaderAndSubmitListNu(it)
            binding.nutritionRecycler.addItemDecoration(
                SpaceItemDecoration(
                    resources.getDimension(R.dimen.recyclerview_between).toInt(),
                    true
                )
            )
            (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()
        })

        binding.foodiePhoto.setOnClickListener{
            //Requesting storage permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getPermissions()
            }
            selectImage()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)

        binding.buttonBack2Main.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
            (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
            (activity as MainActivity).fab.visibility = View.VISIBLE
        }



        class MyDragListener : View.OnDragListener {

            override fun onDrag(v: View, event: DragEvent): Boolean {
                val action = event.action
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                    }
                    DragEvent.ACTION_DROP -> {
                        // Dropped, reassign View to ViewGroup
                        val view = event.localState as View
                        val owner = view.parent as ViewGroup
                        owner.removeView(view)
                        val container = v as LinearLayout
                        container.addView(view)
                        view.visibility = View.VISIBLE
                        view.setOnTouchListener(FoodAdapter.MyTouchListener())
                        binding.dagFoodHint.visibility = View.INVISIBLE
                        viewModel.dragToList("${view.findViewById<TextView>(R.id.food).text}")
//                        viewModel.dragToList("${view.findViewById<EditText>(R.id.food).text}")
                        (binding.foodsRecycler.adapter as FoodAdapter).addHeaderAndSubmitList(viewModel.userFoodList.value)
                       editableFoods.add("${view.findViewById<TextView>(R.id.food).text}")
                    }

                    else -> {
                    }
                }// do nothing
                return true
            }
        }

        class MyDragListenerNu : View.OnDragListener {

            override fun onDrag(v: View, event: DragEvent): Boolean {
                val action = event.action
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                    }
                    DragEvent.ACTION_DROP -> {
                        // Dropped, reassign View to ViewGroup
                        val view = event.localState as View
                        val owner = view.parent as ViewGroup
                        owner.removeView(view)
                        val container = v as LinearLayout
                        container.addView(view)
                        view.visibility = View.VISIBLE
                        view.setOnTouchListener(NutritionAdapter.MyTouchListener())
                        binding.dragNutritionHint.visibility = View.INVISIBLE
                        viewModel.dragToListNu("${view.findViewById<TextView>(R.id.nutrition).text}")
                        editableNutritions.add("${view.findViewById<TextView>(R.id.nutrition).text}")}
                    else -> {
                    }
                }// do nothing
                return true
            }
        }

        class CleanDragListener : View.OnDragListener {

            override fun onDrag(v: View, event: DragEvent): Boolean {
                val action = event.action
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                    }
                    DragEvent.ACTION_DROP -> {
                        // Dropped, reassign View to ViewGroup
                        val view = event.localState as View
                        val owner = view.parent as ViewGroup
                        owner.removeView(view)
                        val container = v as LinearLayout
                        container.addView(view)
                        view.visibility = View.GONE
                        viewModel.dragOutList("${view.findViewById<TextView>(R.id.food).text}")
//                        viewModel.dragToList("${view.findViewById<EditText>(R.id.food).text}")
                        (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()
                        if ((foodie.foods ?: listOf<String>("")).isNotEmpty()){
                            editableFoods.remove("${view.findViewById<TextView>(R.id.food).text}")
                        }
                        foodie.foods?.let {
                            editableFoods.remove("")
                            binding.foodsTransportedRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.LongClickListener())
                            (binding.foodsTransportedRecycler.adapter as FoodAdapter).submitFoods(editableFoods)
                        }

                    }
                    else -> {
                    }
                }// do nothing
                return true
            }
        }

        class CleanDragListenerNu : View.OnDragListener {

            override fun onDrag(v: View, event: DragEvent): Boolean {
                val action = event.action
                when (event.action) {
                    DragEvent.ACTION_DRAG_STARTED -> {
                    }
                    DragEvent.ACTION_DROP -> {
                        // Dropped, reassign View to ViewGroup
                        val view = event.localState as View
                        val owner = view.parent as ViewGroup
                        owner.removeView(view)
                        val container = v as LinearLayout
                        container.addView(view)
                        view.visibility = View.GONE
                        viewModel.dragOutListNu("${view.findViewById<TextView>(R.id.nutrition).text}")
//                        viewModel.dragToList("${view.findViewById<EditText>(R.id.food).text}")
                        (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()
                        if ((foodie.nutritions ?: listOf("")).isNotEmpty()){
                        editableNutritions.remove("${view.findViewById<TextView>(R.id.nutrition).text}")
                        }
                        foodie.nutritions?.let {
                            editableNutritions.remove("")
                            Logger.i("editableNutritions =${editableNutritions}")
                            binding.nutritionsTransportedRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.LongClickListenerNu())
                            (binding.nutritionsTransportedRecycler.adapter as NutritionAdapter).submitNutritions(editableNutritions)
                        }
                    }
                    else -> {
                    }
                }// do nothing
                return true
            }
        }

        Logger.i("viewModel.selectedFood = ${viewModel.selectedFood}")
        binding.foodsMoveOut.setOnDragListener(CleanDragListener())
        binding.chosedFood.setOnDragListener(MyDragListener())
        binding.chosedNutrition.setOnDragListener(MyDragListenerNu())
        binding.nutritionsMoveOut.setOnDragListener(CleanDragListenerNu())

        //讀取手機解析度
        mPhone = DisplayMetrics()
        getWindowManager(App.applicationContext()).getDefaultDisplay().getMetrics(mPhone)


        binding.buttonFoodieSave.setOnClickListener {

            if (foodie.docId != ""){
                viewModel.adjustFoodie()
                viewModel.updateFoodAndNuList()
                viewModel.clearData()
            } else {
                viewModel.addFoodie()
                viewModel.updateFoodAndNuList()
                viewModel.clearData()
            }

            Handler().postDelayed({
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).fab.visibility = View.VISIBLE
            },2000)

            
            if (isConnected()) {
                Logger.i("NetworkConnection Network Connected.")
                //執行下載任務
            }else{
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
                //告訴使用者網路無法使用
            }

        }

        return binding.root
    }


    private fun getWindowManager(context: Context): WindowManager {
        if (windowManager == null) {
            windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }
        return windowManager as WindowManager
    }

    //method to show file chooser
    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
        Logger.i("Inside showFileChooser()")
    }

    private fun fromcamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    if (intent.resolveActivity(App.applicationContext().packageManager)!= null){
        try {
            pictureFile = createImageFile()

        }catch (ex: IOException){
            return
        }
        if (pictureFile != null){
            val photoURI = FileProvider.getUriForFile(this.context!!
                , App.applicationContext().packageName+ ".provider", pictureFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, CAMERA_IMAGE)
        }}}

    // Create an image file name
    private fun createImageFile(): File {

        //This is the directory in which the file will be created. This is the default location of Camera photos
        val storageDir = File(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DCIM), "Camera")
        Logger.i("storageDir = $storageDir")
        val image = File.createTempFile(
            sdf.format(viewModel.date.value),  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        )
        // Save a file: path for using again
        imageFilePathFromCamera = "file://" + image.getAbsolutePath()

        return image
    }


    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            Logger.i("@FoodieFragment onActivityResult filePath =$filePath")
            try {

                bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, filePath)
                val degree = getImageRotation(App.applicationContext(),filePath!!)
                Logger.i("degree = $degree")
                val matrix = Matrix()
                matrix.postRotate(degree.toFloat())
                val outBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap!!.width, bitmap!!.height, matrix, false)
                val baos = ByteArrayOutputStream()
                outBitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos)
                if(outBitmap!!.getWidth()>outBitmap!!.getHeight())ScalePic(outBitmap!!, mPhone!!.heightPixels)
                else ScalePic(outBitmap!!, mPhone!!.widthPixels)
//                bitmap!!.recycle()
                uploadFile()

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }else if (requestCode == CAMERA_IMAGE && resultCode == Activity.RESULT_OK){
            val contentUri: Uri = getUriForFile(this.context!!
                , App.applicationContext().packageName+ ".provider", pictureFile!!)
            filePath = contentUri
            bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, filePath)
            val degree = getImageRotation(App.applicationContext(),filePath!!)
            Logger.i("degree = $degree")
            val matrix = Matrix()
            matrix.postRotate(degree.toFloat())
            val outBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap!!.width, bitmap!!.height, matrix, false)
            val baos = ByteArrayOutputStream()
            outBitmap.compress(Bitmap.CompressFormat.JPEG, 5, baos)
            if(outBitmap!!.getWidth()>outBitmap!!.getHeight())ScalePic(outBitmap!!, mPhone!!.heightPixels)
            else ScalePic(outBitmap!!, mPhone!!.widthPixels)

                uploadFile()
        }
    }

    private fun ScalePic( bitmap:Bitmap, phone: Int)
    {
        //縮放比例預設為1
        var mScale = 1f

        //如果圖片寬度大於手機寬度則進行縮放，否則直接將圖片放入ImageView內
        if(bitmap.getWidth() > phone )
        {
            //判斷縮放比例
            mScale = phone.toFloat()/ bitmap.getWidth().toFloat()

            val mMat: Matrix = Matrix()
            mMat.setScale(mScale, mScale)

            var mScaleBitmap = Bitmap.createBitmap(bitmap,
            0,
            0,
            bitmap.getWidth(),
            bitmap.getHeight(),
            mMat,
            false)
            foodiePhoto.setImageBitmap(mScaleBitmap)
        }
        else foodiePhoto.setImageBitmap(bitmap)
    }


    private fun isConnected(): Boolean{
        val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
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


    private var userChoosenTask: String? = null

    fun selectImage() {

        val items = arrayOf<CharSequence>("Take Photo", "Choose from Library", "Cancel")

        val context = this.context

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle("Add Photo!")
        builder.setItems(items) { dialog, item ->
            if (items[item] == "Cancel") {
                dialog.dismiss()
            } else {
                userChoosenTask = items[item].toString()
                callCameraOrGallery()
            }
        }
        builder.show()
    }

    private fun callCameraOrGallery() {

        if (userChoosenTask.equals("Take Photo")) {
            userChoosenTask = "Take Photo"

            fromcamera()

        } else if (userChoosenTask.equals("Choose from Library")) {
            userChoosenTask = "Choose from Library"

            showFileChooser()

        } else {
        }

    }


    private fun uploadFile(){
        if (filePath != null){
            auth = FirebaseAuth.getInstance()
            val userId = auth!!.currentUser!!.uid
            val data = compress(filePath!!)
            val imgRef = storageReference!!.child("images/users/"+ userId+"/"
                    +sdf.format(viewModel.date.value)+".jpg")
            imgRef.putBytes(data!!)
                .addOnCompleteListener{
                    imgRef.downloadUrl.addOnCompleteListener {
                        viewModel.setPhoto(it.result!!)
                        Logger.i("FoodieFragment uploadFile =${it.result}")
                    }
                        .addOnFailureListener {
                            Logger.i("FoodieFragment uploadFile failed =$it")

                        }
                    Toast.makeText(App.applicationContext(),"Upload success", Toast.LENGTH_SHORT)
                }
                .addOnFailureListener {
                    Toast.makeText(App.applicationContext(),"Upload failed", Toast.LENGTH_SHORT)

                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/ taskSnapshot.totalByteCount
                    Toast.makeText(App.applicationContext(),"$progress uploaded...", Toast.LENGTH_SHORT)
                }
        }
    }

    private fun compress(image: Uri): ByteArray? {

        var imageStream: InputStream? = null
        try {
            imageStream = App.applicationContext().contentResolver.openInputStream(
                image
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }

        val bmp = BitmapFactory.decodeStream(imageStream)

        var stream: ByteArrayOutputStream? = ByteArrayOutputStream()
        //Qaulity was 35
        bmp.compress(Bitmap.CompressFormat.JPEG, 10, stream)
        val byteArray = stream!!.toByteArray()
        try {
            stream.close()
            stream = null
            return byteArray
        } catch (e: IOException) {

            e.printStackTrace()
        }

        return null
    }


    private fun getPermissions() {
        Logger.d( "getLocationPermission: getting location permissions");
        val permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )


        if (ContextCompat.checkSelfPermission(App.applicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(App.applicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (ContextCompat.checkSelfPermission(App.applicationContext(),
                        COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionsGranted = true;
                    try {
//                        fromcamera()
                    } catch (e: IOException) {
                        e.printStackTrace();
                    }
                }

            }
            else {
                ActivityCompat.requestPermissions(activity as MainActivity,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(activity as MainActivity,
                permissions,
                LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        mLocationPermissionsGranted = false;

        when (requestCode) {

            LOCATION_PERMISSION_REQUEST_CODE ->

                if (grantResults.size > 0) {
                    for (i in 0 until grantResults.size) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    Logger.d("onRequestPermissionsResult: permission granted")
                    mLocationPermissionsGranted = true;
                    //initialize our map
                    try {
                    } catch ( e: IOException) {
                        e.printStackTrace();
                    }
                }}

        }





    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout4.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }

}