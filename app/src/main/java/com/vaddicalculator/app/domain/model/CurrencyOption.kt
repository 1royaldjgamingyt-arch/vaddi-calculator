package com.vaddicalculator.app.domain.model

enum class CurrencyOption(val symbol: String, val code: String, val displayName: String) {
    INR("₹", "INR", "Indian Rupee (₹)"),
    USD("$", "USD", "US Dollar ($)"),
    EUR("€", "EUR", "Euro (€)"),
    GBP("£", "GBP", "British Pound (£)")
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    HINDI("hi", "हिन्दी (Hindi)", "हिन्दी"),
    TELUGU("te", "తెలుగు (Telugu)", "తెలుగు"),
    TAMIL("ta", "தமிழ் (Tamil)", "தமிழ்"),
    KANNADA("kn", "ಕನ್ನಡ (Kannada)", "ಕನ್ನಡ"),
    MALAYALAM("ml", "മലയാളം (Malayalam)", "മലയാളം"),
    MARATHI("mr", "मराठी (Marathi)", "मराठी"),
    BENGALI("bn", "বাংলা (Bengali)", "বাংলা"),
    GUJARATI("gu", "ગુજરાતી (Gujarati)", "ગુજરાતી"),
    PUNJABI("pa", "ਪੰਜਾਬੀ (Punjabi)", "ਪੰਜਾਬੀ"),
    ODIA("or", "ଓଡ଼ିଆ (Odia)", "ଓଡ଼ିଆ"),
    ASSAMESE("as", "অসমীয়া (Assamese)", "অসমীয়া");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ENGLISH
        }
    }
}
