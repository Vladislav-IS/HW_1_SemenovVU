package com.semenov.hw1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.add
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity(R.layout.activity_main) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<MainFragment>(R.id.fragment_container_view)
            }
        }
    }
}
