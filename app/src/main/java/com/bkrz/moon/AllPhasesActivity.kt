package com.bkrz.moon

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*

class AllPhasesActivity : AppCompatActivity() {

    private val formatter = SimpleDateFormat("dd.MM.yyyy")
    private val phaseCalculator = PhaseCalculator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_phases)

        initComponents()
    }

    override fun onResume() {
        super.onResume()

        initComponents()
    }

    private fun initComponents() {
        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnMinus: Button = findViewById(R.id.btnMinus)
        val listOfDates: LinearLayout = findViewById(R.id.list_of_moons)
        val yearEditText: EditText = findViewById(R.id.year_text)
        yearEditText.setText(Calendar.getInstance().get(Calendar.YEAR).toString())

        val yearValueInit: Int = yearEditText.text.toString().toInt()

        val savedSettings = readSettings()
        val algorithm = when (savedSettings?.algorithm) {
            "simple" -> Algorithm.SIMPLE
            "conway" -> Algorithm.CONWAY
            "trig1" -> Algorithm.TRIG_1
            "trig2" -> Algorithm.TRIG_2
            else -> Algorithm.TRIG_1
        }

        displayDates(listOfDates, yearValueInit, algorithm)

        yearEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var change = v.text.toString()

                if (change == "" || change == ".") {
                    change = "0"
                }

                var yearValue: Int
                try {
                    yearValue = change.toInt()
                } catch (e: NumberFormatException) {
                    yearValue = 2200
                    v.text = yearValue.toString()

                    Toast.makeText(
                        this@AllPhasesActivity,
                        "Wybierz rok przed 2201!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (yearValue < 1900) {
                    yearValue = 1900
                    v.text = yearValue.toString()

                    Toast.makeText(
                        this@AllPhasesActivity,
                        "Wybierz rok po 1899!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (yearValue > 2200) {
                    yearValue = 2200
                    v.text = yearValue.toString()

                    Toast.makeText(
                        this@AllPhasesActivity,
                        "Wybierz rok przed 2201!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                displayDates(listOfDates, yearValue, algorithm)

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }


        btnPlus.setOnClickListener {
            val year: EditText = findViewById(R.id.year_text)
            var yearValue: Int = year.text.toString().toInt()
            yearValue++

            if (yearValue > 2200) {
                yearValue = 2200

                Toast.makeText(
                    this@AllPhasesActivity,
                    "Wybierz rok przed 2201!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            year.setText(yearValue.toString())

            displayDates(listOfDates, yearValue, algorithm)
        }

        btnMinus.setOnClickListener {
            val year: EditText = findViewById(R.id.year_text)
            var yearValue: Int = year.text.toString().toInt()
            yearValue--

            if (yearValue < 1900) {
                yearValue = 1900

                Toast.makeText(
                    this@AllPhasesActivity,
                    "Wybierz rok po 1899!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            year.setText(yearValue.toString())

            displayDates(listOfDates, yearValue, algorithm)
        }
    }

    private fun displayDates(listOfDates: LinearLayout, year: Int, algorithm: Algorithm) {
        listOfDates.removeAllViews()

        val dates = phaseCalculator.calculateAllFullMoons(algorithm, year)

        for (date in dates) {
            val rowLayout = layoutInflater.inflate(R.layout.date_row, listOfDates, false)
            val textView: TextView = rowLayout.findViewById(R.id.full_in_year_date_row)

            textView.text = formatter.format(date.time)

            listOfDates.addView(rowLayout)
        }
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

    private fun readSettings(): SavedSettings? {
        var savedSettings: SavedSettings? = null
        try {
            val filename = "moonSettings.csv"
            if (fileExists(filename)) {
                val file = InputStreamReader(openFileInput(filename))
                val br = BufferedReader(file)

                val line = br.readLine()
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