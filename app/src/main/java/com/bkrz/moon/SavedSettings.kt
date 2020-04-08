package com.bkrz.moon

class SavedSettings {
    var hemisphere: String? = null
    var algorithm: String? = null

    constructor(hemisphere: String?, algorithm: String?) {
        this.hemisphere = hemisphere
        this.algorithm = algorithm
    }

    constructor(line: String?) {
        if (line != null) {
            val tokens = line.split(";")
            if (tokens.size == 2) {
                hemisphere = tokens[0]
                algorithm = tokens[1]
            }
        }
    }

    override fun toString(): String {
        return "Saved settings: $hemisphere $algorithm"
    }

    fun toCSV() : String {
        return "$hemisphere;$algorithm\n"
    }


}