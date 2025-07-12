package net.adamgoodridge.sequencetrackplayer.constanttext;

public class ConstantText {

    public static final String ERROR_RANDOM_TRACK = "After multiple attempts, a random track cannot be gotten!";
    public static final String DEFAULT_SERVER_URL = "https://radioplayer.adsmac.net/";
    public static final String LIVE_STREAM_ENDPOINT = "http://192.168.181.24:8085/api/v1/feeds";
    public static final String LIVE_STREAM_SUMMARY_ENDPOINT = "http://192.168.181.24:8085/api/v1/feeds/summary";
    //setting
    //form options
    public static final String SETTINGS_VALUE_TIME_OF_DAY_THIS_HOUR  = "This Hour";
    public static final String SETTINGS_VALUE_DAY_OF_WEEK_THIS_DAY  = "This weekday";

    public static final int[] SETTINGS_CHOICES_SILENCE_NUMBERS = {0,1,5,10,15,20,40};

    public static final String[] SETTINGS_CHOICES_DAYS_OF_WEEK = {"Saturday","Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};

    public static final int[] SETTINGS_CHOICES_TIMES_OF_DAY = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23};


    public static final String ERROR_PAGE_ATTRIBUTE_HEADING = "heading";
    public static final String ERROR_PAGE_ATTRIBUTE_INFO = "info";

    @SuppressWarnings("unused")
    public static final int[] TRACK_CURRENT_COUNT = {1, 10, 20, 50};

    //feed attributes
    public static final String FEED_ATTRIBUTE_SILENCE = "silence_length";
    public static final String FEED_ATTRIBUTE_CURRENT_FEED = "feedCurrentPlayingInfo";
    public static final String FEED_ATTRIBUTE_IS_SCANNING = "isScanning";


    //needed for calendar
    public static final String ENDING_PATH_IN_YEAR_REGEX = ".*20[\\d][\\d]/$";

    public static final String URL_ENDING_FEED = "/browser/feed/";
    public static final String URL_ENDING_NO_FEED = "/browser/path/";
    private ConstantText() throws IllegalAccessException {
        throw new IllegalAccessException("ConstantText");
    }
    
    
}

