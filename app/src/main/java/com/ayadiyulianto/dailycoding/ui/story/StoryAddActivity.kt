package com.ayadiyulianto.dailycoding.ui.story

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.ayadiyulianto.dailycoding.R
import com.ayadiyulianto.dailycoding.data.Result
import com.ayadiyulianto.dailycoding.databinding.ActivityStoryAddBinding
import com.ayadiyulianto.dailycoding.ui.ViewModelFactory
import com.ayadiyulianto.dailycoding.ui.auth.AuthViewModel
import com.ayadiyulianto.dailycoding.ui.auth.LoginActivity
import com.ayadiyulianto.dailycoding.utils.reduceFileImage
import com.ayadiyulianto.dailycoding.utils.rotateFile
import com.ayadiyulianto.dailycoding.utils.uriToFile
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

class StoryAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStoryAddBinding

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private var getFile: File? = null

    private val authViewModel: AuthViewModel by viewModels {
        ViewModelFactory(this)
    }

    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        authViewModel.userToken.observe(this) { token ->
            if (token.isEmpty()) gotoLogin()

            storyViewModel.setToken(token)
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnCamera.setOnClickListener { startCameraX() }
        binding.btnGalery.setOnClickListener { startGallery() }
        binding.btnUpload.setOnClickListener { uploadStory() }
        binding.switchLocation.setOnClickListener { getMyLastLocation() }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyLastLocation()
                }
                permissions[Manifest.permission.CAMERA] ?: false -> {

                }
                else -> {
                    Toast.makeText(
                        this,
                        getString(R.string.permissions_not_granted),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }
            }
        }

    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storyViewModel.setLocation(location)
                } else {
                    Toast.makeText(
                        this,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startCameraX() {
        if (checkPermission(Manifest.permission.CAMERA)) {
            val intent = Intent(this, CameraActivity::class.java)
            launcherIntentCameraX.launch(intent)
        } else {
            requestPermissionLauncher.launch(arrayOf(Manifest.permission.CAMERA))
        }
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == CAMERA_X_RESULT) {
            val myFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.data?.getSerializableExtra("picture", File::class.java)
            } else {
                @Suppress("DEPRECATION")
                it.data?.getSerializableExtra("picture")
            } as? File

            val isBackCamera = it.data?.getBooleanExtra("isBackCamera", true) as Boolean

            myFile?.let { file ->
                rotateFile(file, isBackCamera)
                getFile = file
                binding.imgPreview.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri

            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this)
                getFile = myFile
                binding.imgPreview.setImageURI(uri)
            }
        }
    }

    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)
            val description = binding.edtDescription.text.toString()
            val allowLocation = binding.switchLocation.isChecked
            storyViewModel.uploadStory(file, description, allowLocation).observe(this) { result ->
                if (result != null) {
                    when (result) {
                        is Result.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }

                        is Result.Success -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                result.data,
                                Toast.LENGTH_SHORT
                            ).show()
                            // Go to Main
                            gotoMain()
                        }

                        is Result.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(
                                this,
                                getString(R.string.error_message, result.error),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.please_add_photo), Toast.LENGTH_SHORT).show()
        }
    }

    private fun gotoMain() {
        Toast.makeText(this, getString(R.string.upload_success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun gotoLogin() {
        Toast.makeText(this, getString(R.string.logout_success), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    companion object {
        const val CAMERA_X_RESULT = 200
    }
}