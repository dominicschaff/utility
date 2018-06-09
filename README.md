# Utility

First some warnings:

1. Usage of this code is at your own risk!.
2. This app records all sorts of data, but it never leaves the device.
3. Some permissions are not requested and need to be granted in the App Settings.
4. I use this app for testing new functionalities, so not everything will be working.

## Some things this app does:

1. Has a continuous barcode reader (click to copy).
    * Scanning a WiFi barcode will attempt adding it to your WiFi.
2. A book library manager, it only sort of works, so use with caution.
3. Device Info
4. Very basic file browser, with image viewer
5. GPS info
6. Keypad (testing a layout)
7. QR Code generator (insert text, click on image to save)
8. Random quote (there are a few thousand)
9. Sensor information.
10. Random Data
11. Android version, names and codes included
12. WiFi scanner
13. Test if you device supports a second display
14. Open Street maps browser.
    Some files are required to be on your device:

```
/sdcard/
  |
  >- utility/
      |
      >- area.map - this can be taken from a mapsforge server, just rename the file. (the app will exit the map viewer)
      >- area.gz - this is a compiled graphhopper routes, for car and foot traffic. (without this the app won't do routing)
      >- quotes.json - a list of quotes, at some point the app will generate a sample. (without this the app might crash)
      >- locations.json -  a list of locations to be shown on the map, at some point the app will generate a sample (this should just be ignored if this isn't there)
      >- lists/
          |
          >- file.json - a list of strings, at some point the app will generate a sample
```

## SPY activity

This app records phone call info (number, duration) and SMSes. I also try recording other data but have not had much success yet.

The file is stored in `/sdcard/log.json` - be warned this file can get very big

## Scrum Poker cards

These images were used from <https://github.com/redbooth/scrum-poker-cards>.

I believe I can use them.

And the 0 and 1/2 cards from <https://github.com/skseeker/scrum-poker-cards>