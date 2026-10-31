# TarkovRadar — Google Play Store Publishing Guide

This guide provides everything needed to prepare, configure, and publish **TarkovRadar** to the Google Play Console, resolve common device verification warnings, and manage future release version bumps.

---

## 1. App Identity & Store Credentials

| Field | Configuration / Value |
| :--- | :--- |
| **App Name** | `TarkovRadar` |
| **Package Name (`applicationId`)** | `com.tarkovradar.app` |
| **Current Version Code** | `5` |
| **Current Version Name** | `5.0` |
| **Developer Contact Email** | `kthuang09@gmail.com` |
| **Privacy Policy URLs (Any of these work)** | **Option A (GitHub Pages - Recommended):** `https://<github-username>.github.io/<repo-name>/`<br>**Option B (GitHub Markdown):** `https://github.com/<github-username>/<repo-name>/blob/main/PRIVACY_POLICY.md`<br>**Option C (Cloud Run / AI Studio):** `https://ais-pre-3da7mm66phssvwyi2vytqa-628863764922.us-west2.run.app/privacy.html` |
| **App Type** | App (Tools / Game Companion) |
| **Monetization / Pricing** | Free (No Ads, No In-App Purchases) |
| **Target Android SDK** | API 36 (Android 16 / 15 compatible, Min SDK 24 / Android 7.0+) |

---

## 2. Resolving "Get this app from Play" (Play Protect Warning)

If you sideloaded an APK directly onto your phone and encountered a Google Play screen stating:
> *"Get this app from Play: The app installed on your device is not recognized and could harm your device. To continue using this app, search for it on Google Play."*

### Why This Happens:
Google Play Protect scans apps installed from outside the Play Store. If an app with a unique package name (`com.tarkovradar.app`) is sideloaded before it has been registered or published on the Google Play Console under your account, Play Protect flags it as an "unrecognized app".

### How to Resolve:
1. **Official Distribution (Recommended)**:
   - Create an **Internal Testing track** in Google Play Console (see Section 6).
   - Add your own Google Account email to the testers list.
   - Install the app via the Play Store test link. Google Play Protect will immediately recognize the signature and app identity without warnings.
2. **On-Device Bypass During Development / Direct Sideloading**:
   - When installing the APK, tap **"More details"** $\rightarrow$ **"Install anyway"**.
   - If Play Protect blocks launching, open **Google Play Store** $\rightarrow$ Tap your profile icon $\rightarrow$ **Play Protect** $\rightarrow$ Settings gear (top right) $\rightarrow$ temporarily toggle off **"Scan apps with Play Protect"** while testing sideloaded builds.

---

## 3. Building & Exporting the Release Bundle (`.aab`)

Google Play requires the **Android App Bundle (`.aab`)** format for all new releases.

### Method A: Export directly from AI Studio
1. In the top-right header or project settings menu of AI Studio, select **Export / Download**.
2. Select **Download AAB (Android App Bundle)** or **Download APK**.
3. The generated release artifact is signed and ready for upload to Google Play Console.

### Method B: Build via Gradle
To build the release bundle from the terminal:
```bash
gradle :app:bundleRelease
```
The compiled bundle will be located at:
`app/build/outputs/bundle/release/app-release.aab`

To compile a test release APK:
```bash
gradle :app:assembleRelease
```
Output: `app/build/outputs/apk/release/app-release.apk`

---

## 4. Keystore & App Signing

Google Play Console uses **Play App Signing** by default.
- Google manages your app's final delivery key in their secure key vault.
- When you upload your first `.aab`, Google Play registers your upload certificate.
- For local builds, signing is configured in `app/build.gradle.kts` with `my-upload-key.jks` or environment variables `KEYSTORE_PATH`, `STORE_PASSWORD`, `KEY_PASSWORD`, and `keyAlias`.
- **Important**: Keep a secure backup of any custom keystore file you use to sign releases.

---

## 5. Google Play Console Setup Checklist

