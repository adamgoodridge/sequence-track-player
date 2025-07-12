package net.adamgoodridge.sequencetrackplayer;
import net.adamgoodridge.sequencetrackplayer.constanttext.*;
import net.adamgoodridge.sequencetrackplayer.exceptions.errors.*;
import net.adamgoodridge.sequencetrackplayer.mock.respository.*;
import net.adamgoodridge.sequencetrackplayer.settings.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.mockito.*;
import org.springframework.beans.factory.annotation.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SettingServiceTests extends AbstractSpringBootTest {
    @Autowired
    private SettingRepository settingRepository;
    @Autowired
    private SettingService settingService;
    @Autowired
    private SettingArrayRepository settingArrayRepository;

    @BeforeEach
    void setUp() throws IOException {
        //MockitoAnnotations.openMocks(this);
        // Reset the mock to clear any previous interactions
        //reset(settingRepository);
        new SettingRepositoryMock().fillWithMockData(settingRepository);

    }
    @Test
    void getAllSettings_ReturnsAllSettings() {
        // When
        List<Setting> settings = settingService.getSettings();

        // Then
        assertNotNull(settings);
        assertFalse(settings.isEmpty(), "Settings should not be empty");
        assertEquals(SettingName.values().length, settings.size(), "Number of settings should match SettingName enum count");
    }

    //todo
    // Define parameters for parameterized tests
    private static Stream<Arguments> booleanSettingParameters() {
        return Stream.of(Arguments.of(SettingName.IS_SCANNING, false),
                Arguments.of(SettingName.LOGO_VIEW, false)
                    /*     Arguments.of(SettingName.CALENDAR_VIEW, true),
                            Arguments.of(SettingName.RANDOM_FROM_CURRENT, false),
                            Arguments.of(SettingName.OVERWRITE_FEED, true),
                            Arguments.of(SettingName.REGULARLY_CHANGE_TO_RANDOM, true)*/
        );
}

    @ParameterizedTest
    @MethodSource("booleanSettingParameters")
    void getBoolean_ReturnsTrueTrueValue(SettingName settingName, boolean expectedValue) {
        // When
        boolean result = settingService.getBoolean(settingName);

        // Then
        assertEquals(expectedValue, result);

    }

    @Test
    void getPreferredRandomSettings_ReturnsDefaultValues() {
        System.out.println(settingRepository.findAll());
          // When


        PreferredRandomSettings result = settingService.getPreferredRandomSettings();

        // Then
        assertEquals("*", result.getDay());
        assertEquals(-1, result.getTime());
        assertTrue(result.isRegularlyTrackChange());
        assertEquals(50, result.getTrackCurrentCount());
    }


    @Test
    void preferredDateAndTime_WithThisHourSetting_ReturnsCurrentHour() {
        // Given
        Calendar mockCalendar = mock(Calendar.class);
        when(mockCalendar.get(Calendar.HOUR_OF_DAY)).thenReturn(14); // 2 PM

        try (MockedStatic<Calendar> calendarMockedStatic = mockStatic(Calendar.class)) {
            calendarMockedStatic.when(Calendar::getInstance).thenReturn(mockCalendar);

            // Set the setting to "this hour"
            List<Setting> settings = settingService.getSettings();
            settings.removeIf(s -> s.getName().equals(SettingName.PREFERRED_HOUR_OF_DAY.toString().toLowerCase()));
            Setting setting = new Setting(SettingName.PREFERRED_HOUR_OF_DAY.toString().toLowerCase(),
                    ConstantText.SETTINGS_VALUE_TIME_OF_DAY_THIS_HOUR);
            settings.add(setting);

            settingService.saveSettings(settings);

            // When
            int result = settingService.getPreferredRandomSettings().getTime();

            // Then
            assertEquals(14, result);

            // Verify Calendar.getInstance was called
            calendarMockedStatic.verify(Calendar::getInstance);
            verify(mockCalendar).get(Calendar.HOUR_OF_DAY);
        }
    }
    @Test
    void saveSettings_ThrowsAnErrorWhenDoesntSaveAllSettings() {
        // Given
            // Set the setting to "this hour"
            Setting setting = new Setting(SettingName.PREFERRED_HOUR_OF_DAY.toString().toLowerCase(),
                    ConstantText.SETTINGS_VALUE_TIME_OF_DAY_THIS_HOUR);

        // When: GenerateFeed is initialized and process is called
        InvalidOperationError error = assertThrows(InvalidOperationError.class, () -> {
            settingService.saveSettings(Collections.singletonList(setting));
        });

        // Then: Exception with expected error message should be thrown
        String expectedPath = "You must provide all settings to save them.";
        assertTrue(error.getMessage().contains(expectedPath));
    }


    @Test
    void setBoolean_ReturnsSameResult() {
        // Given
        SettingName settingName = SettingName.IS_SCANNING;
        // Then
        settingService.deleteAndReset();
        settingService.saveBoolean(settingName, true);

        boolean result = settingService.getBoolean(settingName);
        assertEquals(true, result);

    }
    @Test
    void setBoolean_ShouldThrowException_WhenNoOtherSettingsisThrow() {
        // Given
        SettingName settingName = SettingName.IS_SCANNING;
        settingRepository.deleteAll();

        // When
        JsonNotFoundError error = assertThrows(JsonNotFoundError.class, () ->
                settingService.saveBoolean(settingName, true)
        );

        // Then
        assertEquals("Setting not found: " + settingName, error.getMessage());

    }

    @Test
    void defaults_CreatesDefaultSettings_WhenSettingsDoNotExist() {
        // Given
        settingRepository.deleteAll(); // Clear existing settings


        // Create a new service to trigger defaults() method
        new SettingService(settingRepository, settingArrayRepository);

        // Then
        // Verify each default setting was created
        for (SettingName settingName : SettingName.values()) {
            Setting setting = settingRepository.findSettingByNameEquals(settingName.toString().toLowerCase()).get();
            assertNotNull(setting, "Setting should not be null: " + settingName);
            assertEquals(settingName.getDefaultValue(), setting.getValue(), "Default value should match for: " + settingName);
        }
    }
}
