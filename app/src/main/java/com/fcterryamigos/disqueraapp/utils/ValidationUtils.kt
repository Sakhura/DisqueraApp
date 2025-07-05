package com.fcterryamigos.disqueraapp.utils


object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= Constants.MIN_PASSWORD_LENGTH
    }

    fun isValidName(name: String): Boolean {
        return name.isNotEmpty() && name.trim().length >= 2
    }

    fun isValidPhone(phone: String): Boolean {
        return phone.isEmpty() || (phone.length >= 10 && phone.all { it.isDigit() || it == ' ' || it == '-' || it == '(' || it == ')' })
    }

    fun isValidCardNumber(cardNumber: String): Boolean {
        val cleanNumber = cardNumber.replace(" ", "").replace("-", "")
        return cleanNumber.length == 16 && cleanNumber.all { it.isDigit() }
    }

    fun isValidCVV(cvv: String): Boolean {
        return cvv.length in 3..4 && cvv.all { it.isDigit() }
    }

    fun isValidExpiryDate(expiryDate: String): Boolean {
        if (!expiryDate.matches(Regex("\\d{2}/\\d{2}"))) return false

        val parts = expiryDate.split("/")
        val month = parts[0].toIntOrNull() ?: return false
        val year = parts[1].toIntOrNull() ?: return false

        if (month < 1 || month > 12) return false

        val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR) % 100
        val currentMonth = java.util.Calendar.getInstance().get(java.util.Calendar.MONTH) + 1

        return when {
            year > currentYear -> true
            year == currentYear -> month >= currentMonth
            else -> false
        }
    }

    fun formatCardNumber(cardNumber: String): String {
        val clean = cardNumber.replace(" ", "")
        return clean.chunked(4).joinToString(" ")
    }

    fun formatExpiryDate(input: String): String {
        val clean = input.replace("/", "")
        return when {
            clean.length >= 4 -> "${clean.substring(0, 2)}/${clean.substring(2, 4)}"
            clean.length >= 2 -> "${clean.substring(0, 2)}/"
            else -> clean
        }
    }
}