Follow these steps in the [Google Play Console](https://play.google.com/console):

### Step 1: Create the App
1. Click **Create app**.
2. **App name**: `TarkovRadar`
3. **Default language**: English (United States) - `en-US`
4. **App or game**: `App`
5. **Free or paid**: `Free`
6. Accept the declarations and click **Create app**.

---

### Step 2: Complete "Set up your app" (App Content Declarations)

Navigate to **Policy and programs** $\rightarrow$ **App content** on the left menu:

| Section | Required Selection / Input | Details |
| :--- | :--- | :--- |
| **Privacy Policy** | **Option 1 (GitHub Pages):** `https://<user>.github.io/<repo>/`<br>**Option 2 (GitHub file):** `https://github.com/<user>/<repo>/blob/main/PRIVACY_POLICY.md`<br>**Option 3 (Direct):** `https://ais-pre-3da7mm66phssvwyi2vytqa-628863764922.us-west2.run.app/privacy.html` | See detailed 3-step GitHub setup below. |
| **App Access** | **"All functionality is available without special access"** | The app does not require a login, subscription, or credentials. |
| **Ads** | **"No, my app does not contain ads"** | TarkovRadar is 100% ad-free. |
| **Content Rating** | Complete the IARC questionnaire: | Select **Utility, Productivity, Communication, or Other** (or Reference/Companion). Declare: No violence in the app itself (game reference text and charts only), no profanity, no user-generated content, no physical location sharing. Expected rating: **Everyone** / **Teen** (13+). |
| **Target Audience & Content** | Target age group: **13 and older** (13–15, 16–17, 18+). | Under "Appeal to children", select **"No"** (app is not designed for children). |
| **News Apps** | **"No"** | Not a news app. |
| **COVID-19 Contact Tracing** | **"No"** | Not a contact tracing app. |
| **Data Safety** | Data collection answer: **"No"** | Select **"No, the app doesn't collect or share any of the required user data types"**. All checklists, raid notes, and map markers are stored exclusively on-device in a local Room SQLite database. No analytics, tracking, or network telemetry SDKs are used. |
| **Government Apps** | **"No"** | Not a government entity. |
| **Financial Features** | **"My app does not provide any financial features"** | No monetary transactions. |

#### How to host your Privacy Policy on your public GitHub Repository:
1. **Option A: Free GitHub Pages (Best & Most Professional)**
   - Push your code to your public GitHub repository. (We have already placed `docs/index.html` and `docs/privacy.html` in the repository).
   - In your GitHub repository, click **Settings** (top tab) $\rightarrow$ **Pages** (left sidebar under "Code and automation").
   - Under **Build and deployment** $\rightarrow$ **Branch**:
     - Select branch: `main` (or `master`)
     - Select folder: `/docs`
     - Click **Save**.
   - Within 1–2 minutes, GitHub will publish your live website at:
     `https://<your-github-username>.github.io/<your-repo-name>/`
   - Paste that URL into Google Play Console's Privacy Policy field.

2. **Option B: GitHub Markdown File Link**
   - We have already placed `PRIVACY_POLICY.md` in the root of the repository.
   - When pushed to GitHub, navigate to `PRIVACY_POLICY.md` in your browser.
   - Copy the browser URL:
     `https://github.com/<your-github-username>/<your-repo-name>/blob/main/PRIVACY_POLICY.md`
   - Paste that URL directly into Google Play Console. (Note: Never use the `raw.githubusercontent.com` raw text link; always use the `github.com/.../blob/...` link so Google Play's verification bot renders the page with full formatting).

---

## 6. Store Listing Copy & Metadata

Navigate to **Grow** $\rightarrow$ **Store presence** $\rightarrow$ **Main store listing**.

### App Title (Max 30 characters)
```text
TarkovRadar
```

### Short Description (Max 80 characters)
```text
Offline interactive maps, ballistics charts, extraction & loot guide.
```

### Full Description (Formatted for Google Play)
```text
TarkovRadar is an offline tactical companion and interactive map guide for Escape from Tarkov. Designed for PMC operators and Scavs who need instant, distraction-free raid intelligence without lag or intrusive ads.

KEY FEATURES:

🗺️ HIGH-RESOLUTION INTERACTIVE MAPS
• Multi-floor navigation with seamless level switching
• Customs, Woods, Interchange, Reserve, Streets of Tarkov, Shoreline, Factory, Ground Zero, Lighthouse, and The Lab
• Instant filtering for PMC & Scav extracts, loose loot, safes, weapon boxes, medical supplies, and keycard doors

🎯 BALLISTICS & AMMO PENETRATION TABLE
• Comprehensive armor class penetration effectiveness (Class 1 through Class 6)
• Penetration power, raw flesh damage, and armor damage stats for all calibers
• Instant search by caliber or cartridge name

🔑 KEYS & KEYCARD COMPANION
• Complete key tier list with lock locations and high-value loot spawns
• Marked room guides and keycard requirements
• Value-to-slot efficiency rankings

📋 RAID EXTRACTION CHECKLIST
• Live extraction condition tracking (flare requirements, friendly scav, rubles, paracord/RR)
• Offline raid planner with custom note-taking and extraction checklists

🔒 100% PRIVACY & OFFLINE CAPABILITY
• Zero telemetry, zero tracking, and no external analytics
• Works completely offline inside the raid without requiring an internet connection
• Clean, battery-efficient dark tactical theme

DISCLAIMER:
TarkovRadar is an unofficial fan-made companion application. It is not affiliated with, endorsed, sponsored, or specifically approved by Battlestate Games. All Escape from Tarkov trademarks and visual assets belong to their respective owners.
```

---

## 7. Graphic Assets Specifications

| Asset | Dimensions | Format | Requirements |
| :--- | :--- | :--- | :--- |
| **App Icon** | `512 x 512 px` | 32-bit PNG | Up to 1024 KB. Transparent background not allowed on Play Store. |
| **Feature Graphic** | `1024 x 500 px` | JPEG or 24-bit PNG | No alpha channel. Showcase radar grid, tactical map preview, or logo. |
| **Phone Screenshots** | Min 2, Max 8 | PNG or JPEG | 16:9 or 9:16 aspect ratio (e.g., `1080 x 2400 px`). Capture the Map screen, Ballistics table, and Keys screen. |

---

## 8. Release Tracks & Publishing Workflow

### Recommended Track: Internal Testing First
1. Go to **Testing** $\rightarrow$ **Internal testing**.
2. Click **Create new release**.
3. Upload `app-release.aab`.
4. Enter Release notes:
   ```text
   TarkovRadar 4.0:
   - Updated offline interactive maps with floor selectors
   - Ballistics penetration table with quick search
   - Key tier lists and raid extraction conditions
   - Zero telemetry and full offline local database support
   ```
5. Click **Next** $\rightarrow$ **Save and publish**.
6. Under the **Testers** tab, create an email list with your Google email (`kthuang09@gmail.com`) and copy the **Join on Android** invitation link.
7. Open the link on your Android phone and install directly through Google Play to test on your real device.

### Production Release
When ready for public availability:
1. Go to **Release** $\rightarrow$ **Production**.
2. Click **Create new release** (or promote your release directly from Internal Testing).
3. Review warnings (ensure Data Safety and Privacy Policy are green).
4. Click **Start rollout to Production**.

---

## 9. Version Bumping Procedure for Future Updates

Whenever you make changes to TarkovRadar and want to upload a new release to Google Play Console:

1. Open `app/build.gradle.kts`.
2. Locate the `defaultConfig` block:
   ```kotlin
   defaultConfig {
       applicationId = "com.tarkovradar.app"
       minSdk = 24
       targetSdk = 36
       versionCode = 5        // <-- INCREMENT BY 1 (e.g. 4 -> 5)
       versionName = "5.0"    // <-- UPDATE TO MATCH (e.g. "4.0" -> "5.0")
       ...
   }
   ```
3. Update `app/src/main/java/com/example/ui/components/PrivacyPolicyDialog.kt` and `public/privacy.html` if the displayed version label changes.
4. Build the new release bundle:
   ```bash
   gradle :app:bundleRelease
   ```
5. Upload the new `.aab` to Google Play Console.
