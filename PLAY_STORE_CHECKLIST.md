# Google Play Store Release Checklist for Vaddi Calculator

A comprehensive pre-launch checklist to ensure a seamless Google Play Store review and launch for **Vaddi Calculator** (`com.vaddicalculator.app`).

---

## 1. App Identity & Store Listing
- [x] **App Name**: "Vaddi Calculator" (under 30 characters, no forbidden promotional keywords).
- [x] **Short Description**: "Calculate interest (Vaddi) and total amounts quickly, accurately, and offline."
- [x] **Full Description**: Outlines Quick Calculation, Date-based calculator (Years, Months, Days breakdown), Indian Rupee formatting, Telugu & English localization, and offline history.
- [x] **Category**: Finance / Tools.
- [x] **App Icon**: 512x512 PNG adaptive fintech icon featuring the modern "V" with rupee symbol.
- [x] **Feature Graphic**: 1024x500 PNG banner highlighting the key calculations and sleek fintech dark/light UI.
- [x] **Screenshots**: High-resolution phone (portrait) and 7"/10" tablet screenshots showing the Dashboard, Result card, and History.

---

## 2. Technical & SDK Compliance
- [x] **Target API Level**: Android API 36 (Complies with latest Google Play requirement).
- [x] **Minimum SDK**: Android API 24 (Supports 95%+ of active Android devices).
- [x] **App Bundle Format**: Android App Bundle (`.aab`) configured in Gradle.
- [x] **64-bit Architecture**: Built natively using Kotlin and standard Android Gradle toolchain.
- [x] **Signing Config**: Release keystore configuration ready via environment variables or JKS file in `app/build.gradle.kts`.

---

## 3. Privacy & Permissions Policy
- [x] **Zero Intrusive Permissions**: No access to Contacts, SMS, Camera, Location, Storage, or Phone.
- [x] **Privacy Policy URL**: Link to `PRIVACY_POLICY.md` hosted on GitHub Pages or company website.
- [x] **Data Safety Form**:
  - *Data collected*: None.
  - *Data shared*: None.
  - *Data stored*: On-device local storage only (Room database).
  - *Data deletion*: Users can clear all records directly inside the app.

---

## 4. Quality & Performance Verification
- [x] **Edge-to-Edge Display**: Implemented via `enableEdgeToEdge()` and WindowInsets handling.
- [x] **Dynamic Theme**: Supports Light, Dark, and System theme without UI artifacts.
- [x] **Accessibility**: Minimum 48dp touch targets, semantic content descriptions on all icons, high contrast text.
- [x] **Responsive Layouts**: Flexible BoxWithConstraints/widthIn bounds for compact phones, large phones, foldables, and tablets.
- [x] **Error Handling**: Input validation for empty values, negative numbers, and invalid calendar date ranges.

---

## 5. Release Build Commands
```bash
# Clean and run unit tests
gradle :app:testDebugUnitTest

# Assemble production Android App Bundle (AAB)
gradle bundleRelease
```
Upload the generated bundle to the Google Play Console under **Production** or **Internal Testing**.
