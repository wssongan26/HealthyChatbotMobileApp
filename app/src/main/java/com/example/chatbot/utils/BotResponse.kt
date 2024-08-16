package com.example.chatbot.utils

import com.example.chatbot.utils.Constant.OPEN_GOOGLE
import com.example.chatbot.utils.Constant.OPEN_SEARCH
import java.sql.Date
import java.sql.Timestamp
import java.text.SimpleDateFormat

object BotResponse {
    private var userAge: Int? = null
    private var hasFamilyHistory: Boolean? = null
    private var bloodPressure: Int? = null
    private var userWeight: Double? = null
    private var userHeight: Double? = null

    private var step = 0
    private var isPredictingHeartAttack = false // Track if predicting heart attack

    fun processInput(input: String): String {
        val message = input.toLowerCase()

        return when {
            isPredictingHeartAttack -> { // Check if predicting heart attack
                predictHeartAttack(message)
            }
            message.contains("predict heart attack") || message.contains("predict") || message.contains("heart predict") || message.contains("heart")-> { // Start predicting heart attack
                step = 0 // Reset step
                isPredictingHeartAttack = true
                "Please enter your age."
            }
            else -> basicResponse(message) // Process basic responses
        }
    }

    private fun predictHeartAttack(message: String): String {
        when (step) {
            0 -> {
                // Asking for age
                try {
                    userAge = message.toInt()
                    step++
                    return "Do you have a family history of heart attack? (yes/no)"
                } catch (e: NumberFormatException) {
                    return "Please enter a valid age."
                }
            }
            1 -> {
                // Asking for family history
                hasFamilyHistory = message.toLowerCase() == "yes"
                step++
                return "Please enter your blood pressure."
            }
            2 -> {
                // Asking for blood pressure
                try {
                    bloodPressure = message.toInt()
                    step++
                    return "Please enter your weight (in kilograms)."
                } catch (e: NumberFormatException) {
                    return "Please enter a valid blood pressure value."
                }
            }
            3 -> {
                // Asking for weight
                try {
                    userWeight = message.toDouble()
                    step++
                    return "Please enter your height (in meters)."
                } catch (e: NumberFormatException) {
                    return "Please enter a valid weight value."
                }
            }
            4 -> {
                // Asking for height
                try {
                    userHeight = message.toDouble()
                    step++
                    // Analyzing user details and predicting heart attack
                    val result = HeartAttackPredictor.predictHeartAttack(
                        userAge ?: 0,
                        hasFamilyHistory ?: false,
                        bloodPressure ?: 0,
                        userWeight ?: 0.0,
                        userHeight ?: 0.0
                    )
                    // Additional conditions
                    val isAgeInRange = userAge in 19..25
                    val isWeightHigh = userWeight ?: 0.0 > 100
                    val isHeightInRange = userHeight!! in 160.0..170.0

                    return when {
                        result && isAgeInRange && hasFamilyHistory == true && bloodPressure ?: 0 > 150 && isWeightHigh && isHeightInRange -> {
                            "Based on the information provided, there is a potential risk of a heart attack. Please consult a healthcare professional."
                        }
                        !result && isAgeInRange && !hasFamilyHistory!! && bloodPressure ?: 0 <= 150 && !isWeightHigh && isHeightInRange -> {
                            "Based on the information provided, there is no significant risk of a heart attack. If you have further concerns, consult a healthcare professional."
                        }
                        else -> {
                            "Based on the information provided, there are no specific indicators for a heart attack. If you have concerns, consult a healthcare professional."
                        }
                    }
                } catch (e: NumberFormatException) {
                    return "Please enter a valid height value."
                }
            }
            else -> {
                isPredictingHeartAttack = false // Reset predicting heart attack
                return "Unexpected input."
            }
        }
    }

    private fun basicResponse(message: String): String {
        val random = (0..2).random()

        return when {
            message.contains("hello")  || message.contains("sup")   || message.contains("whatsup") -> {
                when (random) {
                    0 -> "Hello there!"
                    1 -> "Sup"
                    2 -> "Buongiorno"
                    else -> "error"
                }
            }
            message.contains("how are you") -> {
                when (random) {
                    0 -> "I am doing fine, Thanks for asking!"
                    1 -> "I am hungry"
                    2 -> "Pretty Good! How about you?"
                    else -> "error"
                }
            }
            message.contains("open") && message.contains("google") -> {
                OPEN_GOOGLE
            }
            message.contains("search") -> {
                OPEN_SEARCH
            }
            message.contains("time") && message.contains("?") -> {
                val timeStamp = Timestamp(System.currentTimeMillis())
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val date = sdf.format(Date(timeStamp.time))

                date.toString()
            }
            message.contains("flip") && message.contains("coin") -> {
                val r = (0..1).random()
                val result = if (r == 0) "heads" else "tails"

                "I flipped a coin and it landed on $result"
            }
            message.contains("solve") -> {
                val equation: String? = message.substringAfter("solve")
                return try {
                    val answer = SolveMath.solveMath(equation ?: "0")
                    answer.toString()
                } catch (e: Exception) {
                    "Sorry I cant solve that!"
                }
            }
            else -> {
                when (random) {
                    0 -> "I don't understand.."
                    1 -> "Idk"
                    2 -> "Try asking me something different"
                    else -> "error"
                }
            }
        }
    }
}
