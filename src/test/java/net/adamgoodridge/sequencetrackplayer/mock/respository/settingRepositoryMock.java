package net.adamgoodridge.sequencetrackplayer.mock.respository;

import net.adamgoodridge.sequencetrackplayer.settings.Setting;
import net.adamgoodridge.sequencetrackplayer.settings.SettingRepository;

import static org.mockito.Mockito.when;

public class settingRepositoryMock {
    public static SettingRepository get() {
        SettingRepository settingRepository = org.mockito.Mockito.mock(SettingRepository.class);
        // Setup default mocked settings
        Setting mockDayOfWeek = new Setting("day_of_week", "Monday");
        Setting mockHourOfDay = new Setting("hour_of_day", "12");
        Setting mockIsScanning = new Setting("is_scanning", "true");

        when(settingRepository.findSettingByNameEquals("day_of_week")).thenReturn(mockDayOfWeek);
        when(settingRepository.findSettingByNameEquals("hour_of_day")).thenReturn(mockHourOfDay);
        when(settingRepository.findSettingByNameEquals("is_scanning")).thenReturn(mockIsScanning);

        return settingRepository;
    }
}
