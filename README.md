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

# Required files and their contents:

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

`/sdcard/utility/area.map`

This can be any file from: <http://download.mapsforge.org>

`/sdcard/utility/area.gz`

The routing comes from <https://github.com/graphhopper/graphhopper> but follow the following bash script:

Update the `COUNTRY` field based on which place you want from <https://download.geofabrik.de>

```bash
TOOLS="graphhopper-tools-0.10.1-jar-with-dependencies.jar"

COUNTRY="africa/south-africa-latest.osm.pbf"

wget http://central.maven.org/maven2/com/graphhopper/graphhopper-tools/0.10.1/$TOOLS -O $TOOLS
wget "https://download.geofabrik.de/$COUNTRY" -O area.osm.pbf
cat - > config.properties <<EOF
graph.dataaccess = RAM_STORE
graph.flag_encoders = car,foot|turn_costs=true
prepare.ch.weightings = fastest
prepare.min_network_size = 1
prepare.min_one_way_network_size = 1
prepare.minNetworkSize = 1
prepare.minOnewayNetworkSize = 1
routing.non_ch.max_waypoint_distance = 1000000
EOF

java -Xmx4000m -Xms4000m -server -cp "$TOOLS" com.graphhopper.tools.Import config=config.properties graph.location=area datareader.file=area.osm.pbf

cd area; zip -r ../area.ghz *
```



`/sdcard/utility/quotes.json`

```json
[
  {
    "quote": "The quote",
    "author": "The author"
  }
]
```

`/sdcard/utility/locations.json`

```json
[
  {
    "name": "First location",
    "latitude": -31.123,
    "longitude": 20.123
  }
]
```

`/sdcard/utility/lists/file.json`

```json
[
  "Value 1",
  "Value 2"
]
```

## SPY activity

This app records phone call info (number, duration) and SMSes. I also try recording other data but have not had much success yet.

The file is stored in `/sdcard/log.json` - be warned this file can get very big

## Scrum Poker cards

These images were used from <https://github.com/redbooth/scrum-poker-cards>.

I believe I can use them.

And the 0 and 1/2 cards from <https://github.com/skseeker/scrum-poker-cards>