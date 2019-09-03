package com.terricom.mytype.foodie

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentFoodieRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_foodie_record.*
import java.io.IOException
import java.io.InputStream
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


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFoodieRecordBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.setLifecycleOwner(this)

        binding.buttonFoodieShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToReferenceDialog())
        }

        binding.foodsRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.MyTouchListener())
        val foodList: MutableList<String> = mutableListOf("紅油抄手", "擔擔麵", "蘿蔔糕")
        (binding.foodsRecycler.adapter as FoodAdapter).submitList(foodList)
        (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()

        binding.nutritionRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.MyTouchListener())
        val nutritionList: MutableList<String> = mutableListOf("葉黃素", "氨基酸", "蠻牛")
        (binding.nutritionRecycler.adapter as NutritionAdapter).submitList(nutritionList)
        (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()


        binding.foodiephoto.setOnClickListener{
            //Requesting storage permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                requestStoragePermission()
            }
            showFileChooser()
            it.visibility = View.INVISIBLE
            binding.foodieMyType.visibility = View.INVISIBLE
            binding.foodieGreet.visibility = View.INVISIBLE
        }

        binding.buttonFoodieSave.setOnClickListener {
            uploadMultipart()
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
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


        binding.buttonFoodieSave.setOnClickListener {
            val inputDate = binding.editDate.text.toString()
            val inputTime = binding.editTime.text.toString()

            viewModel.date.value = inputDate
            viewModel.time.value = inputTime

            val dateArray: List<String> = inputDate.split(".")
            Logger.i("dateArray = ${dateArray[0]}")
//            var timestamp = Timestamp.valueOf("${dateArray[0]}-${dateArray[1]}-${dateArray[2]} ${inputTime}:00.000000000")

            viewModel.addFoodie()
        }


        return binding.root
    }


    fun uploadMultipart() {
        //getting name for the image

        //getting the actual path of the image

        val path = getPath(filePath)

        //Uploading code
//        try {
//            val uploadId = UUID.randomUUID().toString()
//
//            //Creating a multi part request
//            MultipartUploadRequest(App.applicationContext(), uploadId, Constants.UPLOAD_URL)
//                .addFileToUpload(path, "image") //Adding file
//                .addParameter("name", Calendar.DATE.toString()) //Adding text parameter to the request
//                .setNotificationConfig(UploadNotificationConfig())
//                .setMaxRetries(2)
//                .startUpload() //Starting the upload
//
//        } catch (exc: Exception) {
//            Toast.makeText(App.applicationContext(), exc.message, Toast.LENGTH_SHORT).show()
//        }


    }


    //method to show file chooser
    private fun showFileChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
    }

    //handling the image chooser activity result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            Logger.i("$this@FoodieFragment onActivityResult filePath =$filePath")
            uriToFilePath(App.applicationContext(), data.data)
            Logger.i("riToFilePath(App.applicationContext(), data.data) = ${uriToFilePath(App.applicationContext(), data.data)}")
            try {
                bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, filePath)
                foodieUploadPhoto.setImageBitmap(bitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }

    //method to get the file path from uri
    fun getPath(uri: Uri?): String {



        var path:String ?= null

        var cursor: Cursor? = App.applicationContext().contentResolver
            .query(uri!!, null, null, null, null) ?: return ""
        cursor!!.moveToFirst()
        var document_id = cursor.getString(0)
        Logger.i("document_id =$document_id")
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
        Logger.i("document_id 222=$document_id")

        cursor.close()

        cursor = App.applicationContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            MediaStore.Images.Media._ID + " = ? ",
            arrayOf(document_id),
            null
        )
        if (cursor != null ){
            cursor.moveToFirst()
            Logger.i("cursor.getColumnIndex(MediaStore.Images.Media.EXTERNAL_CONTENT_URI)= ${cursor.getColumnIndex(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())}")

            path = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
            cursor.close()
        }
        Logger.i("path= $path")



        return path as String

    }

    public fun uriToFilePath(context: Context,uri: Uri){
        var filePath: String?
        if (uri != null && "file".equals(uri.getScheme())) {
            filePath = uri.getPath();
        } else {
            filePath = filenameFromUri(context,uri)
        }
    }
    public fun filenameFromUri(context: Context,uri: Uri): String? {
        var filePath = getFilePathFromCursor(context, uri)
        if (TextUtils.isEmpty(filePath)) {
//            filePath = getFilePathFromInputStream(context, uri);
        }
        return filePath
    }

    private fun getFilePathFromCursor(context: Context, uri: Uri): String? {
        var filePath: String ?= null
        var cursor: Cursor ?= null
        try {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
            cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            Logger.i("getFilePathFromCursor columnIndex = $columnIndex")

            filePath = cursor.getString(columnIndex)
            Logger.i("getFilePathFromCursor filePath = $filePath filePathColumn =${filePathColumn[0]}")
            cursor.close()
        } catch ( e:Exception) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return filePath
    }

//    private fun getFilePathFromInputStream(context: Context, uri: Uri): String {
//        var filePath: String ?= ""
//        var inputStream: InputStream ?= null
//        try {
//            inputStream = context.getContentResolver().openInputStream(uri);
//            val bitmap: Bitmap  = BitmapFactory.decodeStream(inputStream, null, getBitMapOptions(context, uri));
//            inputStream.close();
//            filePath = saveImg(bitmap, FileUtil.getTempFileName())
//        } catch (e: Exception ) {
//            e.printStackTrace();
//        } finally {
//            if (inputStream != null) {
//                try {
//                    inputStream.close();
//                } catch (e: IOException ) {
//                    e.printStackTrace();
//                }
//            }
//        }
//        return filePath as String
//    }

    public fun getBitMapOptions(context: Context, uri: Uri): BitmapFactory.Options {

        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true;
        var stream: InputStream = context.getContentResolver().openInputStream(uri);
        BitmapFactory.decodeStream(stream, null, options);
        stream.close();
        var width = options.outWidth;
        var height = options.outHeight;
        if (width > height) {
            val temp = width;
            width = height;
            height = temp;
        }
        var sampleRatio = Math.max(width / 900, height / 1600);
//        options = BitmapFactory.Options();
        options.inSampleSize = sampleRatio;
        return options;
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


    companion object {

        //storage permission code
        private val STORAGE_PERMISSION_CODE = 123
    }

    override fun onStop() {
        super.onStop()
        (activity as MainActivity).fabLayout1.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout2.visibility = View.INVISIBLE
        (activity as MainActivity).fabLayout3.visibility = View.INVISIBLE
        (activity as MainActivity).isFABOpen = false

    }

}