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
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.terricom.mytype.*
import com.terricom.mytype.calendar.SpaceItemDecoration
import com.terricom.mytype.databinding.FragmentFoodieRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_foodie_record.*
import java.io.*
import java.text.SimpleDateFormat
import java.util.*



class FoodieFragment: Fragment() {

    val time = Calendar.getInstance().time

    private val viewModel: FoodieViewModel by lazy {
        ViewModelProviders.of(this).get(FoodieViewModel::class.java)
    }

    //Image request code
    private val PICK_IMAGE_REQUEST = 1

    //Bitmap to get image from gallery
    private var bitmap: Bitmap? = null

    //Uri to store the image uri
    private var filePath: Uri? = null

    private var storageReference: StorageReference ?= null
    private var auth: FirebaseAuth ?= null

    private var filePathProvider: FileProvider ?= null

    private var mPhone: DisplayMetrics?= null

    private var windowManager: WindowManager? = null

    private lateinit var binding: FragmentFoodieRecordBinding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val foodie = FoodieFragmentArgs.fromBundle(arguments!!).selectedProperty

        binding = FragmentFoodieRecordBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        //處理從其他 Fragment 帶 Argument 過來的情況
        binding.foodie = foodie
        if (foodie.timestamp != null){
            viewModel.setDate(foodie.timestamp!!)
            viewModel.water.value = foodie.water
            viewModel.fruit.value = foodie.fruit
            viewModel.protein.value = foodie.protein
            viewModel.vegetable.value = foodie.vegetable
            viewModel.oil.value = foodie.oil
            viewModel.carbon.value = foodie.carbon
            if (foodie.foods!!.isNotEmpty()){
                binding.dagFoodHint.visibility = View.GONE
                binding.foodsTransportedRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.LongClickListener())
                (binding.foodsTransportedRecycler.adapter as FoodAdapter).submitList(foodie.foods)
                binding.foodsTransportedRecycler.addItemDecoration(
                    SpaceItemDecoration(
                        resources.getDimension(R.dimen.recyclerview_between).toInt(),
                        true
                    )
                )
                val owner = binding.foodsTransportedRecycler.parent as ViewGroup
                owner.removeView(binding.foodsTransportedRecycler)
                binding.chosedFood.addView(binding.foodsTransportedRecycler)
            }
        }

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        binding.buttonFoodieShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToReferenceDialog())
        }

        binding.foodsRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.LongClickListener())
        viewModel.userFoodList.observe(this, androidx.lifecycle.Observer {
            val newList = it.toMutableList()
            newList.add("新增食物")
            Logger.i("Foodie Fragment newList =$newList")
            (binding.foodsRecycler.adapter as FoodAdapter).submitList(newList)
            binding.foodsRecycler.addItemDecoration(
                SpaceItemDecoration(
                    resources.getDimension(R.dimen.recyclerview_between).toInt(),
                    true
                )
            )
            (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()
        })

        binding.nutritionRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.LongClickListenerNu())
        viewModel.userNuList.observe(this, androidx.lifecycle.Observer {
            (binding.nutritionRecycler.adapter as NutritionAdapter).submitList(it)
            binding.nutritionRecycler.addItemDecoration(
                SpaceItemDecoration(
                    resources.getDimension(R.dimen.recyclerview_between).toInt(),
                    true
                )
            )
            (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()
        })

        binding.foodiePhoto.setOnClickListener{
            Logger.i("Clicked foodiephoto")
            //Requesting storage permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                requestStoragePermission()
            }
            showFileChooser()
            Logger.i("showFileChooser() in foodiephoto")
        }
//        binding.uploadIcon.setOnClickListener{
//            Logger.i("Clicked uploadIcon")
//            viewModel.addPhoto()
//            //Requesting storage permission
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                requestStoragePermission()
//            }
//            showFileChooser()
//            Logger.i("showFileChooser() in uploadIcon")
//
//        }

//        binding.buttonAddFood.setOnClickListener {
//            binding.dagFoodHint.visibility = View.GONE
//            if (viewModel.addFood.value != null){
//                viewModel.newFuList.add("${viewModel.addFood.value}")
//            (binding.foodsRecycler.adapter as FoodAdapter).submitList(listOf(viewModel.addFood.value))
//            (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()
//            viewModel.addFood.value = ""
//            }
//        }
//
//        binding.buttonAddNutrition.setOnClickListener {
//            binding.dragNutritionHint.visibility = View.GONE
//            if (viewModel.addNutrition.value != null){
//                viewModel.newNuList.add("${viewModel.addNutrition.value}")
//                (binding.nutritionRecycler.adapter as NutritionAdapter).submitList(listOf(viewModel.addNutrition.value))
//                (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()
//                viewModel.addNutrition.value = ""
//            }
//
//        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)



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
                        binding.dagFoodHint.visibility = View.INVISIBLE
                        viewModel.dragToList("${view.findViewById<TextView>(R.id.food).text}")
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
                        binding.dragNutritionHint.visibility = View.INVISIBLE
                        viewModel.dragToListNu("${view.findViewById<TextView>(R.id.nutrition).text}")
                    }
                    else -> {
                    }
                }// do nothing
                return true
            }
        }

        binding.chosedFood.setOnDragListener(MyDragListener())
        binding.chosedNutrition.setOnDragListener(MyDragListenerNu())

        //讀取手機解析度
        mPhone = DisplayMetrics()
        getWindowManager(App.applicationContext()).getDefaultDisplay().getMetrics(mPhone)


        binding.buttonFoodieSave.setOnClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm")

            val user: FirebaseUser = auth!!.currentUser as FirebaseUser
            val userId = user.uid
            val name = sdf.format(Date().time)

