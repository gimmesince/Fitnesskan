package com.c23ps021.capstoneprojects

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

class AddVideosActivity : AppCompatActivity() {
    private lateinit var btnSelectVideo: Button
    private lateinit var btnUploadVideo: Button
    private var selectedVideoUri: Uri? = null

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedVideoUri = it
            Toast.makeText(this, "Video selected: $it", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_videos)

        btnSelectVideo = findViewById(R.id.btn_select_video)
        btnUploadVideo = findViewById(R.id.btn_upload_video)

        btnSelectVideo.setOnClickListener {
            selectVideoFromGallery()
        }

        btnUploadVideo.setOnClickListener {
            if (selectedVideoUri != null) {
                uploadVideo(selectedVideoUri!!)
            } else {
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectVideoFromGallery() {
        getContent.launch("video/*")
    }

    private fun uploadVideo(videoUri: Uri) {
        val file = videoUri.toFile()

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("video", file.name, file.asRequestBody("video/*".toMediaTypeOrNull()))
            .build()

        val request = Request.Builder()
            .url("https://backend-upload-dot-c23-ps021-387009.et.r.appspot.com")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddVideosActivity, "Upload successful", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@AddVideosActivity, "Upload failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error uploading video", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@AddVideosActivity, "Error uploading video", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val TAG = "AddVideosActivity"
    }
}
