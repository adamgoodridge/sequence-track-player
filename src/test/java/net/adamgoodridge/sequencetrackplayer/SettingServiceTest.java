package net.adamgoodridge.sequencetrackplayer;
import net.adamgoodridge.sequencetrackplayer.settings.Setting;
import net.adamgoodridge.sequencetrackplayer.settings.SettingRepository;
import net.adamgoodridge.sequencetrackplayer.settings.SettingService;
import net.adamgoodridge.sequencetrackplayer.settings.SettingArrayRepository;
import net.adamgoodridge.sequencetrackplayer.settings.SettingName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SettingServiceTest {

    @Mock
    private SettingRepository settingRepository;

    @Mock
    private SettingArrayRepository settingArrayRepository;

    private SettingService settingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        //todo
        when(settingRepository.findSettingByNameEquals(any())).thenReturn(new Setting("test", "value"));
        settingService = new SettingService(settingRepository, settingArrayRepository);
    }

    @Test
    void saveSettings_DeletesOldAndSavesNew() {
        // Given
        List<Setting> settings = Arrays.asList(
            new Setting("test1", "value1"),
            new Setting("test2", "value2")
        );

        // When
        settingService.saveSettings(settings);

        // Then
        verify(settingRepository).deleteAll();
        verify(settingRepository).saveAll(settings);
    }

    @Test
    void getBoolean_ReturnsTrueForTrueValue() {
        // Given
        Setting mockSetting = new Setting("is_scanning", "true");
        when(settingRepository.findSettingByNameEquals("is_scanning")).thenReturn(mockSetting);

        // When
        boolean result = settingService.getBoolean(SettingName.IS_SCANNING);

        // Then
        assertTrue(result);
    }

    @Test
    void saveBoolean_UpdatesSettingValue() {
        // Given
        Setting mockSetting = new Setting("is_scanning", "false");
        when(settingRepository.findSettingByNameEquals("is_scanning")).thenReturn(mockSetting);

        // When
        settingService.saveBoolean(SettingName.IS_SCANNING, true);

        // Then
        verify(settingRepository).save(any(Setting.class));
    }

    @Test
    void preferredDateAndTime_WithThisHour_ReturnsCurrentHour() {
        // Given
        Setting mockSetting = new Setting("hour_of_day", "This Hour");
        when(settingRepository.findSettingByNameEquals("hour_of_day")).thenReturn(mockSetting);
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.get(Calendar.HOUR_OF_DAY)).thenReturn(12);
        // When
        int result = settingService.preferredDateAndTime();
        // Then
        assertEquals(12, result);
    }

    @Test
    void preferredDayOfWeek_WithWildcard_ReturnsWildcard() {
        // Given
        Setting mockSetting = new Setting("day_of_week", "*");
        when(settingRepository.findSettingByNameEquals("day_of_week")).thenReturn(mockSetting);

        // When
        String result = settingService.preferredDayOfWeek();

        // Then
        assertEquals("*", result);
    }

    @Test
    void getSettings_ReturnsAllSettings() {
        // Given
        List<Setting> expectedSettings = Arrays.asList(
            new Setting("setting1", "value1"),
            new Setting("setting2", "value2")
        );
        when(settingRepository.findAll()).thenReturn(expectedSettings);

        // When
        List<Setting> result = settingService.getSettings();

        // Then
        assertEquals(expectedSettings, result);
        verify(settingRepository).findAll();
    }
}