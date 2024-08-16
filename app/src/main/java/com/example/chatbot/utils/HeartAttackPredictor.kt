package com.example.chatbot.utils

import android.util.Log


object HeartAttackPredictor {
    fun predictHeartAttack(age: Int, familyHistory: Boolean, bloodPressure: Int, weight: Double, height: Double): Boolean {
        // Check age range
        if (age !in 19..25) {
            Log.d("HeartAttackPredictor", "Age is not within the valid range (19-25). Please re-enter.")
            return false
        }

        // Check family history
        if (familyHistory) {
            Log.d("HeartAttackPredictor", "Family history of heart attack detected.")
            return true
        }

        // Check blood pressure
        if (bloodPressure > 150) {
            Log.d("HeartAttackPredictor", "High blood pressure detected.")
            return true
        }

        // Check weight
        if (weight > 100) {
            Log.d("HeartAttackPredictor", "High weight detected.")
            return true
        }

        // Check height
        if (height < 160 || height > 170) {
            Log.d("HeartAttackPredictor", "Height not within the valid range (160-170). Please re-enter.")
            return false
        }

        // No significant indicators of a heart attack
        return false
    }
}
