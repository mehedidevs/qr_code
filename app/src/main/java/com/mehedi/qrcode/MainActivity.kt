package com.mehedi.qrcode

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.qrcode.databinding.ActivityMainBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var bmp: Bitmap


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.convertBtn.setOnClickListener {

            binding.batmanTxt.text = "Mago Moira gelam"


            val data = binding.batmanTxt.text.toString().trim()

            val writer = QRCodeWriter()

            try {

                val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512)
                val width = bitMatrix.width
                val height = bitMatrix.height
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

                for (x in 0 until width) {
                    for (y in 0 until height) {

                        var color = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE

                        bmp.setPixel(x, y, color)


                    }


                }

                binding.batmanLogo.setImageBitmap(bmp)

                binding.saveBtn.visibility = View.VISIBLE


            } catch (e: java.lang.Exception) {

            }


        }

        binding.saveBtn.setOnClickListener {
            saveImage()
        }


        binding.shareBtn.setOnClickListener {
            shareImage()
        }


    }


    private fun saveImage() {

        val contentResolver: ContentResolver = contentResolver

        val images: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.DISPLAY_NAME, "${System.currentTimeMillis()}.png")
        contentValues.put(MediaStore.Images.Media.MIME_TYPE, "images/*")

        val uri: Uri? = contentResolver.insert(images, contentValues)


        try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri!!)

            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

            outputStream?.close()


        } catch (x: Exception) {

            Log.i("TAG", "saveImage Error : $x ")

        }


    }

    private fun shareImage() {
        shareImageAndTxt(bmp)

    }


    fun shareImageAndTxt(bmp: Bitmap) {

        val uri = imageUri(bmp)

        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.type = "image/*"
        startActivity(Intent.createChooser(intent, "Via"))

    }


    fun imageUri(bmp: Bitmap): Uri? {

        var folder = File(cacheDir, "images")
        var uri: Uri
        try {

            folder.mkdirs()

            var file = File(folder, "Image.png")

            var outputStream = FileOutputStream(file)

            bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)



            outputStream.flush()
            outputStream.close()

            uri = FileProvider.getUriForFile(this, "com.example.iamrich", file)

            return uri
        } catch (e: java.lang.Exception) {

        }

        return null


    }

}