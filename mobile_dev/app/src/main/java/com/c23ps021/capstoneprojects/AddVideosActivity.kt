package com.c23ps021.capstoneprojects
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.telecom.Call
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.c23ps021.capstoneprojects.config.ApiConfig
import com.c23ps021.capstoneprojects.config.ApiResponse
import com.c23ps021.capstoneprojects.databinding.ActivityAddVideosBinding
import com.google.gson.Gson
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.Response
import java.io.File

class AddVideosActivity : AppCompatActivity() {
    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

    private lateinit var _binding: ActivityAddVideosBinding
    private lateinit var photoPath: String
    private var file: File? = null
    private var token: String? = null
    private lateinit var progressBar: View
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory(UserPreference.getInstance(dataStore))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.null_premission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_videos)

        _binding = ActivityAddVideosBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        if (!allPermissionsGranted()){
            ActivityCompat.requestPermissions(
                this@AddVideosActivity, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }

        mainViewModel.getUser().observe(this, {
            token = "Bearer ${it.token}"
        })

        _binding.apply {
            btnSelectVideo.setOnClickListener{
                val intent = Intent()
                intent.action = Intent.ACTION_GET_CONTENT
                intent.type = "image/*"
                val choose = Intent.createChooser(intent, getString(R.string.title_choose_a_picture))
                OpenGallery.launch(choose)
            }

            _binding.btnUploadVideo.setOnClickListener{
                UploadStory(token!!)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.camera, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.camera -> {
                startCamera()
                true
            }
            else -> true
        }
    }

    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddVideosActivity,
                "com.example.storyapp.myCamera",
                it
            )
            photoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            OpenCamera.launch(intent)
        }
    }


    private val OpenCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(photoPath)
            file = myFile

            val result = BitmapFactory.decodeFile(file?.path)
            _binding.previewImageView.setImageBitmap(result)
        }
    }

    private val OpenGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddVideosActivity)
            file = myFile
            _binding.previewImageView.setImageURI(selectedImg)
        }
    }

    private fun UploadStory(token: String){
        if (file !== null){
            val file = reduceFileImage(file as File)

            val description = _binding.description.text.toString().toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile,
            )
            showLoading(true, progressBar)
            val client = ApiConfig.ApiServices().uploadStory(token, imageMultipart, description)
            client.enqueue(object : Callback<ApiResponse> {
                override fun onResponse(call: Call<ApiResponse>, response: Response<ApiResponse>) {
                    if (response.isSuccessful){
                        showLoading(false, progressBar)
                        val responseBody = response.body()
                        if (responseBody != null){
                            if (!responseBody.error){
                                AlertDialog.Builder(this@AddVideosActivity).apply {
                                    setTitle(getString(R.string.title_alert_dialog))
                                    setMessage(getString(R.string.title_alert_dialog))
                                    setPositiveButton(getString(R.string.message_success_upload)) { _, _ ->
                                        val intent = Intent(context, MainActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    create()
                                    show()
                                }
                            }
                        }
                    }else{
                        val responseBody = Gson().fromJson(
                            response.errorBody()?.charStream(),
                            ApiResponse::class.java
                        )
                        showLoading(false, progressBar)
                        Toast.makeText(this@AddVideosActivity, response.message(), Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse>, t: Throwable) {
                    Toast.makeText(this@AddVideosActivity, t.message.toString(), Toast.LENGTH_SHORT).show()
                }
            })
        }else{
            Toast.makeText(this@AddVideosActivity, getString(R.string.message_failed_take_picture), Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoading(isLoading: Boolean, progressbar: View){
        if (isLoading){
            progressbar.visibility = View.VISIBLE
        }else{
            progressbar.visibility = View.GONE
        }
    }
}