package com.payment.watchdatafinger

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.payment.watchdatafinger.databinding.ActivityOneTuchBinding

class OneTuchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOneTuchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOneTuchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val qrCodeData = "Your QR Code Data Here"
        val qrCodeSize = 500

        val bitmap = generateQRCode(qrCodeData, qrCodeSize)
        binding.imgQr.setImageBitmap(bitmap)

    }

    private fun generateQRCode(data: String, size: Int): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)
        val pixels = IntArray(size * size)

        for (y in 0 until size) {
            for (x in 0 until size) {
                pixels[y * size + x] = if (generateQRBit(data, x, y)) Color.BLACK else Color.WHITE
            }
        }

        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        return bitmap
    }

    private fun generateQRBit(data: String, x: Int, y: Int): Boolean {
        val index = y * data.length + x
        return if (index < data.length) {
            val char = data[index]
            char.toInt() and 1 == 1
        } else {
            false
        }
    }

}