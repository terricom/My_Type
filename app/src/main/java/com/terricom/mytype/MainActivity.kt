package com.terricom.mytype

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.terricom.mytype.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : BaseActivity(){

    val viewModel : MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private lateinit var binding: ActivityMainBinding
    var isFABOpen: Boolean = false
    val time = Calendar.getInstance().time

    //Declaring views
    private var buttonChoose: ImageButton? = null
    private var buttonUpload: Button? = null
    private var imageView: ImageView? = null

    //Image request code
    private val PICK_IMAGE_REQUEST = 1

    //Bitmap to get image from gallery
    private var bitmap: Bitmap? = null

    //Uri to store the image uri
    private var filePath: Uri? = null


    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_food_record -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToDiaryFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_diary -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToLinechartFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_line_chart -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToAchivementFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_harvest -> {
                findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    // get the height of status bar from system
    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return when {
                resourceId > 0 -> resources.getDimensionPixelSize(resourceId)
                else -> 0
            }
        }

    private val duration = 1000L
    private val await = 1500L

    @SuppressLint("ObsoleteSdkInt")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.bottom_nav_view)

        val fab = findViewById<View>(R.id.fab) as FloatingActionButton

        fab.setOnClickListener {
            if (!isFABOpen) {
                showFABMenu()
            } else {
                closeFABMenu()
            }
        }
        binding.fab1.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment())
        }

        binding.fab2.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment())
        }

        binding.fabLayout1.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToFoodieFragment())
        }
        binding.fabLayout2.setOnClickListener {
            findNavController(R.id.myNavHostFragment).navigate(NavigationDirections.navigateToShapeRecordFragment())
        }


        setupNavController()

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)


    }

    //UploadImg
//    fun uploadImg(){
//        //Requesting storage permission
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            requestStoragePermission()
//        }
//
//        //Initializing views
//        buttonChoose = findViewById(R.id.foodiephoto)
//        buttonUpload = findViewById(R.id.button_foodie_save)
//        imageView = findViewById(R.id.foodieUploadPhoto)
//
//        //Setting clicklistener
////        buttonChoose!!.setOnClickListener(this)
////        buttonUpload!!.setOnClickListener(this)
//    }


    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.foodieFragment -> CurrentFragmentType.FOODIE
                R.id.diaryFragment -> CurrentFragmentType.DIARY
                R.id.linechartFragment -> CurrentFragmentType.LINECHART
                R.id.achivementFragment -> CurrentFragmentType.HARVEST
                R.id.loginFragment -> CurrentFragmentType.LOGIN
                R.id.shaperecordFragment -> CurrentFragmentType.SHAPE_RECORD
                R.id.referenceDialog -> CurrentFragmentType.REF
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                else -> viewModel.currentFragmentType.value
            }
        }
        viewModel.currentFragmentType.observe(this, Observer {
            Log.i("Terri", "viewModel.currentFragmentType.observe = ${it.value}")
            binding.textToolbarTitle.text = it.value
            if (it.value == ""){
                hideBottomNavView()
                hideToolbar()
                hideFABView()
            }
            if (it.value == App.instance?.getString(R.string.title_foodie) || it.value == App.instance?.getString(R.string.title_shape_record) ){
                hideBottomNavView()
                hideFABView()
            }

        })
    }

    fun hideToolbar(){
        binding.toolbar.visibility = View.GONE
    }

    fun hideBottomNavView(){
        binding.bottomNavView.visibility = View.GONE
    }

    fun hideFABView(){
        binding.fab.visibility = View.GONE
        binding.fabLayout1.visibility = View.GONE
        binding.fabLayout2.visibility = View.GONE
        binding.fab1.visibility = View.GONE
        binding.fab2.visibility = View.GONE
    }


    private fun showFABMenu() {
        isFABOpen = true
        fabLayout1.animate().translationY(-resources.getDimension(R.dimen.standard_55))
        fabLayout2.animate().translationY(-resources.getDimension(R.dimen.standard_105))
        binding.fab1.visibility = View.VISIBLE
        binding.fab2.visibility = View.VISIBLE
        binding.fabLayout1.visibility = View.VISIBLE
        binding.fabLayout2.visibility = View.VISIBLE


    }

    private fun closeFABMenu() {
        isFABOpen = false
        fabLayout1.animate().translationY(resources.getDimension(R.dimen.standard_0))
        fabLayout2.animate().translationY(resources.getDimension(R.dimen.standard_0))

        Handler().postDelayed({
            binding.fabLayout1.visibility = View.INVISIBLE
            binding.fabLayout2.visibility = View.INVISIBLE }, 300)
    }