//            uploadFile()
            viewModel.addFoodie()
            viewModel.updateFoodAndNuList()
            viewModel.clearData()

            findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
            (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_diary
            (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
            (activity as MainActivity).fab.visibility = View.VISIBLE

            if (isConnected()) {
                Logger.i("NetworkConnection Network Connected.")
                //執行下載任務
            }else{
                Toast.makeText(App.applicationContext(),resources.getText(R.string.network_check), Toast.LENGTH_SHORT)
                //告訴使用者網路無法使用
            }


        }

        viewModel.photoUri.observe(this, androidx.lifecycle.Observer {
            Logger.i("viewModel.photoUri.observe =$it")
        })

        filePathProvider = FileProvider()


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

    //計算圖片的縮放值
    fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val heightRatio = Math.round(height.toFloat() / reqHeight.toFloat())
            val widthRatio = Math.round(width.toFloat() / reqWidth.toFloat())
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        return inSampleSize
    }


    private fun isConnected(): Boolean{
        val connectivityManager = App.applicationContext().getSystemService(Context.CONNECTIVITY_SERVICE)
        return if (connectivityManager is ConnectivityManager) {
            val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo
            networkInfo?.isConnected ?: false
        } else false
    }



    companion object {


        //storage permission code
        private val STORAGE_PERMISSION_CODE = 123

        /** Calculate inSampleSize to fit within requestSize */
        fun calculateInSampleSize(context: Context, uri: Uri, requestSize: Int): Int {
            var stream: InputStream? = null
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            return try {
                stream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(stream, null, options)
                val longer = Math.max(options.outWidth, options.outHeight)
                if (longer > requestSize) {
                    longer / requestSize
                } else {
                    1
                }
            } catch (e: Exception) {
                1
            } finally {
                stream?.close()
            }
        }

        /** Acquire image rotation from Uri */
        fun getImageRotation(context: Context, uri: Uri): Int {
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

        /** Retrieve the reduced image below requestSize */
        fun createReducedBitmap(context: Context, uri: Uri, requestSize: Int): Bitmap? {
            var stream: InputStream? = null
            val options = BitmapFactory.Options().apply {
                inSampleSize = calculateInSampleSize(context, uri, requestSize)
            }
            return try {
                stream = context.contentResolver.openInputStream(uri)
                BitmapFactory.decodeStream(stream, null, options)
            } catch (e: Exception) {
                null
            } finally {
                stream?.close()
            }
        }

        /** Save images to a file */
        fun saveBitmapToFile(bitmap: Bitmap, file: File): Boolean {
            var stream: OutputStream? = null
            return try {
                stream = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            } catch (e: Exception) {
                false
            } finally {
                stream?.close()
            }
        }

        /** Reset file Orientation tag */
        fun resetOrientation(filePath: String): Boolean {
            return try {
                val exifInterface = ExifInterface(filePath)
                exifInterface.setAttribute(ExifInterface.TAG_ORIENTATION, "0")
                true
            } catch (e: Exception) {
                false
            }
        }
    }



    private fun uploadFile(){
        if (filePath != null){
            auth = FirebaseAuth.getInstance()
            val userId = auth!!.currentUser!!.uid
            val sdf = SimpleDateFormat("yyyy-MM-dd-hhmmss")
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


    //Requesting permission
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(
                App.applicationContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        )
            return

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity as MainActivity, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }
        //And finally ask for the permission
        ActivityCompat.requestPermissions(
            activity as MainActivity,
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            STORAGE_PERMISSION_CODE
        )
    }

    //This method will be called when the user will tap on allow or deny
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        //Checking the request code of our request
        if (requestCode == STORAGE_PERMISSION_CODE) {

            //If permission is granted
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Displaying a toast
                Toast.makeText(App.applicationContext(), "Permission granted now you can read the storage", Toast.LENGTH_LONG).show()
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(App.applicationContext(), "Oops you just denied the permission", Toast.LENGTH_LONG).show()
            }
        }
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