package net.adamgoodridge.sequencetrackplayer.feeder.trackcontrol;

public enum TrackAction {
    NEXT, PREVIOUS,UNSUPPORTED_ACTION;


    public static TrackAction fromString(String value) {
        try {
            return TrackAction.valueOf(value.toUpperCase()); // Handles case-insensitivity
        } catch (IllegalArgumentException e) {
            return UNSUPPORTED_ACTION; // Default value
        }
    }
}
