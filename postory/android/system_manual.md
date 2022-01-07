# Postory Android App System Manual

This manual is a guide for running the Postory Android APK. APK is a file format that is used for distribution of Android applications. Our apk is located at _2021SpringGroup9/postory/android/postory.apk_ on master branch.

## Run on your Android smartphone

The apk file should be transferred to your smartphone. This could be done with a USB connection. After the transfer, click on the APK file on your phone. Defaultly, Android smartphones do not install apk files for security reasons.

* If your Android version is recent, it might prompt you to permit installation of unknown apps. After you allow it, the application will be installed and opened.

* If you are not prompted for giving permissions to unknown apps, you may need to do it manually. 

  1- Go to Settings -> Security -> Install Unknown Apps
  2- Allow installation of unknown apps.
  
Note: The steps for giving manual permissions may vary among devices. This is a summary of the process. 

## Run on Emulator

To run an APK on emulator, you need to have Android Studio along with AVD Manager. To see the formal documentation, [click here](https://developer.android.com/studio/run/managing-avds).

After the AVD Manager is successfully installed, an emulator device should be created (or you can use another emulator that you created before)

To create an emulator(you can skip if you have a suitable emulator):

  1- Click on AVD Manager
  
  ![](https://i.imgur.com/3qTrnaf.png)

  
  
  2- Click on Create Virtual Device
  
  ![](https://i.imgur.com/KSf5OVb.png)

  
  3- Pick a device size. Note that the application works best on smartphone ratios.
  
  4- Select a system image. You need to download the image if it does not exist in your system. This might take some time. Note that you should pick an API level in accordance with the limitations stated in README. A higher API level is usually better but you might need to download more data.
  
  
After you have a suitable emulator, run the emulator.

1- Open AVD Manager

2- Run the emulator you want.

![](https://i.imgur.com/1cMMmJL.png)

3- After the emulator is on, drag and drop the apk file onto its screen.

4- The application might start running or you might need to click on the application at the emulator to run it.

For detailed documentation, [click here](https://developer.android.com/studio/run/emulator).


