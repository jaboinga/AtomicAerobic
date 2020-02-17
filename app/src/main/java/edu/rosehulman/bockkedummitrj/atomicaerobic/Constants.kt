package edu.rosehulman.bockkedummitrj.atomicaerobic

object Constants {
    const val TAG = "aa"
    const val BLOCKOUT_TIMES_COLLECTION = "blockout_times"
    const val SETTINGS_COLLECTION = "settings"
    const val COMPLETED_SESSIONS_COLLECTION = "completed_sessions"
    const val INTERVALS_COLLECTION = "intervals"

    const val DEFAULT_TOTAL_TIME = 60
    const val DEFAULT_TIME_PER_SESSION = 10

    const val CHANNEL_ID = "edu.rosehulman.bockkedummitrj.atomicaerobic"
    const val WORKOUT_TIMER_TAG = "workoutTimerFragment"
    const val WORKOUT_TIMER_TAG_TWO = "workoutTimerFragment2"

    const val UID_TAG = "uid"

    val armWorkouts: List<String> = listOf<String>(
        "Windmills",
        "Pushups",
        "Pull Ups",
        "Tricep Dips",
        "Inverted Row",
        "Handstand"
    )

    val legWorkouts: List<String> = listOf<String>(
        "Squats",
        "Leg Raises",
        "Lunges",
        "Calf Raises",
        "Heel Raises",
        "Pull ins"
    )

    val coreWorkouts: List<String> = listOf<String>(
        "Plank",
        "Sit Ups",
        "Crunches",
        "Bicycle Crunches",
        "Mountain Climbers",
        "High Knees"
    )

    val cardioWorkouts: List<String> = listOf<String>(
        "Jumping Jacks",
        "Burpees",
        "Butt Kicks",
        "Squat Jumps",
        "Inchworm",
        "Jog"
    )
}