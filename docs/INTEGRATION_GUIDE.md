# MemoTalk SDK - Integration Guide

---

This guide provides step-by-step instructions for integrating the MemoTalk SDK into your Android and iOS applications.

## Android Integration

### 1. Prerequisites

-   Android Studio 2023.2.1 or later
-   Android API level 26 (Android 8.0) or higher
-   Kotlin 1.8.0 or later

### 2. Add Dependency

Add the following line to the `dependencies` block of your app-level `build.gradle.kts` file:

```kotlin
implementation("com.memotalk:sdk:1.0.0")
```

### 3. Permissions

Declare the following permissions in your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.INTERNET" />
```

### 4. Initialization

Initialize the SDK in your `Application` class:

```kotlin
import com.memotalk.sdk.MemoTalkSDK
import com.memotalk.sdk.SDKConfig

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val config = SDKConfig(
            appKey = "YOUR_APP_KEY",
            appSecret = "YOUR_APP_SECRET"
        )
        MemoTalkSDK.initialize(this, config)
    }
}
```

## iOS Integration

### 1. Prerequisites

-   Xcode 14.3 or later
-   iOS 12.0 or later
-   Swift 5.8 or later

### 2. Add Swift Package

In Xcode, go to `File > Add Packages...` and enter the following repository URL:

```
https://github.com/alanwangxiyu-wq/MemoTalk-SDK01.git
```

### 3. Permissions

Add the following keys to your `Info.plist` file:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>We need Bluetooth to connect to your MemoTalk device.</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>We need Bluetooth to connect to your MemoTalk device.</string>
```

### 4. Initialization

Initialize the SDK in your `AppDelegate`:

```swift
import MemoTalkSDK

func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
    let config = SDKConfig(
        appKey: "YOUR_APP_KEY",
        appSecret: "YOUR_APP_SECRET"
    )
    MemoTalkSDK.initialize(config: config)
    return true
}
```
