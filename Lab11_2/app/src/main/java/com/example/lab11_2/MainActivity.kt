package com.example.lab11_2

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    // 1. Create a launcher for the permission request.
    // This handles the result of the permission dialog.
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. You can now launch the camera.
                // You might want to call your openCamera() function here again.
                openCamera() // Or whatever function launches the camera intent
            } else {
                // Permission denied.
                // You should show a message to the user explaining why you need the camera.
                // For example, using a Toast or a Snack bar.
                Toast.makeText(this, "Camera permission is required to take pictures.", Toast.LENGTH_SHORT).show()
            }
        }

    // 當前的圖片旋轉角度
    private var angle = 0f

    // 宣告 ActivityResultLauncher，取得回傳的照片
    private val startForResult = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            findViewById<ImageView>(R.id.imgPhoto).setImageBitmap(bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val btnCamera = findViewById<Button>(R.id.btnCapture)
        btnCamera.setOnClickListener {
            // 2. This is where you check for the permission before opening the camera.
            checkCameraPermissionAndOpenCamera()

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            findViewById<Button>(R.id.btnCapture).setOnClickListener {
                // 用 try-catch 避免例外錯誤產生，若產生錯誤則使用 Toast 顯示
                try {
                    // 使用 startForResult 來拍攝照片
                    startForResult.launch(null)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this, "無相機應用程式", Toast.LENGTH_SHORT).show()
                }
            }

            findViewById<Button>(R.id.btnRotate).setOnClickListener {
                // 原本角度再加上90度
                angle += 90f
                // 使 ImageView 旋轉
                findViewById<ImageView>(R.id.imgPhoto).rotation = angle
            }
        }


    }


    private fun checkCameraPermissionAndOpenCamera() {
        when {
            // 3. Check if the permission is already granted.
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                // You have permission, so you can launch the camera directly.
                openCamera()
            }
            // 4. (Optional but recommended) Show a rationale before requesting.
            // This is good practice if the user has previously denied the request.
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                // Show a dialog explaining why you need the permission.
                // After the user sees the explanation, you can request again.
                // For now, we'll just request it directly.
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            // 5. Request the permission for the first time.
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        // This is where your original camera intent code should go.
        // For example:
        // val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // startActivity(cameraIntent)
    }

}