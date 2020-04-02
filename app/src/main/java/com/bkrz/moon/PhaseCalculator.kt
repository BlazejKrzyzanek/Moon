package com.bkrz.moon

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt


class PhaseCalculator {

    private val daysInCycle = 29

    fun calculate(algorithm: Algorithm, date: Date): CalculationResult {
        var phaseDay = 0

        when (algorithm) {
            Algorithm.SIMPLE -> phaseDay = simple(date)
        }

        return CalculationResult(
            phaseDay,
            calculatePercentOfMoon(phaseDay),
            floor(phaseDay.toDouble().div(daysInCycle).times(100.0)).toInt(),
            calculateLastNewMoonDate(date, phaseDay),
            calculateNextFullMoonDate(date, phaseDay)
        )
    }

    private fun calculateNextFullMoonDate(date: Date, phaseDay: Int): Date {

        val daysLeft: Int

        if (phaseDay < 15) {
            daysLeft = 15 - phaseDay
        } else {
            daysLeft = daysInCycle.plus(15 - phaseDay)
        }

        val resultDate = Date()
        resultDate.time = date.time + daysLeft.times(86400000)

        return resultDate
    }

    private fun calculateLastNewMoonDate(date: Date, phaseDay: Int): Date {
        val resultDate = Date()
        resultDate.time = date.time - phaseDay.times(86400000)

        return resultDate
    }

    private fun calculatePercentOfMoon(phaseDay: Int): Int {
        return 40
    }

    fun simple(date: Date): Int {
        val lp = 2551443
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val newMoon = formatter.parse("1970-01-07 20:35:00")
        val phase = (date.time - newMoon.time) / 1000.0 % lp

        return (floor(phase / (24.0 * 3600.0)) + 1).roundToInt()
    }

    fun conway(date: Date): Int {
        return 0
    }
}