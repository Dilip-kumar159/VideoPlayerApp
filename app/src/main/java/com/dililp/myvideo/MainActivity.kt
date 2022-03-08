package com.dililp.myvideo

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.dililp.myvideo.databinding.ActivityMainBinding
import com.dililp.myvideo.databinding.ThemeViewBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    //    private var runnable : Runnable? = null?
    private lateinit var currentFragment: Fragment

    companion object {
        lateinit var videoList: ArrayList<Video>
        lateinit var folderList: ArrayList<Folder>
        lateinit var searchList: ArrayList<Video>
        var search: Boolean = false
        var themeIndex: Int = 0
        var sortValue: Int = 0
        val themesList = arrayOf(
            R.style.coolPinkNav, R.style.coolBlueNav, R.style.coolRedNav, R.style.coolGreenNav,
            R.style.coolYellowNav, R.style.coolBlackNav
        )

        //        var dataChanged : Boolean = false
//        var adapterChanged : Boolean = false
        val sortList = arrayOf(
            MediaStore.Video.Media.DATE_ADDED + " DESC",
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.TITLE,
            MediaStore.Video.Media.TITLE + " DESC",
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.SIZE + " DESC"
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val editor = getSharedPreferences("Themes", MODE_PRIVATE)
        themeIndex = editor.getInt("themeIndex", 0)

        setTheme(themesList[themeIndex])
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // For Navigation Bar
        toggle = ActionBarDrawerToggle(this, binding.root, R.string.open, R.string.close)
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (requestRuntTimePermission()) {
            folderList = ArrayList()
            videoList = getAllVideos(this)
            setFragment(VideosFragment())

//            runnable = Runnable{
//                if(dataChanged){
//                    videoList = getAllVideos(this)
//                    dataChanged = false
//                    adapterChanged = true
//                }
//                Handler(Looper.getMainLooper()).postDelayed(runnable!!, 200)
//            }
//            Handler(Looper.getMainLooper()).postDelayed(runnable!!, 0)
        } else {  // This was missing added
            folderList = ArrayList()
            videoList = ArrayList()
        }
        binding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.videoView -> setFragment(VideosFragment())
                R.id.foldersView -> setFragment(FoldersFragment())
            }
            return@setOnItemSelectedListener true
        }
        binding.navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.themesNav -> {
                    val customDialog =
                        LayoutInflater.from(this).inflate(R.layout.theme_view, binding.root, false)
                    val bindingTV = ThemeViewBinding.bind(customDialog)
                    val dialog = MaterialAlertDialogBuilder(this).setView(customDialog)
                        .setTitle("Select Theme")
                        .create()
                    dialog.show()
                    when (themeIndex) {
                        0 -> bindingTV.themePink.setBackgroundColor(Color.GRAY)
                        1 -> bindingTV.themeBlue.setBackgroundColor(Color.GRAY)
                        2 -> bindingTV.themeRed.setBackgroundColor(Color.GRAY)
                        3 -> bindingTV.themeGreen.setBackgroundColor(Color.GRAY)
                        4 -> bindingTV.themeYellow.setBackgroundColor(Color.GRAY)
                        5 -> bindingTV.themeBlack.setBackgroundColor(Color.GRAY)
                    }
                    bindingTV.themePink.setOnClickListener { saveTheme(0) }

                    bindingTV.themeBlue.setOnClickListener { saveTheme(1) }

                    bindingTV.themeRed.setOnClickListener { saveTheme(2) }

                    bindingTV.themeGreen.setOnClickListener { saveTheme(3) }

                    bindingTV.themeYellow.setOnClickListener { saveTheme(4) }

                    bindingTV.themeBlack.setOnClickListener { saveTheme(5) }
                }
                R.id.sortOrderNav -> {
                    val menuItems = arrayOf(
                        "Latest",
                        "Oldest",
                        "Name(A to Z)",
                        "Name(a to z)",
                        "File Size(Smallest)",
                        "File Size(Largest)"
                    )
                    var value = sortValue
                    val dialog = MaterialAlertDialogBuilder(this)
                        .setTitle("Sort By")
                        .setPositiveButton("OK") { _, _ ->
                            val sortEditor = getSharedPreferences("Sorting", MODE_PRIVATE).edit()
                            sortEditor.putInt("sortValue", value)
                            sortEditor.apply()

                            // for Restarting App inorder to set the Themes
                            finish()
                            startActivity(intent)
                        }
                        .setSingleChoiceItems(menuItems, sortValue) { _, pos ->
                            value = pos
                        }
                        .create()
                    dialog.show()
                    dialog.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(Color.BLACK)
                }
                R.id.aboutNav -> startActivity(Intent(this, AboutActivity::class.java))
                R.id.exitNav -> exitProcess(1)
            }
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setFragment(fragment: Fragment) {
        currentFragment = fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragmentFL, fragment)
        transaction.disallowAddToBackStack()
        transaction.commit()
    }


    // for requesting permission
    private fun requestRuntTimePermission(): Boolean {

        // requesting storage permission for only devices less than api 28
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
                return false
            }
        } else {
            // read external storage permission for devices higher than android 10 i.e api 29
            if (ActivityCompat.checkSelfPermission(
                    this,
                    READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 14)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 13) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show()
                folderList = ArrayList()
                videoList = getAllVideos(this)
                setFragment(VideosFragment())
            } else
                Snackbar.make(binding.root, "Storage Permission Needed!!", 5000)
                    .setAction("OK") {
                        ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
                    }
                    .show()
            // ActivityCompat.requestPermissions(this, arrayOf(WRITE_EXTERNAL_STORAGE), 13)
        }
        //for read external storage permission
        if (requestCode == 14) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                folderList = ArrayList()
                videoList = getAllVideos(this)
                setFragment(VideosFragment())
            } else Snackbar.make(binding.root, "Storage Permission Needed!!", 5000)
                .setAction("OK") {
                    ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE), 14)
                }
                .show()
//            else
//                ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE),14)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val gradientList = arrayOf(R.drawable.pink_gradient, R.drawable.blue_gradient, R.drawable.red_gradient,
        R.drawable.green_gradient, R.drawable.yellow_gradient, R.drawable.black_gradient)

        findViewById<LinearLayout>(R.id.gradientLayout).setBackgroundResource(gradientList[themeIndex])

        if(toggle.onOptionsItemSelected(item))
            return true
        return super.onOptionsItemSelected(item)
    }

    private fun saveTheme(index : Int){
        val editor = getSharedPreferences("Themes", MODE_PRIVATE).edit()
        editor.putInt("themeIndex", index)
        editor.apply()

        // for Restarting App inorder to set the Themes
        finish()
        startActivity(intent)
    }

//    override fun onDestroy() {
//        super.onDestroy()
//        runnable = null
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        (currentFragment as VideosFragment).adapter.onResult(requestCode, resultCode)
    }
}
