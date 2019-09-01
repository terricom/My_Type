//package com.terricom.mytype
//
//import android.Manifest
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Intent
//import android.content.pm.PackageManager
//import android.graphics.Bitmap
//import android.net.Uri
//import android.os.Build
//import android.os.Bundle
//import android.provider.MediaStore
//import android.view.View
//import android.widget.Button
//import android.widget.ImageButton
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.annotation.RequiresApi
//import androidx.core.app.ActivityCompat
//import androidx.core.content.ContextCompat
//import androidx.lifecycle.ViewModelProviders
//import com.terricom.mytype.uploadimg.Constants
//import kotlinx.android.synthetic.main.fragment_foodie_record.*
//import net.gotev.uploadservice.MultipartUploadRequest
//import net.gotev.uploadservice.UploadNotificationConfig
//import java.io.IOException
//import java.util.*
//
//
//class UploadActivity: BaseActivity(), View.OnClickListener {
//
//    val viewModel : MainViewModel by lazy {
//        ViewModelProviders.of(this).get(MainViewModel::class.java)
//    }
//
//    //Declaring views
//    private var buttonChoose: ImageButton? = null
//    private var buttonUpload: Button? = null
//    private var imageView: ImageView? = null
//
//    //Image request code
//    private val PICK_IMAGE_REQUEST = 1
//
//    //Bitmap to get image from gallery
//    private var bitmap: Bitmap? = null
//
//    //Uri to store the image uri
//    private var filePath: Uri? = null
//
//    @SuppressLint("ObsoleteSdkInt")
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.fragment_foodie_record)
//
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
//        buttonChoose!!.setOnClickListener(this)
//        buttonUpload!!.setOnClickListener(this)
//
//    }
//
//
//    /*
//    * This is the method responsible for image upload
//    * We need the full image path and the name for the image in this method
//    * */
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
//    companion object {
//
//        //storage permission code
//        private val STORAGE_PERMISSION_CODE = 123
//    }
//
//
//}