package com.vaddicalculatortool.app.data.ads

/**
 * AdManager provides an isolated interface for advertising integration.
 * In compliance with requirements, real AdMob IDs are not embedded.
 * All ad hooks are isolated from core calculation and business logic.
 */
object AdManager {
    // Official Google AdMob Sample / Test Ad Unit IDs
    const val TEST_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111"
    const val TEST_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

    // Feature toggle for ads - can be enabled once AdMob SDK dependency is added
    var isAdsEnabled: Boolean = false

    fun isReady(): Boolean = isAdsEnabled
}
