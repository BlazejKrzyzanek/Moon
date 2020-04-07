package com.bkrz.moon

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

    private fun initComponents() {
        val todayTextView = findViewById<TextView>(R.id.today_text)
        val lastNewTextView = findViewById<TextView>(R.id.last_new_text)
        val nextFullTextView = findViewById<TextView>(R.id.next_full_text)

        val currentDate = Calendar.getInstance()
        val result = phaseCalculator.calculate(Algorithm.TRIG_1, currentDate)

        todayTextView.text = getString(R.string.today).plus(" ").plus(result.phasePercent).plus("%")
        lastNewTextView.text =
            getString(R.string.last_new_moon).plus(" ").plus(formatter.format(result.lastNewMoon.time))
                .plus(" r.")
        nextFullTextView.text =
            getString(R.string.next_full_moon).plus(" ").plus(formatter.format(result.nextFullMoon.time))
                .plus(" r.")

        val yearPhasesButton = findViewById<Button>(R.id.full_in_year)

        yearPhasesButton.setOnClickListener {
            val intent = Intent(this, AllPhasesActivity::class.java)
            startActivity(intent)
        }

    }

}
