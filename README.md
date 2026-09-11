# Vaddi Calculator

**Vaddi Calculator** is a native Android fintech application designed for Indian users to calculate interest (*Vaddi*) and total payable or receivable amounts quickly, accurately, and reliably.

---

## 🌟 Key Features

1. **Quick Calculator Mode**:
   - Calculate Simple Interest by entering Principal, Interest Rate, and Duration.
   - Dynamic duration units: **Monthly** (Months), **Yearly** (Years), and **Daily** (Days).
   - Supports traditional Indian *₹2 vaddi*, *₹1.50 vaddi*, etc.

2. **Date Calculator Mode**:
   - Select calendar **Start Date** and **End Date** using a Material 3 date picker.
   - Calculates exact day counts (leap-year aware) and breaks down duration into **Years, Months, and Days**.
   - Accurately computes monthly and daily vaddi equivalents.

3. **Premium Fintech Result Card**:
   - Large, high-contrast typography for the **Total Amount**.
   - Visual breakdown: `Principal + Interest Earned = Total Amount`.
   - Metrics: Daily interest, monthly equivalent, and formula details.
   - Instant native **Share Sheet** integration.
   - One-tap **Save to History**.

4. **Offline History**:
   - Stored 100% locally on the device using **Room Database**.
   - Detailed inspection dialog with option to re-open calculations directly into the calculator.
   - Individual deletion and bulk clear with safety confirmations.

5. **Dual-Language Support**:
   - **English** and **Telugu (తెలుగు)** with in-app language switching.
   - Fully localized strings in `res/values/strings.xml` and `res/values-te/strings.xml`.

6. **Flexible Settings & Theming**:
   - **Theme**: System Default, Light, and Dark mode.
   - **Currency**: Indian Rupee (₹), US Dollar ($), Euro (€), and British Pound (£).

7. **100% Privacy & Offline-First**:
   - No backend, no login, no accounts.
   - Zero intrusive permissions (no contacts, location, camera, audio, or SMS).

---

## 🛠 Technology Stack

- **Language**: Kotlin 2.2+
- **UI Toolkit**: Jetpack Compose with Material Design 3 (M3)
- **Architecture**: MVVM (Model-View-ViewModel) with Clean Architecture principles
- **Local Storage**: Android Room Database with KSP (Kotlin Symbol Processing)
- **Asynchronous**: Kotlin Coroutines & StateFlow
- **Build System**: Gradle Kotlin DSL (`build.gradle.kts`)
- **SDK Target**: `compileSdk = 36`, `targetSdk = 36`, `minSdk = 24`
- **Java**: Java 17

---

## 🚀 How to Build & Run

### 1. Build Debug APK
```bash
gradle assembleDebug
```
The resulting debug APK will be located at:
`app/build/outputs/apk/debug/app-debug.apk`

### 2. Run Tests
```bash
gradle :app:testDebugUnitTest
```

### 3. Create Release APK / AAB for Google Play Store
```bash
# Generate Release APK
gradle assembleRelease

# Generate Android App Bundle (AAB) for Play Store
gradle bundleRelease
```
The signed/ready bundle will be at:
`app/build/outputs/bundle/release/app-release.aab`

---

## ⚙️ Configuration & Maintenance

### Where to Change App Version
Open `app/build.gradle.kts` and update the `defaultConfig` block:
```kotlin
defaultConfig {
    applicationId = "com.vaddicalculator.app"
    minSdk = 24
    targetSdk = 36
    versionCode = 1        // Increment for each Play Store release
    versionName = "1.0.0"  // User-facing version string
}
```

### Where to Add AdMob IDs Later
Advertising architecture is cleanly decoupled in:
`app/src/main/java/com/vaddicalculator/app/data/ads/AdManager.kt`

To enable Google Mobile Ads:
1. Add the Google Play Services Ads dependency in `app/build.gradle.kts`.
2. Add the AdMob App ID `<meta-data>` in `AndroidManifest.xml`.
3. Replace the test ad unit IDs in `AdManager.kt` with your verified AdMob Ad Unit IDs and set `isAdsEnabled = true`.

### Where Translations are Stored
- **English**: `app/src/main/res/values/strings.xml`
- **Telugu**: `app/src/main/res/values-te/strings.xml`

---

## 📄 Documentation
- `PRIVACY_POLICY.md` — Privacy Policy document for Play Store listing.
- `TERMS_OF_USE.md` — Standard Terms of Use.
- `PLAY_STORE_CHECKLIST.md` — Complete Google Play Store submission guide.
