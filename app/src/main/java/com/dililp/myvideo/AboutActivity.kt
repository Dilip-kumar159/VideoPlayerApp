package com.dililp.myvideo

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dililp.myvideo.MainActivity.Companion.themeIndex
import com.dililp.myvideo.MainActivity.Companion.themesList
import com.dililp.myvideo.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setTheme(themesList[themeIndex])
        val binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "About"
        binding.aboutText.text = "Developed By : Dilip V \n\n Enjoy the Video Player. \n\n " +
                " This Video Player as a cool Feature like \n\n " +
                " 1. Double Tap Feature.\n\n " +
                " 2. Right swipe to Change Brightness.\n\n " +
                " 3. Left swipe to Change Volume.\n\n " +
                " 4. Custom Theme Selection.\n\n " +
                " 5. Swipe to Refresh User-Interface.\n\n " +
                " 6. UI designs are made using Material Widgets.\n\n "
//                " 7. Subtitles Selection\n\n " +
//                " 8. Audio Selection\n\n " +
//                " 9. Sleeper Time\n\n "
    }
}