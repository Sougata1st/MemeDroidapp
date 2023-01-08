package com.example.myapplication

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.myapplication.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isRecordPermissionGranted = false
    private var isWritePermissionGranted = false

    var x=false
    val url = "https://meme-api.com/gimme"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        memecaller()

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){ permissions ->


            isRecordPermissionGranted = permissions[Manifest.permission.RECORD_AUDIO] ?: isRecordPermissionGranted
            isWritePermissionGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] ?: isWritePermissionGranted

        }
        requestPermission()

    }
    fun memecaller(){
        binding.progressbar.visibility=View.VISIBLE
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("sougata","the responce is ${response.getString("url")}")
                Glide.with(this)
                    .load(response.getString("url")).listener(object : RequestListener<Drawable>{

                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressbar.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            binding.progressbar.visibility = View.GONE
                            binding.nextmemebutton.isEnabled = true
                            binding.sharememebutton.isEnabled = true
                            return false
                        }
                    })
                    .into(binding.imageView);
                binding.progressbar.visibility=View.GONE
                 x=true
            },
            { error ->
                x=false
                binding.progressbar.visibility=View.GONE
                Toast.makeText(applicationContext,"Something Went Wrong !!",Toast.LENGTH_SHORT).show()
                Log.d("sougata","something went wrong")
            }
        )


       MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }


    fun shareMeme() {


            val intent = Intent(Intent.ACTION_SEND).setType("image/*")


            val bitmap = binding.imageView.drawable.toBitmap() // your imageView here.


            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)


            val path =
                MediaStore.Images.Media.insertImage(contentResolver, bitmap, "tempimage", null)


            val uri = Uri.parse(path)


            intent.putExtra(Intent.EXTRA_TEXT, "meme shared with MemeDroid made by Sougata Kar")
            intent.putExtra(Intent.EXTRA_STREAM, uri)


            startActivity(intent)



    }

    fun nextmeme(view: View) {
    memecaller()
    }
    private fun requestPermission(){

        isRecordPermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        isWritePermissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        val permissionRequest : MutableList<String> = ArrayList()


        if (!isRecordPermissionGranted){

            permissionRequest.add(Manifest.permission.RECORD_AUDIO)

        }
        if (!isWritePermissionGranted){

            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        }
        if (permissionRequest.isNotEmpty()){

            permissionLauncher.launch(permissionRequest.toTypedArray())
        }

    }

    fun sharememe(view: View) {
        shareMeme()
    }


}