package edu.rosehulman.bockkedummitrj.atomicaerobic.ui

data class Workouts(
    val armWorkouts: ArrayList<String> = arrayListOf<String>(
        "Windmills",
        "Pushups",
        "Pull Ups",
        "Tricep Dips",
        "Inverted Row",
        "Handstand"
    ),

    val legWorkouts: ArrayList<String> = arrayListOf<String>(
        "Squats",
        "Leg Raises",
        "Lunges",
        "Calf Raises",
        "Heel Raises",
        "Pull ins"
    ),

    val coreWorkouts: ArrayList<String> = arrayListOf<String>(
        "Plank",
        "Sit Ups",
        "Crunches",
        "Bicycle Crunches",
        "Mountain Climbers",
        "High Knees"
    ),

    val cardioWorkouts: ArrayList<String> = arrayListOf<String>(
        "Jumping Jacks",
        "Burpees",
        "Butt Kicks",
        "Squat Jumps",
        "Inchworm",
        "Jog"
    )

)