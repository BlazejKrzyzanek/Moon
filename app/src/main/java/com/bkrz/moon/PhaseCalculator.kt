package com.bkrz.moon

import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.security.spec.RSAKeyGenParameterSpec.F0
import java.util.*
import kotlin.math.*


class PhaseCalculator {

    private val daysInCycle = 29

    fun calculate(algorithm: Algorithm, date: Calendar): CalculationResult {
        var phaseDay = 0.0

        when (algorithm) {
            Algorithm.SIMPLE -> phaseDay = simple(date)
            Algorithm.CONWAY -> phaseDay = conway(date)
            Algorithm.TRIG_1 -> phaseDay = trig1(date)
            Algorithm.TRIG_2 -> phaseDay = trig2(date)
        }

        return CalculationResult(
            floor(phaseDay.div(daysInCycle).times(100.0)).roundToInt(),
            calculateLastNewMoonDate(date, round(phaseDay).roundToInt()),
            calculateNextFullMoonDate(date, round(phaseDay).roundToInt())
        )
    }

    private fun calculateNextFullMoonDate(date: Calendar, phaseDay: Int): Calendar {
        val daysLeft: Int = if (phaseDay < 15) {
            15 - phaseDay
        } else {
            daysInCycle.plus(15 - phaseDay)
        }
        val newDate = Calendar.getInstance()
        newDate.time = date.time
        newDate.add(Calendar.DAY_OF_MONTH, daysLeft)

        return newDate
    }

    private fun calculateLastNewMoonDate(date: Calendar, phaseDay: Int): Calendar {
        val newDate = Calendar.getInstance()
        newDate.time = date.time
        newDate.add(Calendar.DAY_OF_MONTH, -1 * phaseDay)
        return newDate
    }

    private fun simple(date: Calendar): Double {
        val lp = 2551443
        val newMoon = Calendar.getInstance()
        newMoon.set(1970, 1, 7, 20, 35, 0)
        val phase = (date.timeInMillis - newMoon.timeInMillis) / 1000.0 % lp

        return (floor(phase / (24.0 * 3600.0)) + 1)
    }

    private fun conway(date: Calendar): Double {
        var r: Double = (date.get(Calendar.YEAR) % 100).toDouble()
        r %= 19
        if (r > 9) {
            r -= 19
        }
        r = r * 11 % 30 + date.get(Calendar.MONTH) + date.get(Calendar.DAY_OF_MONTH)
        if (date.get(Calendar.MONTH) < 3) {
            r += 2
        }
        if (date.get(Calendar.YEAR) < 2000)
            r -= 4
        else
            r -= 8.3
        r = floor(r + 0.5) % 30
        return if (r < 0) (r + 30) else r
    }

    fun trig1(date: Calendar): Double {
        val thisJD = julday(
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH)
        )
        val degToRad = 3.14159265 / 180
        val K0 = floor((date.get(Calendar.YEAR) - 1900) * 12.3685)
        val T = (date.get(Calendar.YEAR) - 1899.5) / 100
        val T2 = T * T
        val T3 = T * T * T
        val J0 = 2415020 + 29 * K0
        val F0 =
            0.0001178 * T2 - 0.000000155 * T3 + (0.75933 + 0.53058868 * K0) - (0.000837 * T + 0.000335 * T2)
        val M0 = 360 * (getFrac(K0 * 0.08084821133)) + 359.2242 - 0.0000333 * T2 - 0.00000347 * T3
        val M1 = 360 * (getFrac(K0 * 0.07171366128)) + 306.0253 + 0.0107306 * T2 + 0.00001236 * T3
        val B1 =
            360 * (getFrac(K0 * 0.08519585128)) + 21.2964 - (0.0016528 * T2) - (0.00000239 * T3)
        var phase = 0
        var jday = 0
        var oldJ = 0
        while (jday < thisJD) {
            var F = F0 + 1.530588 * phase
            var M5 = (M0 + phase * 29.10535608) * degToRad
            var M6 = (M1 + phase * 385.81691806) * degToRad
            var B6 = (B1 + phase * 390.67050646) * degToRad
            F -= 0.4068 * Math.sin(M6) + (0.1734 - 0.000393 * T) * Math.sin(M5)
            F += 0.0161 * Math.sin(2 * M6) + 0.0104 * Math.sin(2 * B6)
            F -= 0.0074 * Math.sin(M5 - M6) - 0.0051 * Math.sin(M5 + M6)
            F += 0.0021 * Math.sin(2 * M5) + 0.0010 * Math.sin(2 * B6 - M6)
            F += 0.5 / 1440;
            oldJ = jday;
            jday = (J0 + 28 * phase + floor(F)).toInt()
            phase++
        }
        return (thisJD - oldJ) % 30
    }

    fun getFrac(fr: Double): Double {
        return (fr - floor(fr))
    }


    fun trig2(date: Calendar): Double {
        val n =
            floor(12.37 * (date.get(Calendar.YEAR) - 1900 + ((1.0 * (date.get(Calendar.MONTH)) - 0.5) / 12.0)))
        val rad = 3.14159265 / 180.0
        val t = n / 1236.85
        val t2 = t * t
        val as1 = 359.2242 + 29.105356 * n
        val am = 306.0253 + 385.816918 * n + 0.010730 * t2
        var xtra = 0.75933 + 1.53058868 * n + ((1.178e-4) - (1.55e-7) * t) * t2
        xtra += (0.1734 - 3.93e-4 * t) * sin(rad * as1) - 0.4068 * sin(rad * am)
        val i = if (xtra > 0.0) floor(xtra) else ceil(xtra - 1.0)
        val j1 = julday(
            date.get(Calendar.YEAR),
            date.get(Calendar.MONTH),
            date.get(Calendar.DAY_OF_MONTH)
        )
        val jd = (2415020 + 28 * n) + i
        return ((j1 - jd + 30) % 30)
    }

    private fun julday(year: Int, month: Int, day: Int): Double {

        val correctYear = if (year < 0) year + 1 else year
        var jy = correctYear
        var jm = month + 1
        if (month <= 2) {
            jy--; jm += 12; }
        var jul = floor(365.25 * jy) + floor(30.6001 * jm) + day + 1720995
        if (day + 31 * (month + 12 * correctYear) >= (15 + 31 * (10 + 12 * 1582))) {
            val ja = floor(0.01 * jy)
            jul = jul + 2 - ja + floor(0.25 * ja)
        }
        return jul
    }
}