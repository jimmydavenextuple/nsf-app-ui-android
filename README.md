# NSF - Store App

## Development Environment Setup

### Required environment variables

Our Gradle build requires certain environment variables to exist in order to sync and build.

IS_LOCAL=true
LOGIN_ENABLED=true
STORE_PASSWORD=
KEY_ALIAS=Nextuple Inc
KEY_PASSWORD=
GOAT_API_LOCAL=
GOAT_API_DEV=https://internal-demo.nextuple.com/services/bff/
GOAT_API_STAGE=
GOAT_API_PROD=

### Emulator

The device types we must support consist of:

| Device Model | OS Version | API Level | Display (in.) | Resolution |
|--------------|------------|-----------|---------------|------------|
| Zebra TC21   | 11         | 30        | 5             | 720 x 1280 |
| Zebra TC52   | 8.1        | 27        | 5             | 720 x 1280 |

#### Emulating a barcode scan

Use `./devTools/emulateScan.sh` to send an emulated scan from DataWedge to the app.

This requires `adb`, which should be included as part of any Android Studio install. To verify that
it works, navigate to the Home screen Order Search component and run the script. The supplied input
should appear in the search field.

## Snapshot Testing using [Shot Library](https://github.com/pedrovgs/Shot)

### Prerequisite: Delete temporary directories after Record/Verify

```
rm -rf app/screenshots/dev/debug/screenshots-compose-default/
rm -rf app/screenshots/dev/debug/screenshots-default/
```

### To Record Expected Snapshots

```
./gradlew devDebugExecuteScreenshotTests -Precord -Dorg.gradle.jvmargs="--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.nio.channels=ALL-UNNAMED --add-exports java.base/sun.nio.ch=ALL-UNNAMED"
```

### To Verify Actual Snapshots

```
./gradlew devDebugExecuteScreenshotTests -Dorg.gradle.jvmargs="--add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.nio.channels=ALL-UNNAMED --add-exports java.base/sun.nio.ch=ALL-UNNAMED"
```

### References

* Known Issue(s)
    * Unable to make public void sun.nio.ch.ChannelInputStream.close() throws java.io.IOException
      accessible: module java.base does not "exports sun.nio.ch" to unnamed module @
        * Fix: https://github.com/pedrovgs/Shot/issues/268