#!/bin/bash

adb shell am start -W -a android.intent.action.VIEW -d "nfs://orders_pickup" com.nextuple.nsf.dev

# edit the URI to change which screen you want to deep link too
# edit the package name if you are testing with a different build flavor
# package should match applicationId in build.gradle