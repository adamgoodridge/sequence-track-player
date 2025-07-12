package net.adamgoodridge.sequencetrackplayer.settings;

import java.util.stream.*;


/**
 * This enum represents the different settings that can be configured in the application.
 * Each setting has a default value associated with it.
 */

public enum SettingName {

    SILENCE_LENGTH ("0"),
    PREFERRED_DAY_OF_WEEK("*"),
    PREFERRED_HOUR_OF_DAY("*"),
    IS_SCANNING  (Boolean.toString(false)),
    CALENDAR_VIEW ("true"),
    LOGO_VIEW ("true"),
    //get a random feed from current audio feeders
    RANDOM_FROM_CURRENT (Boolean.toString(false)),
    //when browser from feed, it will override that feed id
    OVERWRITE_FEED("true"),
    REGULARLY_CHANGE_TO_RANDOM("true"),
    TRACK_CURRENT_COUNT("50");
    private final String defaultValue;

    private SettingName(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public static Stream<SettingName> stream() {
        return Stream.of(SettingName.values());
    }
    public boolean isBoolean() {
        return this.defaultValue.equals("true") || this.defaultValue.equals("false");
    }
}