//    /*
//* This is the method responsible for image upload
//* We need the full image path and the name for the image in this method
//* */
//    fun uploadMultipart() {
//        //getting name for the image
//
//        //getting the actual path of the image
//        val path = getPath(filePath)
//
//        //Uploading code
//        try {
//            val uploadId = UUID.randomUUID().toString()
//
//            //Creating a multi part request
//            MultipartUploadRequest(this, uploadId, Constants.UPLOAD_URL)
//                .addFileToUpload(path, "image") //Adding file
//                .addParameter("name", Calendar.DATE.toString()) //Adding text parameter to the request
//                .setNotificationConfig(UploadNotificationConfig())
//                .setMaxRetries(2)
//                .startUpload() //Starting the upload
//
//        } catch (exc: Exception) {
//            Toast.makeText(this, exc.message, Toast.LENGTH_SHORT).show()
//        }
//
//    }
//
//
//    //method to show file chooser
//    private fun showFileChooser() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//    }
//
//    //handling the image chooser activity result
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
//            filePath = data.data
//            try {
//                bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
//                imageView!!.setImageBitmap(bitmap)
//
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//        }
//    }
//
//    //method to get the file path from uri
//    fun getPath(uri: Uri?): String {
//        var cursor = contentResolver.query(uri!!, null, null, null, null)
//        cursor!!.moveToFirst()
//        var document_id = cursor.getString(0)
//        document_id = document_id.substring(document_id.lastIndexOf(":") + 1)
//        cursor.close()
//
//        cursor = contentResolver.query(
//            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            null,
//            MediaStore.Images.Media._ID + " = ? ",
//            arrayOf(document_id),
//            null
//        )
//        cursor!!.moveToFirst()
//        val path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
//        cursor.close()
//
//        return path
//    }
//
//
//
//
//    //Requesting permission
//    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
//    private fun requestStoragePermission() {
//        if (ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            ) == PackageManager.PERMISSION_GRANTED
//        )
//            return
//
//        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
//            //If the user has denied the permission previously your code will come to this block
//            //Here you can explain why you need this permission
//            //Explain here why you need this permission
//        }
//        //And finally ask for the permission
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
//            STORAGE_PERMISSION_CODE
//        )
//    }
//
//    //This method will be called when the user will tap on allow or deny
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
//
//        //Checking the request code of our request
//        if (requestCode == STORAGE_PERMISSION_CODE) {
//
//            //If permission is granted
//            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Displaying a toast
//                Toast.makeText(this, "Permission granted now you can read the storage", Toast.LENGTH_LONG).show()
//            } else {
//                //Displaying another toast if permission is not granted
//                Toast.makeText(this, "Oops you just denied the permission", Toast.LENGTH_LONG).show()
//            }
//        }
//    }
//
//
//    override fun onClick(v: View) {
//        if (v === buttonChoose) {
//            showFileChooser()
//            buttonChoose!!.visibility = View.INVISIBLE
//            foodie_my_type.visibility = View.INVISIBLE
//            foodie_greet.visibility = View.INVISIBLE
//
//        }
//        if (v === buttonUpload) {
//            uploadMultipart()
//        }
//    }
//
//
//    companion object {
//
//        //storage permission code
//        private val STORAGE_PERMISSION_CODE = 123
//    }



}
