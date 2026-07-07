## User Story
As a user, I want to record statistics on how many seconds of audio have been played by day, so that I can see how much content has been consumed over time.

## Acceptance Criteria
- The system should record the total number of seconds of audio played each day.
- The system should record the total number of seconds played per feed for each day.
- The system should be able to provide a report of the total seconds of audio played for any given day.
- The system should be able to provide a report that includes the feed name and seconds played per feed for any given day.

## Add seconds to statistics

Statistics are updated via the playback position endpoint. When the current position advances, the delta is added to the day's statistic.
The same delta is also attributed to the currently playing feed for that day.

## Endpoints

### Get statistics

```
GET /api/v1/statistic/
```

Optional query parameters:

| Parameter    | Type   | Description                                                        |
|--------------|--------|--------------------------------------------------------------------|
| `fromDate`   | string | Start date in `YYYY-MM-DD` format (inclusive)                      |
| `toDate`     | string | End date in `YYYY-MM-DD` format (inclusive)                        |
| `days`       | number | Number of last days to return (default: `100`)                     |

**Rules:**
- If no parameters are provided, returns the last 100 days of statistics.
- `fromDate`/`toDate` and `days` are mutually exclusive — providing both returns a `400 Bad Request`.
- If `fromDate` is provided without `toDate`, returns statistics from `fromDate` up to today.
- `toDate` cannot be provided without `fromDate` — returns a `400 Bad Request`.

**Error response (`400 Bad Request`):**

```json
{ "error": "Query parameters 'days' and 'from'/'to' are mutually exclusive. Use either 'days' or a date range (by providing 'from' (optional) and 'to' dates).." }
```

**Response:**

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

### Get statistics for today

```
GET /api/v1/statistic/today
```

Returns `404` if nothing has been played today.

**Response:**

```json
{
  "date": "2026-03-19",
  "dayOfWeek": "Thursday",
  "secondsPlayed": 9000,
  "feedStats": [
    {
      "feedName": "Feed A",
      "secondsPlayed": 5000
    },
    {
      "feedName": "Feed B",
      "secondsPlayed": 4000
    }
  ]
}
```

### Get week summary

```
GET /api/v1/statistic/summary/week
```

Returns one entry per day of the week, accumulating all-time seconds for that day, including per-feed totals.

**Response:**

```json
[
  {
    "name": "Monday",
    "totalSeconds": 3600,
    "percentage": 100,
    "feedStats": [
      {
        "feedName": "Feed A",
        "secondsPlayed": 3000
      },
      {
        "feedName": "Feed B",
        "secondsPlayed": 600
      }
    ]
  }
]
```

### Get month summary

```
GET /api/v1/statistic/summary/month
```

Returns one entry per month, accumulating all-time seconds for that month, including per-feed totals.

**Response:**

```json
[
  {
    "name": "January",
    "totalSeconds": 86400,
    "feedStats": [
      {
        "feedName": "Feed A",
        "secondsPlayed": 52000
      },
      {
        "feedName": "Feed B",
        "secondsPlayed": 34400
      }
    ],
    "weekDayBreakdown": {
      "Monday": {
        "totalSeconds": 3600,
        "percentage": 4.17,
        "feedStats": [
          {
            "feedName": "Feed A",
            "secondsPlayed": 2400
          },
          {
            "feedName": "Feed B",
            "secondsPlayed": 1200
          }
        ]
      },
      "Tuesday": {
        "totalSeconds": 7200,
        "percentage": 8.33,
        "feedStats": [
          {
            "feedName": "Feed A",
            "secondsPlayed": 4500
          },
          {
            "feedName": "Feed B",
            "secondsPlayed": 2700
          }
        ]
      },
      "Wednesday": {
        "totalSeconds": 10800,
        "percentage": 12.5,
        "feedStats": [
          {
            "feedName": "Feed A",
            "secondsPlayed": 6000
          },
          {
            "feedName": "Feed B",
            "secondsPlayed": 4800
          }
        ]
      },
      "Thursday": {
        "totalSeconds": 14400,
        "percentage": 16.67,
        "feedStats": [
          {
            "feedName": "Feed A",
            "secondsPlayed": 9000
          },
          {
            "feedName": "Feed B",
            "secondsPlayed": 5400
          }
        ]
      },
      "Friday": {
        "totalSeconds": 18000,
        "percentage": 20.83,
        "feedStats": [
          {
            "feedName": "Feed A",
            "secondsPlayed": 11200
          },
          {
            "feedName": "Feed B",
            "secondsPlayed": 6800
          }
        ]
      }
    }
  }
]
```