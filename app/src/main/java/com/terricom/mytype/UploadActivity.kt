package com.terricom.mytype

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*



class UploadActivity: AppCompatActivity() {

    //Image request code
    private val PICK_IMAGE_REQUEST = 1

    //storage permission code
    private val STORAGE_PERMISSION_CODE = 123

    //Bitmap to get image from gallery
    private val bitmap: Bitmap? = null

    //Uri to store the image uri
    private val filePath: Uri? = null
//    private val webview_url =
//        "file:///android_res/raw/index.html"    // web address or local file location you want to open in webview
    private val file_type = "image/*"    // file types to be allowed for upload
    private val multiple_files = false         // allowing multiple file upload

//    var webView: WebView? = null

    private var cam_file_data: String? = null        // for storing camera file information
    private var file_data: ValueCallback<Uri>? = null       // data/header received after file selection
    private var file_path: ValueCallback<Array<Uri>>? = null     // received file(s) temp. location

    private val file_req_code = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (Build.VERSION.SDK_INT >= 21) {
            var results: Array<Uri> ?= null

            /*-- if file request cancelled; exited camera. we need to send null value to make future attempts workable --*/
            if (resultCode == Activity.RESULT_CANCELED) {
                if (requestCode == file_req_code) {
                    file_path?.onReceiveValue(null)
                    return
                }
            }

            /*-- continue if response is positive --*/
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == file_req_code) {
                    if (null == file_path) {
                        return
                    }
                    if (null == intent!!.clipData && null == intent.dataString && null != cam_file_data) {
                        results = arrayOf(Uri.parse(cam_file_data))
                    } else {
                        if (null != intent.clipData && results != null) { // checking if multiple files selected or not
                            val numSelectedFiles = intent.clipData.itemCount
                            results = arrayOfNulls<Uri>(numSelectedFiles) as Array<Uri>
                            for (i in 0 until intent.clipData!!.itemCount) {
                                results[i] = intent.clipData!!.getItemAt(i).uri
                            }
                        } else {
                            results = arrayOf(Uri.parse(intent.dataString))
                        }
                    }
                }
            }
            file_path?.onReceiveValue(results)
            file_path = null
        } else {
            if (requestCode == file_req_code) {
                if (null == file_data) return
                val result = if (intent == null || resultCode != Activity.RESULT_OK) null else intent.data
                file_data!!.onReceiveValue(result)
                file_data = null
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        webView = findViewById<WebView>(R.id.os_view)
//        assert(webView != null)
//        val webSettings = webView?.getSettings()
//        webSettings?.javaScriptEnabled = true
//        webSettings?.allowFileAccess = true

//        if (Build.VERSION.SDK_INT >= 21) {
//            webSettings?.mixedContentMode = 0
//            webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
//        } else if (Build.VERSION.SDK_INT >= 19) {
//            webView?.setLayerType(View.LAYER_TYPE_HARDWARE, null)
//        } else {
//            webView?.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//        }
//        webView?.setWebViewClient(Callback())
//        webView?.loadUrl(webview_url)
//        webView?.webChromeClient = object : WebChromeClient() {

            /*-- openFileChooser is not a public Android API and has never been part of the SDK. --*/

            /*-- handling input[type="file"] requests for android API 16+ --*/
            fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
                file_data = uploadMsg
                val i = Intent(Intent.ACTION_GET_CONTENT)
                i.addCategory(Intent.CATEGORY_OPENABLE)
                i.type = file_type
                if (multiple_files && Build.VERSION.SDK_INT >= 18) {
                    i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                }
                startActivityForResult(Intent.createChooser(i, "File Chooser"), file_req_code)
            }

            /*-- handling input[type="file"] requests for android API 21+ --*/
            fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams
            ): Boolean {
                if (file_permission() && Build.VERSION.SDK_INT >= 21) {
                    file_path = filePathCallback
                    var takePictureIntent: Intent? = null
                    var takeVideoIntent: Intent? = null

                    var includeVideo = false
                    var includePhoto = false

                    /*-- checking the accept parameter to determine which intent(s) to include --*/
                    paramCheck@ for (acceptTypes in fileChooserParams.acceptTypes) {
                        val splitTypes = acceptTypes.split(", ?+".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray() // although it's an array, it still seems to be the whole value; split it out into chunks so that we can detect multiple values
                        for (acceptType in splitTypes) {
                            when (acceptType) {
                                "*/*" -> {
                                    includePhoto = true
                                    includeVideo = true
                                    break@paramCheck
                                }
                                "image/*" -> includePhoto = true
                            }
                        }
                    }

                    if (fileChooserParams.acceptTypes.size == 0) {   //no `accept` parameter was specified, allow both photo and video
                        includePhoto = true
                        includeVideo = true
                    }

                    if (includePhoto) {
                        takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent.resolveActivity(this@UploadActivity.getPackageManager()) != null) {
                            var photoFile: File? = null
                            try {
                                photoFile = create_image()
                                takePictureIntent.putExtra("PhotoPath", cam_file_data)
                            } catch (ex: IOException) {
                                Logger.e( "Image file creation failed")
                            }

                            if (photoFile != null) {
                                cam_file_data = "file:" + photoFile!!.getAbsolutePath()
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile))
                            } else {
                                takePictureIntent = null
                            }
                        }
                    }


                    val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                    contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE)
                    contentSelectionIntent.type = file_type
                    if (multiple_files) {
                        contentSelectionIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                    }

                    val intentArray: Array<Intent?>
                    if (takePictureIntent != null && takeVideoIntent != null) {
                        intentArray = arrayOf(takePictureIntent, takeVideoIntent)
                    } else if (takePictureIntent != null) {
                        intentArray = arrayOf(takePictureIntent)
                    } else if (takeVideoIntent != null) {
                        intentArray = arrayOf(takeVideoIntent)
                    } else {
                        intentArray = arrayOfNulls(0)
                    }

                    val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                    chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                    chooserIntent.putExtra(Intent.EXTRA_TITLE, "File chooser")
                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                    startActivityForResult(chooserIntent, file_req_code)
                    return true
                } else {
                    return false
                }
            }
        }




    /*-- checking and asking for required file permissions --*/
    fun file_permission(): Boolean {
        if (Build.VERSION.SDK_INT >= 23 && (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                this@UploadActivity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA),
                1
            )
            return false
        } else {
            return true
        }
    }

    @Throws(IOException::class)
    private fun create_image(): File {
        @SuppressLint("SimpleDateFormat") val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "img_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(imageFileName, ".jpg", storageDir)
    }

//    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (event.getAction() === KeyEvent.ACTION_DOWN) {
//            if (keyCode == KeyEvent.KEYCODE_BACK) {
//                if (webView?.canGoBack()!!) {
//                    webView?.goBack()
//                } else {
//                    finish()
//                }
//                return true
//            }
//        }
//        return super.onKeyDown(keyCode, event)
//    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
    }


}