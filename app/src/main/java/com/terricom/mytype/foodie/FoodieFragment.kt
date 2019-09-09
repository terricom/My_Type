package com.terricom.mytype.foodie

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.terricom.mytype.*
import com.terricom.mytype.databinding.FragmentFoodieRecordBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_foodie_record.*
import java.io.IOException
import java.io.InputStream
import java.sql.Timestamp
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

    private var imgDownloadUri: Uri ?= null



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentFoodieRecordBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        auth = FirebaseAuth.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        binding.buttonFoodieShowInfo.setOnClickListener {
            findNavController().navigate(NavigationDirections.navigateToReferenceDialog())
        }

        binding.foodsRecycler.adapter = FoodAdapter(viewModel, FoodAdapter.LongClickListener())
        viewModel.userFoodList.observe(this, androidx.lifecycle.Observer {
            (binding.foodsRecycler.adapter as FoodAdapter).submitList(it)
            (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()
        })

        binding.nutritionRecycler.adapter = NutritionAdapter(viewModel, NutritionAdapter.LongClickListenerNu())
        viewModel.userNuList.observe(this, androidx.lifecycle.Observer {
            (binding.nutritionRecycler.adapter as NutritionAdapter).submitList(it)
            (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()
        })

        binding.foodiephoto.setOnClickListener{
            //Requesting storage permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                requestStoragePermission()
            }
            showFileChooser()
            it.visibility = View.GONE
            binding.foodieMyType.visibility = View.INVISIBLE
            binding.foodieGreet.visibility = View.INVISIBLE
        }

        binding.buttonAddFood.setOnClickListener {
            binding.dagFoodHint.visibility = View.GONE
            if (viewModel.addFood.value != null){
                viewModel.newFuList.add("${viewModel.addFood.value}")
            (binding.foodsRecycler.adapter as FoodAdapter).submitList(listOf(viewModel.addFood.value))
            (binding.foodsRecycler.adapter as FoodAdapter).notifyDataSetChanged()
            viewModel.addFood.value = ""
            }
        }

        binding.buttonAddNutrition.setOnClickListener {
            binding.dragNutritionHint.visibility = View.GONE
            if (viewModel.addNutrition.value != null){
                viewModel.newNuList.add("${viewModel.addNutrition.value}")
                (binding.nutritionRecycler.adapter as NutritionAdapter).submitList(listOf(viewModel.addNutrition.value))
                (binding.nutritionRecycler.adapter as NutritionAdapter).notifyDataSetChanged()
                viewModel.addNutrition.value = ""
            }

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



        binding.buttonFoodieSave.setOnClickListener {
            val sdf = SimpleDateFormat("yyyy-MM-dd hh:mm")

            val user: FirebaseUser = auth!!.currentUser as FirebaseUser
            val userId = user.uid
            val name = sdf.format(Date().time)

            uploadFile()


            val handler = Handler()

            handler.postDelayed({
                viewModel.addFoodie()
                viewModel.updateFoodAndNuList()
                viewModel.clearData()

                findNavController().navigate(NavigationDirections.navigateToDiaryFragment())
                (activity as MainActivity).bottom_nav_view.selectedItemId = R.id.navigation_food_record
                (activity as MainActivity).bottom_nav_view!!.visibility = View.VISIBLE
                (activity as MainActivity).fab.visibility = View.VISIBLE
            }, 3000)



        }

        viewModel.photoUri.observe(this, androidx.lifecycle.Observer {
            Logger.i("viewModel.photoUri.observe =$it")
        })


        return binding.root
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
            Logger.i("@FoodieFragment onActivityResult filePath =$filePath")
            uriToFilePath(App.applicationContext(), data.data)
            try {
                val matrix = Matrix()

                bitmap = MediaStore.Images.Media.getBitmap((activity as MainActivity).contentResolver, filePath)
                val rotatedBitmap: Bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap!!.getWidth(), bitmap!!.getHeight(), matrix, true);

                foodieUploadPhoto.setImageBitmap(rotatedBitmap)

            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }



    private fun uploadFile(){
        if (filePath != null){
            auth = FirebaseAuth.getInstance()
            val userId = auth!!.currentUser!!.uid
            val sdf = SimpleDateFormat("yyyy-MM-dd-hhmmss")
            val imgRef = storageReference!!.child("images/users/"+ userId+"/"
                    +sdf.format(Date(Timestamp.valueOf("${viewModel.date.value?.replace(".","-")} ${viewModel.time.value}:00.000000000").time))+".jpg")
            imgRef.putFile(filePath!!)
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


    fun uriToFilePath(context: Context,uri: Uri){
        var filePath: String?
        if (uri != null && "file".equals(uri.scheme)) {
            Logger.i("uriToFilePath not null")
            filePath = uri.path
        } else {
            filePath = filenameFromUri(context,uri)
        }
    }

    fun filenameFromUri(context: Context,uri: Uri): String? {
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
            cursor = context.contentResolver.query(uri, filePathColumn, null, null, null)
            cursor.moveToFirst()
            val columnIndex = cursor.getColumnIndex(filePathColumn[0])
            Logger.i("getFilePathFromCursor columnIndex = $columnIndex")

            filePath = cursor.getString(columnIndex)
            Logger.i("getFilePathFromCursor filePath = $filePath filePathColumn =${filePathColumn[0]}")
            cursor.close()
        } catch ( e:Exception) {
            e.printStackTrace()
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

    fun getBitMapOptions(context: Context, uri: Uri): BitmapFactory.Options {

        var options: BitmapFactory.Options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var stream: InputStream = context.contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(stream, null, options)
        stream.close()
        var width = options.outWidth
        var height = options.outHeight
        if (width > height) {
            val temp = width
            width = height
            height = temp
        }
        var sampleRatio = Math.max(width / 900, height / 1600)
        options = BitmapFactory.Options()
        options.inSampleSize = sampleRatio
        return options
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