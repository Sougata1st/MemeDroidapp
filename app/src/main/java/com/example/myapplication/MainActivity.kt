package com.example.myapplication

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.core.graphics.drawable.toBitmap
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.example.myapplication.databinding.ActivityMainBinding
import java.io.ByteArrayOutputStream

class MainActivity : AppCompatActivity() {
    var x=false
    val url = "https://meme-api.com/gimme"
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding=ActivityMainBinding.inflate(LayoutInflater.from(this))
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
memecaller()
    }
    fun memecaller(){
        binding.progressbar.visibility=View.VISIBLE
        val queue = Volley.newRequestQueue(this)
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response ->
                Log.d("sougata","the responce is ${response.getString("url")}")
                Glide.with(this)
                    .load(response.getString("url"))
                    .into(binding.imageView);
                binding.progressbar.visibility=View.GONE
                 x=true
            },
            { error ->
                x=false
                Log.d("sougata","something went wrong")
            }
        )

// Add the request to the RequestQueue.
        queue.add(jsonObjectRequest)
    }
    fun sharememe(view: View) {
        if(x) {

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


    }
    fun nextmeme(view: View) {
    memecaller()
    }
}