package jp.techacademy.konomi.sugiura.autoslideshowapp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import android.database.Cursor
import kotlinx.android.synthetic.main.activity_main.*
import android.os.Handler
import android.widget.TextView
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    var cursor:Cursor? = null
    private var mTimer: Timer? = null
    private var mTimerSec = 0.0
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                getContentsInfo()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
                start_button.setEnabled(false)
                next_button.setEnabled(false)
                back_button.setEnabled(false)
                text.text = "ストレージの権限を許可してください"
            }
        } else {
            getContentsInfo()
        }

        next_button.setOnClickListener{
            if (cursor!!.moveToNext()) {
                var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor!!.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }else{
                cursor!!.moveToFirst()
                var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor!!.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }
        }

        back_button.setOnClickListener{
            if(cursor!!.moveToPrevious()){
                var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                var id = cursor!!.getLong(fieldIndex)
                var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)
            }else{
                cursor!!.moveToLast()
            }
        }

        start_button.setOnClickListener {
            if (start_button.text == "再生") {
                start_button.text = "停止"
                next_button.setEnabled(false)
                back_button.setEnabled(false)
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {
                        mTimerSec += 2
                        mHandler.post {
                            if (cursor!!.moveToNext()) {
                                var fieldIndex =
                                    cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                var id = cursor!!.getLong(fieldIndex)
                                var imageUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                                imageView.setImageURI(imageUri)
                            } else {
                                cursor!!.moveToFirst()
                                var fieldIndex =
                                    cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
                                var id = cursor!!.getLong(fieldIndex)
                                var imageUri = ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                                imageView.setImageURI(imageUri)
                            }
                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで 100ミリ秒、ループの間隔を 100ミリ秒 に設定
            }else{
                start_button.text = "再生"
                next_button.setEnabled(true)
                back_button.setEnabled(true)
                mTimer!!.cancel()
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }

    private fun getContentsInfo() {
        var resolver = contentResolver
        cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目(null = 全項目)
            null, // フィルタ条件(null = フィルタなし)
            null, // フィルタ用パラメータ
            null // ソート (null ソートなし)
        )
        if (cursor!!.moveToFirst()) {
            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }
    }
}