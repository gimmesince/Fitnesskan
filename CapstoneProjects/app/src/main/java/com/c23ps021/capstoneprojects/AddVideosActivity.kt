package com.c23ps021.capstoneprojects

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.result.contract.ActivityResultContracts
import com.c23ps021.capstoneprojects.databinding.ActivityAddVideosBinding
import com.c23ps021.capstoneprojects.databinding.ActivityLanding4Binding
import com.google.android.exoplayer2.ui.PlayerView

class AddVideosActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddVideosBinding
    private lateinit var btnSelectVideo: Button
    private lateinit var btnUploadVideo: Button
    private lateinit var videoView: VideoView
    private lateinit var playerView: PlayerView
    private var selectedVideoUri: Uri? = null

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                selectVideoFromGallery()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

    private val getContent =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedVideoUri = it
                videoView.setVideoURI(selectedVideoUri)
                videoView.start()
                playerView.player = null // Remove the ExoPlayer from PlayerView if video is played using VideoView
                Toast.makeText(this, "Video selected: $it", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddVideosBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_add_videos)
        setContentView(binding.root)
        btnSelectVideo = findViewById(R.id.btn_select_video)
        btnUploadVideo = findViewById(R.id.btn_upload_video)
        videoView = findViewById(R.id.video_view)
        playerView = findViewById(R.id.player_view)

        btnSelectVideo.setOnClickListener {
            checkPermissionsAndSelectVideo()
        }

        btnUploadVideo.setOnClickListener {
            if (selectedVideoUri != null) {
                uploadVideo(selectedVideoUri!!)
            } else {
                Toast.makeText(this, "No video selected", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBack.setOnClickListener {
            val intent = Intent(this, home_page::class.java)
            startActivity(intent)
        }

        videoView.setOnPreparedListener { mediaPlayer ->
            val mediaController = MediaController(this)
            videoView.setMediaController(mediaController)
            mediaController.setAnchorView(videoView)
            mediaPlayer.setOnVideoSizeChangedListener { _, _, _ ->
                mediaController.setAnchorView(videoView)
            }
        }
    }

    private fun checkPermissionsAndSelectVideo() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            selectVideoFromGallery()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    private fun selectVideoFromGallery() {
        getContent.launch("video/*")
    }

    private fun uploadVideo(videoUri: Uri) {
        val videoPath = "android.resource://${packageName}/${R.raw.model4}"
        videoView.setVideoURI(Uri.parse(videoPath))
        videoView.start()
    }

}
