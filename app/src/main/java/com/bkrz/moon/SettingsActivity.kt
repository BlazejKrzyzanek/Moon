package com.bkrz.moon

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_layout)

        initComponents()
    }

    private fun initComponents() {
        val savedSettings = readSettings()

        val radioGroupHemisphere: RadioGroup = findViewById(R.id.used_hemisphere)
        val hemisphereId = when (savedSettings?.hemisphere) {
            "north" -> R.id.radio_N
            "south" -> R.id.radio_S
            else -> R.id.radio_N
        }
        radioGroupHemisphere.check(hemisphereId)

        val radioGroupAlgorithm: RadioGroup = findViewById(R.id.used_algorithm)
        val algorithmId = when (savedSettings?.algorithm) {
            "simple" -> R.id.radio_simple
            "conway" -> R.id.radio_conway
            "trig1" -> R.id.radio_trig1
            "trig2" -> R.id.radio_trig2
            else -> R.id.radio_trig1
        }
        radioGroupAlgorithm.check(algorithmId)

        val saveButton: Button = findViewById(R.id.save_button)
        saveButton.setOnClickListener {
            val radioGroupHemisphere: RadioGroup = findViewById(R.id.used_hemisphere)
            val selectedHemisphereId = radioGroupHemisphere.checkedRadioButtonId
            val selectedHemisphere: RadioButton = findViewById(selectedHemisphereId)

            val radioGroupAlgorithm: RadioGroup = findViewById(R.id.used_algorithm)
            val selectedAlgorithmId = radioGroupAlgorithm.checkedRadioButtonId
            val selectedAlgorithm: RadioButton = findViewById(selectedAlgorithmId)

            val savedSettings =
                SavedSettings(selectedHemisphere.tag as String?, selectedAlgorithm.tag as String?)

            val filename = "moonSettings.csv"
            val file = OutputStreamWriter(openFileOutput(filename, Context.MODE_PRIVATE))

            file.write(savedSettings.toCSV())

            file.flush()
            file.close()

            Toast.makeText(this, getString(R.string.settings_saved), Toast.LENGTH_LONG).show()
        }
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
