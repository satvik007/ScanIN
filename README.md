# Sample OpenCV NDK integration app:

To setup the same project in your system first download the opencv sdk from this link:

https://sourceforge.net/projects/opencvlibrary/files/4.2.0/opencv-4.2.0-android-sdk.zip/download

Extract the above zip file.

Then open the `ScanIN/ScanIN` folder in Android Studio and change the following files:

1. In file `local.properties` change the `sdk.dir` to the Android Sdk location on your system.

2. If the `ScanIN/ScanIN/gradle.properties` doesn't exist create it. 

3. Add the following settings in your `gradle.properties` file:

```
org.gradle.jvmargs=-Xmx2048m
android.useAndroidX=true
android.enableJetifier=true
opencvsdk=/home/kaushal/Android/opencv-4.2.0-android-sdk/OpenCV-android-sdk
```

4. Change the `opencvsdk` to such that the path `{opencvsdk}/sdk` points to the OpenCV `sdk` folder. For eg. if your OpenCV SDK is located at `/home/kaushal/Android/opencv-4.2.0-android-sdk/OpenCV-android-sdk/sdk` then:

```
opencvsdk=/home/kaushal/Android/opencv-4.2.0-android-sdk/OpenCV-android-sdk
```
