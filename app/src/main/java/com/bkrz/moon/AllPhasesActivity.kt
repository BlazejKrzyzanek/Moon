package com.bkrz.moon

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class AllPhasesActivity : AppCompatActivity() {

    private val formatter = SimpleDateFormat("dd.MM.yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_phases)

        initComponents()
    }

    private fun initComponents() {
        val btnPlus: Button = findViewById(R.id.btnPlus)
        val btnMinus: Button = findViewById(R.id.btnMinus)

        val listOfDates: LinearLayout = findViewById(R.id.list_of_moons)
        val yearEditText: EditText = findViewById(R.id.year_text)
        var yearValueInit: Int = yearEditText.text.toString().toInt()

        calculateDates(listOfDates, yearValueInit)

        yearEditText.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                var change = v.text.toString()

                if (change == "" || change == ".") {
                    change = "0"
                }

                var yearValue: Int
                try {
                     yearValue = change.toInt()
                } catch (e : NumberFormatException)
                {
                    yearValue = 2099
                    v.text = yearValue.toString()

                    Toast.makeText(
                        this@AllPhasesActivity,
                        "Wybierz rok przed 2100!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                if (yearValue < 1971) {
                    yearValue = 1971
                    v.text = yearValue.toString()

                    Toast.makeText(
                        this@AllPhasesActivity,
                        "Wybierz rok po 1970!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (yearValue > 2099) {
                    yearValue = 2099
                    v.text = yearValue.toString()

                    Toast.makeText(
                        this@AllPhasesActivity,
                        "Wybierz rok przed 2100!",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                calculateDates(listOfDates, yearValue)

                return@setOnEditorActionListener true
            }

            return@setOnEditorActionListener false
        }


        btnPlus.setOnClickListener {
            val year: EditText = findViewById(R.id.year_text)
            var yearValue: Int = year.text.toString().toInt()
            yearValue++

            if (yearValue > 2099) {
                yearValue = 2099

                Toast.makeText(
                    this@AllPhasesActivity,
                    "Wybierz rok przed 2100!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            year.setText(yearValue.toString())

            calculateDates(listOfDates, yearValue)
        }

        btnMinus.setOnClickListener {
            val year: EditText = findViewById(R.id.year_text)
            var yearValue: Int = year.text.toString().toInt()
            yearValue--

            if (yearValue < 1971) {
                yearValue = 1971

                Toast.makeText(
                    this@AllPhasesActivity,
                    "Wybierz rok po 1970!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            year.setText(yearValue.toString())

            calculateDates(listOfDates, yearValue)
        }
    }

    private fun calculateDates(listOfDates: LinearLayout, year: Int) {
        listOfDates.removeAllViews()

        val dates = listOf(Date(), formatter.parse("01.03.$year"))

        for (date in dates) {
            val rowLayout = layoutInflater.inflate(R.layout.date_row, listOfDates, false)
            val textView: TextView = rowLayout.findViewById(R.id.full_in_year_date_row)

            textView.text = formatter.format(date)

            listOfDates.addView(rowLayout)
        }
    }
}