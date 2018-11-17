# Utility

First some warnings:

1. Usage of this code is at your own risk!.
3. Some permissions are not requested and need to be granted in the App Settings.
4. I use this app for testing new functionalities, so not everything will be working.

## Some things this app does:

1. Has a continuous barcode reader (click to copy).
2. Open Street Map browser with routing.
3. Device Info
4. Very basic file browser, with image viewer, and video viewer
5. GPS info
6. QR Code generator (insert text, click on image to save)
7. Random quote either from file, or API.
8. Sensor information.
9. Android version, names and codes included
10. WiFi scanner (but seems to only work on some devices)
12. Open Street maps browser.
13. Car dock (takes a photo every x seconds, and routes to a location)
14. Scrum Poker
15. Camera f stop scales
16. Image Downloader
17. Latest XKCD comic
18. Basic drawer (shows coords and pressure/size)
19. Attempt at a knowledge game (but it sometimes fails)
20. The POC screens are for testing, they change quite often
21. A map browser for points (draw many points on the device) (tested up to around 150 000 points on a phone)
22. A very basic launcher.

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
      >- utility.json -  a list of locations to be shown on the map, at some point the app will generate a sample (this should just be ignored if this isn't there)
      >- lists/
          |
          >- file.json - a list of strings, at some point the app will generate a sample
```

`/sdcard/utility/area.map`

This can be any file from: <http://download.mapsforge.org>

`/sdcard/utility/area.gz`

The routing comes from <https://github.com/graphhopper/graphhopper> but follow the following bash script:

*This script is setup for South Africa, but minor changes are required to make it work on other countries.*

```bash
set -e

TOOLS="graphhopper-web-0.11.0.jar"

wget "http://central.maven.org/maven2/com/graphhopper/graphhopper-web/0.11.0/$TOOLS" -O $TOOLS &
wget "https://download.geofabrik.de/africa/south-africa-latest.osm.pbf" -O area.osm.pbf &
wget "http://download.mapsforge.org/maps/v5/africa/south-africa-and-lesotho.map" -O area.map &
wait

cat - > config.yml <<EOF
graphhopper:
  datareader.file: area.osm.pbf
  graph.flag_encoders: car,foot|turn_costs=true
  prepare.ch.weightings: fastest
  prepare.min_network_size: 1
  prepare.min_one_way_network_size: 1
  routing.non_ch.max_waypoint_distance: 1000000
  graph.dataaccess: RAM_STORE
server:
  applicationConnectors:
  - type: http
    port: 8989
    # for security reasons bind to localhost
    bindHost: localhost
  requestLog:
      appenders: []
  adminConnectors:
  - type: http
    port: 8990
    bindHost: localhost
EOF

java -Xmx4000m -Xms4000m -server \
  -Dgraphhopper.graph.location="area" \
  -jar "$TOOLS" \
  import config.yml

cd area; zip -r ../area.ghz *

rm config.yml
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

`/sdcard/utility/utility.json`

```json
{
  "locations": [
    {
      "name": "Location",
      "latitude": -33.1,
      "longitude": 18.1
    }
  ],
  "imageUrls": [
    {
      "title": "Cape Town Tide Information",
      "url": "https://www.tide-forecast.com/tides/Cape-Town-South-Africa.png"
    }
  ],
  "launcher": {
    "fave": [
      "com.google.android.dialer",
      "com.android.chrome",
      "com.whatsapp"
    ],
    "hide": [
      "zz.utility"
    ]
  }
}

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