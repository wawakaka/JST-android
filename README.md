# JST-android
# Before you run this app

- You need to run this app https://github.com/wawakaka/jst-server

- You need to edit this file `/app/gradle.properties`

```
base_url_dev="your server url for development purpose"
base_url_admin="your server url for development purpose"
base_url_prod="your server url for development production"
cdn_cloud_name="your cloudinary name"
cdn_api_key="your cloudinary api key"
cdn_api_secret="your cloudinary api secret"
```
you need to register to cloudinary to use their api

- To build apk you need to use this command<br />
  for development `./gradlew assembleDevelopmentDebug`<br />
  for development `./gradlew assembleDevelopmentAdmin`<br />
  for production `./gradlew assembleDevelopmentRelease`<br />
  * notes `./gradlew` only for linux environtment only. You need to use `gradlew` for windows envorontment or you can using git bash terminal in windows to be able to use linux command in windows
  
