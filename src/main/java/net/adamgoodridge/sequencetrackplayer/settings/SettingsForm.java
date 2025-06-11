package net.adamgoodridge.sequencetrackplayer.settings;

import java.util.*;

public class SettingsForm {
	private String silenceLength;
	
	private String dayOfWeek;
	private String hourOfDay;
	
	private Boolean scanning;
	private Boolean calendarView;
	private Boolean logoView;
	
	private Boolean randomFromCurrent;
	private Boolean overwriteFeed;
	private Boolean regularlyChangeToRandom;
	private String trackCurrentCount;
	public SettingsForm() {
	}
	
	@SuppressWarnings("unused")
	public String getSilenceLength() {
		return silenceLength;
	}
	
	public void setSilenceLength(String silenceLength) {
		this.silenceLength = silenceLength;
	}
	
	@SuppressWarnings("unused")
	public String getDayOfWeek() {
		return dayOfWeek;
	}
	
	public void setDayOfWeek(String dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}
	
	@SuppressWarnings("unused")
	public String getHourOfDay() {
		return hourOfDay;
	}
	
	public void setHourOfDay(String hourOfDay) {
		this.hourOfDay = hourOfDay;
	}
	
	public List<Setting> toSettingsRows() {
		//every new setting new to be added
		List<Setting> settings = new LinkedList<>();
		settings.add(new Setting(SettingName.SILENCE_LENGTH, silenceLength));
		settings.add(new Setting(SettingName.IS_SCANNING, scanning));
		settings.add(new Setting(SettingName.CALENDAR_VIEW, calendarView));
		settings.add(new Setting(SettingName.RANDOM_FROM_CURRENT, randomFromCurrent));
		settings.add(new Setting(SettingName.LOGO_VIEW, logoView));
		settings.add(new Setting(SettingName.DAY_OF_WEEK, dayOfWeek));
		settings.add(new Setting(SettingName.HOUR_OF_DAY, hourOfDay));
		settings.add(new Setting(SettingName.OVERWRITE_FEED, overwriteFeed));
		settings.add(new Setting(SettingName.REGULARLY_CHANGE_TO_RANDOM, regularlyChangeToRandom));
		settings.add(new Setting(SettingName.TRACK_CURRENT_COUNT, trackCurrentCount));
		return settings;
	}
	
	public void setScanning(Boolean scanning) {
		this.scanning = scanning;
	}

	public Boolean getRegularlyChangeToRandom() {
		return regularlyChangeToRandom;
	}

	public void setRegularlyChangeToRandom(Boolean regularlyChangeToRandom) {
		this.regularlyChangeToRandom = regularlyChangeToRandom;
	}

	@SuppressWarnings("unused")
	public String getTrackCurrentCount() {
		return trackCurrentCount;
	}

	@SuppressWarnings("unused")
	public void setTrackCurrentCount(String trackCurrentCount) {
		this.trackCurrentCount = trackCurrentCount;
	}

	@SuppressWarnings("unused")
	public Boolean isScanning() {
		return scanning;
	}
	@SuppressWarnings("unused")
	public Boolean getScanning() {
		return scanning;
	}
	
	@SuppressWarnings("unused")
	public Boolean getCalendarView() {
		return calendarView;
	}
	
	public void setCalendarView(Boolean calendarView) {
		this.calendarView = calendarView;
	}
	@SuppressWarnings("unused")
	public Boolean getLogoView() {
		return logoView;
	}
	
	public void setLogoView(Boolean logoView) {
		this.logoView = logoView;
	}
	@SuppressWarnings("unused")
	public Boolean getRandomFromCurrent() {
		return randomFromCurrent;
	}
	
	public void setRandomFromCurrent(Boolean randomFromCurrent) {
		this.randomFromCurrent = randomFromCurrent;
	}
	@SuppressWarnings("unused")
	public Boolean getOverwriteFeed() {
		return overwriteFeed;
	}
	
	public void setOverwriteFeed(Boolean overwriteFeed) {
		this.overwriteFeed = overwriteFeed;
	}
	
	@Override
	public String toString() {
		return "SettingsForm{" +
				"silenceLength='" + silenceLength + '\'' +
				", dayOfWeek='" + dayOfWeek + '\'' +
				", hourOfDay='" + hourOfDay + '\'' +
				", scanning=" + scanning +
				", calendarView=" + calendarView +
				'}';
	}
}
