package net.adamgoodridge.sequencetrackplayer.settings;

import lombok.*;

import java.util.*;
@Data
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

	public List<Setting> toSettingsRows() {
		//every new setting new to be added
		List<Setting> settings = new LinkedList<>();
		settings.add(new Setting(SettingName.SILENCE_LENGTH, silenceLength));
		settings.add(new Setting(SettingName.IS_SCANNING, scanning));
		settings.add(new Setting(SettingName.CALENDAR_VIEW, calendarView));
		settings.add(new Setting(SettingName.RANDOM_FROM_CURRENT, randomFromCurrent));
		settings.add(new Setting(SettingName.LOGO_VIEW, logoView));
		settings.add(new Setting(SettingName.PREFERRED_DAY_OF_WEEK, dayOfWeek));
		settings.add(new Setting(SettingName.PREFERRED_HOUR_OF_DAY, hourOfDay));
		settings.add(new Setting(SettingName.OVERWRITE_FEED, overwriteFeed));
		settings.add(new Setting(SettingName.REGULARLY_CHANGE_TO_RANDOM, regularlyChangeToRandom));
		settings.add(new Setting(SettingName.TRACK_CURRENT_COUNT, trackCurrentCount));
		return settings;
	}

}
