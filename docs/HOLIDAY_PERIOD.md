# Play Hoiliday Periods

## User Story
**As a listener**
I want to play holiday periods in my sequence track player
**So that** I listen to holiday-themed audio during specific times of the year

## Acceptance Criteria
- **Given:** A user that has clicked play




## Setting

The setting 'holidayPeriodsEnabled' can be toggled to:
- <b>ALWAYS</b>: Holiday periods are always played regardless of the date
- <b>NEVER</b>: Holiday periods are never played
- <b>AUTOMATIC</b>: Holiday periods are played only during specific holiday dates (e.g., December 20 - December 31 for Christmas/New Year)


## Sequence Diagram

```plantuml
@startuml playHolidayPeriods
title Play Holiday Periods
actor User
participant "Frontend" as FE
participant "Backend" as BE
User -> FE : Click play button on a specific feed
FE -> BE : GET /api/play?feedId=<<FEED_ID>>
BE -> BE : Check 'holidayPeriodsEnabled' setting
alt holidayPeriodsEnabled = always or (automatic and current date in holiday range)
    BE -> DB : Fetch an existing holiday period audio track
    alt holiday track found
        DB --> BE : Return feed ID
    else no holiday track found
        BE -> DB : Grenerate new feed based the feed and pick an audio track within holiday period
    end 
    BE -> FE : 200 OK with holiday audio track
else holidayPeriodsEnabled = never or (automatic and current date not in holiday range)
    BE -> BE : Fetch regular audio tracks
end
    BE -> FE : 200 OK with the feed id
```