I want to expand the amount of stats collected per day, in addition to the current metrics based day and total second played  in STATISTICS , I would like to include the following:
- Feedname play
- How seconds played per feedname
to provide a response that includes the feedname and the total seconds played for each feedname per day.
-
```json
[
  {
    "date": "2025-03-12",
    "dayOfWeek": "Wednesday",
    "secondsPlayed": 3600,
    "feedStats": [
      {
        "feedName": "Feed A",
        "secondsPlayed": 1800
      },
      {
        "feedName": "Feed B",
        "secondsPlayed": 1200
      },
      {
        "feedName": "Feed C",
        "secondsPlayed": 600
      }
    ]
  }
]
```