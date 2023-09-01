#!/bin/bash

while true; do
  read -rp "Input Scan Data: " scanData
  adb shell am broadcast -a eomomniops.RECVR --es com.motorolasolutions.emdk.datawedge.data_string "$scanData" --es com.motorolasolutions.emdk.datawedge.label_type "LABEL-TYPE-UPC"
  printf '\n'
done
