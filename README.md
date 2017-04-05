A repo to repro instabug issue: https://github.com/Instabug/instabug-cordova/issues/16

Steps to reproduce:
- Open the project in Android Studio: `insta_test/platforms/android`
- Edit `baba.html:18` to include your App Id, where it says "\<redacted\>":
https://github.com/travelbank/instabug_bug/blob/master/insta_test/platforms/android/assets/www/baba.html
- Run the app, you should see a picture of a cat.
- Hit the Instabug icon, then hit "Report a Problem"
- Notice the blank screen.
