package com.bkrz.moon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    var phaseCalculator: PhaseCalculator = PhaseCalculator()
    private val formatter = SimpleDateFormat("dd.MM.yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initComponents()
    }

    override fun onResume() {
        super.onResume()
        initComponents()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        var inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            else -> {
            }
        }

        return true
    }


    private fun initComponents() {
        val savedSettings = readSettings()

        val todayTextView = findViewById<TextView>(R.id.today_text)
        val lastNewTextView = findViewById<TextView>(R.id.last_new_text)
        val nextFullTextView = findViewById<TextView>(R.id.next_full_text)

        val currentDate = Calendar.getInstance()

        val algorithm = when (savedSettings?.algorithm) {
            "simple" -> Algorithm.SIMPLE
            "conway" -> Algorithm.CONWAY
            "trig1" -> Algorithm.TRIG_1
            "trig2" -> Algorithm.TRIG_2
            else -> Algorithm.TRIG_1
        }

        val hemisphereFileName = when (savedSettings?.hemisphere) {
            "north" -> "n"
            "south" -> "s"
            else -> "n"
        }

        val result = phaseCalculator.calculate(algorithm, currentDate, hemisphereFileName)

        todayTextView.text = getString(R.string.today).plus(" ").plus(result.phasePercent).plus("%")
        lastNewTextView.text =
            getString(R.string.last_new_moon).plus(" ")
                .plus(formatter.format(result.lastNewMoon.time))
                .plus(" r.")
        nextFullTextView.text =
            getString(R.string.next_full_moon).plus(" ")
                .plus(formatter.format(result.nextFullMoon.time))
                .plus(" r.")

        val yearPhasesButton = findViewById<Button>(R.id.full_in_year)

        yearPhasesButton.setOnClickListener {
            val intent = Intent(this, AllPhasesActivity::class.java)
            startActivity(intent)
        }

        val imageView = findViewById<ImageView>(R.id.imageView)

        val resourceId = resources.getIdentifier(result.filename, "drawable", packageName)
        imageView.setImageResource(resourceId)
    }

    private fun readSettings(): SavedSettings? {
        var savedSettings: SavedSettings? = null
        try {
            val filename = "moonSettings.csv"
            if (fileExists(filename)) {
                val file = InputStreamReader(openFileInput(filename))
                val br = BufferedReader(file)

                var line = br.readLine()
                file.close()

                savedSettings = SavedSettings(line)
            }
        } catch (e: Exception) {
        }

        return savedSettings
    }

    private fun fileExists(path: String): Boolean {
        val file = baseContext.getFileStreamPath(path)
        return file.exists()
    }

}

