package edu.rosehulman.bockkedummitrj.atomicaerobic

data class Interval(
    var workout: String,
    var hour: Int,
    var minute : Int,
    var duration: Long // in seconds always
)